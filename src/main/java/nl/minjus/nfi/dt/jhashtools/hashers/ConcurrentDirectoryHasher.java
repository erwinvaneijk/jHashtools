/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA. User: eijk Date: May 12, 2010 Time: 12:22:04 PM To change this template use File | Settings
 * | File Templates.
 */
public class ConcurrentDirectoryHasher extends AbstractDirectoryHasher {
    private DirHasherResult result;
    private int maxThreads;

    static enum ProcessingStates {

        UNINITIALIZED,
        BUSY,
        FINISHED
    };

    public ConcurrentDirectoryHasher() {
        super();
        this.result = new DirHasherResult();
        this.maxThreads = 1;
    }

    public ConcurrentDirectoryHasher(String algorithm) throws NoSuchAlgorithmException {
        super(algorithm);
    }

    public ConcurrentDirectoryHasher(int maxThreads) {
        this();
        this.maxThreads = maxThreads;
    }

    public ConcurrentDirectoryHasher(String algorithm, int maxThreads) throws NoSuchAlgorithmException {
        this(algorithm);
        this.maxThreads = maxThreads;
    }

    public int getMaxThreads() {
        return this.maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public DirHasherResult getDigests(File startPath) {
        DirHasherResult result = new DirHasherResult();
        this.updateDigests(result, startPath);
        return result;
    }

    @Override
    public void updateDigests(DirHasherResult digests, File startPath) {
        if (!startPath.exists()) {
            throw new IllegalArgumentException("Path " + startPath + " does not exist");
        }

        Collection<Thread> threads = new LinkedList<Thread>();
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>();

        ProcessingState currentState = new ProcessingState();

        // Fire up the first filewalker, that can already start filling up the queue.
        //FileWalkerTask d = new FileWalkerTask(startPath, queue, currentState);
        //d.call();

        ExecutorService executor = Executors.newFixedThreadPool(this.maxThreads);
        CompletionService<DirHasherResult> completionService = new ExecutorCompletionService<DirHasherResult>(executor);
        Future<DirHasherResult> fileWalkerTask = completionService.submit(new FileWalkerTask(startPath, queue, currentState));
        Collection<Future<DirHasherResult>> computeTasks = new LinkedList<Future<DirHasherResult>>();
        for (int i = 0; i < this.maxThreads; i++) {
            try {
                computeTasks.add(completionService.submit(new FileDigestComputeTask(queue, this.algorithms, currentState)));
            } catch (NoSuchAlgorithmException e) {
                // complain loudly.
            }
        }

        try {
            // first, wait for the filewalkertask to finish.
            if (fileWalkerTask.get() != null) {
                System.err.println("Something went terribly wrong!");
            }

            for (Future<DirHasherResult> task : computeTasks) {
                DirHasherResult partial = task.get();
                if (partial != null) {
                    digests.putAll(partial);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    static class ProcessingState {

        public ProcessingState() {
            this.lock = new ReentrantLock();
            this.currentState = ProcessingStates.UNINITIALIZED;
        }
        public Lock lock;
        public ProcessingStates currentState;
    }

    static class FileWalkerTask implements Callable<DirHasherResult> {

        private File startingPath;
        private DirVisitorTask visitor;
        private ProcessingState processingState;

        public FileWalkerTask(File startingPath, BlockingQueue<File> inputQueue, ProcessingState processingState) {
            this.startingPath = startingPath;
            this.visitor = new DirVisitorTask(inputQueue);
            this.processingState = processingState;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
         * causes the object's <code>run</code> method to be called in that separately executing thread.
         * <p/>
         * The general contract of the method <code>run</code> is that it may take any action whatsoever.
         *
         * @see Thread#run()
         */
        public DirHasherResult call() {
            try {
                this.processingState.lock.lock();
                this.processingState.currentState = ProcessingStates.BUSY;
            } finally {
                this.processingState.lock.unlock();
            }
            FileWalker walker = new FileWalker();
            walker.addWalkerVisitor(visitor);
            walker.walk(this.startingPath);
            try {
                this.processingState.lock.lock();
                this.processingState.currentState = ProcessingStates.FINISHED;
            } finally {
                this.processingState.lock.unlock();
            }

            return null;
        }
    }

    static class FileDigestComputeTask implements Callable<DirHasherResult> {

        private BlockingQueue<File> inputQueue;
        private FileHasher fileHasher;
        private DirHasherResult partialResult;
        private ProcessingState processingState;

        public FileDigestComputeTask(BlockingQueue<File> inputQueue, Collection<MessageDigest> digests, ProcessingState processingState) throws NoSuchAlgorithmException {
            this.inputQueue = inputQueue;
            this.fileHasher = FileHasherCreator.createThreadedHasher();
            try {
                            for (MessageDigest d: digests) {
                                    this.fileHasher.addAlgorithm((MessageDigest)d.clone());
                            }
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
            
            this.partialResult = new DirHasherResult();
            this.processingState = processingState;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         *
         * @throws Exception if unable to compute a result
         */
        public DirHasherResult call() throws Exception {
            try {
                while (true) {
                    try {
                        File file = this.inputQueue.poll(10, TimeUnit.MICROSECONDS);
                        if (file != null) {
                            this.partialResult.put(file, this.fileHasher.getDigest(file));
                        } else if (this.processingState.currentState == ProcessingStates.FINISHED) {
                                break;
                        }
                    } catch (IOException ex) {
                        // pass
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return this.partialResult;
        }
    }
}

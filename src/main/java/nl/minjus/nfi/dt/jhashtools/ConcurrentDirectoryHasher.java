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

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by IntelliJ IDEA. User: eijk Date: May 12, 2010 Time: 12:22:04 PM To change this template use File | Settings
 * | File Templates.
 */
public class ConcurrentDirectoryHasher implements DirectoryHasher {

    private static final int MAX_THREADS = 1;
    private Set<String> algorithms;
    private DirHasherResult result;
    private boolean verbose;

    static enum ProcessingStates {
        UNINITIALIZED,
        BUSY,
        FINISHED
    }

    ;

    public ConcurrentDirectoryHasher() {
        this.result = new DirHasherResult();
        this.algorithms = new HashSet<String>();
        this.verbose = false;
    }

    public ConcurrentDirectoryHasher(String algorithm) throws NoSuchAlgorithmException {
        this();
        this.addAlgorithm(algorithm);
    }

    @Override
    public void addAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        this.algorithms.add(algorithm);
    }

    @Override
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

        List<Thread> threads = new LinkedList<Thread>();
        BlockingQueue<File> queue = new LinkedBlockingQueue<File>();

        ProcessingState currentState = new ProcessingState();

        // Fire up the first filewalker, that can already start filling up the queue.
        FileWalkerTask d = new  FileWalkerTask(startPath, queue, currentState);
        d.call();

        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS + 1);
        CompletionService<DirHasherResult> completionService = new ExecutorCompletionService<DirHasherResult>(executor);
        //Future<DirHasherResult> fileWalkerTask = completionService.submit(new FileWalkerTask(startPath, queue, currentState));
        for (int i = 0; i < MAX_THREADS; i++) {
            try {
                completionService.submit(new FileDigestComputeTask(queue, this.algorithms, currentState));
            } catch (NoSuchAlgorithmException e) {
                // complain loudly.
            }
        }

        try {
            // first, wait for the filewalkertask to finish.
            //if (fileWalkerTask.get() != null) {
            //    System.err.println("Something went terribly wrong!");
            //}

            System.out.println("Getting the results");
            for (int i = 0; i < MAX_THREADS; i++) {
                Future<DirHasherResult> task = completionService.take();
                if (task != null) {
                    DirHasherResult partial = task.get();
                    if (partial != null) {
                        System.out.println("Received " + partial.size() + " digests");
                        for (Map.Entry<File, DigestResult> entry: partial) {
                            System.out.println("\t" + entry.getKey().toString());
                        }
                        digests.putAll(partial);
                    }
                }
            }
        } catch (InterruptedException ignore) {
        } catch (ExecutionException ignore) {
        }

    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public boolean isVerbose() {
        return this.verbose;
    }

    @Override
    public Collection<String> getAlgorithms() {
        return this.algorithms;
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
        @Override
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

        public FileDigestComputeTask(BlockingQueue<File> inputQueue, Set<String> digests, ProcessingState processingState) throws NoSuchAlgorithmException {
            this.inputQueue = inputQueue;
            this.fileHasher = new FileHasher(digests);
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
        @Override
        public DirHasherResult call() throws Exception {
            try {
            while (true) {
                try {
                    File file = this.inputQueue.remove();
                    this.partialResult.put(file, this.fileHasher.getDigest(file));
                } catch (IOException e) {
                    // pass
                } catch (NoSuchElementException ignore) {
                    if (this.processingState.currentState == ProcessingStates.FINISHED) {
                        break;
                    }
                    Thread.sleep(10);           // sleep 10 ms for new results to be put in the queue
                }
            }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return this.partialResult;
        }
    }
}

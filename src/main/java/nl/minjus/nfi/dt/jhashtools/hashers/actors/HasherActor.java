package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Logger;

import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import nl.minjus.nfi.dt.jhashtools.hashers.DigestAlgorithm;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.FileHasherCreator;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;

/**
 * The actor responsible for hashing files.
 * 
 * @author Erwin van Eijk
 * 
 */
public class HasherActor extends Actor {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(HasherActor.class
			.getName());

	private final FileHasher fileHasher;

	public HasherActor(final Collection<DigestAlgorithm> algorithms,
			final Channel<Message> inbox, final Channel<Message> outbox,
			Fiber fiber) throws AlgorithmNotFoundException {
		super(inbox, outbox, fiber);

		try {
			fileHasher = FileHasherCreator.createSimple(algorithms);
		} catch (final NoSuchAlgorithmException e) {
			throw new AlgorithmNotFoundException("Could not find message");
		}
	}

	@Override
	public Message act(final Message request) {
		if (request instanceof FileMessage) {
			final FileMessage message = (FileMessage) request;
			try {
				final DigestResult digest = fileHasher.getDigest(message
						.getFile());
				return new DigestMessage(message.getFile(), digest);
			} catch (final IOException ex) {
				return null;
			}
		} else if (request instanceof StopMessage) {
			return null;
		}
		return null;
	}
}

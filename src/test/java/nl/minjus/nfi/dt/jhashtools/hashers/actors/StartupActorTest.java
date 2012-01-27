package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import kilim.Mailbox;
import kilim.Pausable;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StartupActorTest {

	@Test
	public void testGenerator() {
		File file = new File("testdata");
		final FileNameGenerator generator = new FileNameGenerator(file);
		int count = 0;
		for (final File message : generator) {
			assertThat(message.exists(), is(true));
			count += 1;
		}
		assertThat(count, is(equalTo(13)));
	}

	@Test
	public void testStartupActor() throws Pausable {
		Mailbox<Message> theBox = new Mailbox<Message>();
		File file = new File("testdata");
		StartupActor actor = new StartupActor(file, 1, null, theBox);
		actor.start();
		int num = 0;
		while (true) {
			Message m = theBox.getb();
			if (m instanceof FileMessage) {
				FileMessage fileMessage = (FileMessage) m;
				assertThat(fileMessage, is(not(nullValue())));
				if (fileMessage.isStop()) {
					break;
				}
				File theFile = fileMessage.getFile();
				assertThat(theFile.exists(), is(true));
				num += 1;
			} else if (m instanceof StopMessage) {
				break;
			}
		}
		assertThat(num, is(equalTo(13)));
	}

}

package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;

public class Channels {

	public static final Channel<Message> filenameChannel = new MemoryChannel<Message>();
	public static final Channel<Message> digestChannel = new MemoryChannel<Message>();
	public static final Channel<Message> pathChannel = new MemoryChannel<Message>();
}

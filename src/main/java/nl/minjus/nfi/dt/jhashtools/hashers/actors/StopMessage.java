package nl.minjus.nfi.dt.jhashtools.hashers.actors;

/**
 * This message is used to signal that everything should stop.
 *
 * @author Erwin van Eijk
 *
 */
public class StopMessage implements Message
{

    @Override
    public boolean isStop() {
        return true;
    }

}

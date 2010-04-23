/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kojak
 */
public class ConstructionInfoTest {

    public ConstructionInfoTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetup() {
        ConstructionInfo info = new ConstructionInfo();
        assertNotNull(info.getConstructionDate());
        assertNotNull(info.getOperatingSystem());
        assertNotNull(info.getUserid());
        assertNotNull(info.getUsername());
        requestDelay(10);
        assertTrue(Calendar.getInstance().getTime().after(info.getConstructionDate()));
    }

    @Test
    public void testSetters() {
        ConstructionInfo info = new ConstructionInfo();
        info.setConstructionDate(Calendar.getInstance().getTime());

        info.setOperatingSystem("fooIS");
        assertEquals("fooIS", info.getOperatingSystem());
        info.setUserid("user");
        assertEquals("user", info.getUserid());
        info.setUsername("Username");
        assertEquals("Username", info.getUsername());
        requestDelay(10);
        assertTrue(Calendar.getInstance().getTime().after(info.getConstructionDate()));
    }

    private void requestDelay(int delayInMilisecs) {
        try {
            // insert a delay here. Otherwise, Date.after() will fail sometimes
            Thread.sleep(delayInMilisecs);
        } catch (InterruptedException ex) {
            // We really do not care.
        }
    }

}

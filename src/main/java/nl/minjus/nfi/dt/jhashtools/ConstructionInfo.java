/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Erwin van Eijk
 */
public class ConstructionInfo {

    private String username;

    private String userid;

    private String operatingSystem;

    private Date constructionDate;

    public ConstructionInfo() {
        this.constructionDate = Calendar.getInstance().getTime();
        this.username = System.getProperty("user.name");
        this.operatingSystem = System.getProperty("os.name") + ":" + System.getProperty("os.version");
        // Currently, I do not know how we can get the userid of the currently
        // running user.
        this.userid = "unknown";
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the userid
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the userid to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the constructionDate
     */
    public Date getConstructionDate() {
        return constructionDate;
    }

    /**
     * @param constructionDate the constructionDate to set
     */
    public void setConstructionDate(Date constructionDate) {
        this.constructionDate = constructionDate;
    }

    /**
     * @return the operatingSystem
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * @param operatingSystem the operatingSystem to set
     */
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }


}

/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>. All rights reserved.
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
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of <copyright holder>.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.utils.Version;

import java.util.Calendar;
import java.util.Date;

/**
 * ConstructionInfo holds the information on when the app was run.
 *
 * @author Erwin van Eijk
 */
public class ConstructionInfo
{

    private String username;

    private String versionInformation;

    private String operatingSystem;

    private Date constructionDate;

    /**
     * Constructor.
     */
    public ConstructionInfo()
    {
        this.constructionDate = Calendar.getInstance().getTime();
        this.username = System.getProperty("user.name");
        this.operatingSystem = System.getProperty("os.name") + ":" + System.getProperty("os.version");
        this.versionInformation = Version.getVersion();
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param theUsername the username to set
     */
    public void setUsername(String theUsername)
    {
        this.username = theUsername;
    }

    /**
     * @return the versionInformation
     */
    public String getVersionInformation()
    {
        return versionInformation;
    }

    /**
     * @param theVersionInformation the versionInformation to set
     */
    public void setVersionInformation(String theVersionInformation)
    {
        this.versionInformation = theVersionInformation;
    }

    /**
     * @return the constructionDate
     */
    public Date getConstructionDate()
    {
        return this.constructionDate;
    }

    /**
     * @param theConstructionDate the constructionDate to set
     */
    public void setConstructionDate(Date theConstructionDate)
    {
        this.constructionDate = theConstructionDate;
    }

    /**
     * @return the operatingSystem
     */
    public String getOperatingSystem()
    {
        return operatingSystem;
    }

    /**
     * @param theOperatingSystem the operatingSystem to set
     */
    public void setOperatingSystem(String theOperatingSystem)
    {
        this.operatingSystem = theOperatingSystem;
    }


}

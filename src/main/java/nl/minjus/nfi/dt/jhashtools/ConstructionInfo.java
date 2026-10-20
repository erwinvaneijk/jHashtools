/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package nl.minjus.nfi.dt.jhashtools;

import java.util.Calendar;
import java.util.Date;

import nl.minjus.nfi.dt.jhashtools.utils.Version;

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
    public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param theUsername
     *            the username to set
     */
    public void setUsername(final String theUsername) {
        this.username = theUsername;
    }

    /**
     * @return the versionInformation
     */
    public String getVersionInformation() {
        return versionInformation;
    }

    /**
     * @param theVersionInformation
     *            the versionInformation to set
     */
    public void setVersionInformation(final String theVersionInformation) {
        this.versionInformation = theVersionInformation;
    }

    /**
     * @return the constructionDate
     */
    public Date getConstructionDate() {
        return this.constructionDate;
    }

    /**
     * @param theConstructionDate
     *            the constructionDate to set
     */
    public void setConstructionDate(final Date theConstructionDate) {
        this.constructionDate = theConstructionDate;
    }

    /**
     * @return the operatingSystem
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }

    /**
     * @param theOperatingSystem
     *            the operatingSystem to set
     */
    public void setOperatingSystem(final String theOperatingSystem) {
        this.operatingSystem = theOperatingSystem;
    }

}

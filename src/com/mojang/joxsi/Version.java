package com.mojang.joxsi;

/**
 * This class contains the version information of joXSI.
 *
 * @author Egal
 */
public final class Version
{
    /** joXSI Major Version. */
    private static final int JOXSI_MAJOR_VERSION = 1;
    /** joXSI Minor Version. */
    private static final int JOXSI_MINOR_VERSION = 0;

    /**
     * Do not instantaite this class as it only has staic methods.
     */
    private Version()
    {
    }

    /**
     * Returns the joXSI Major Version.
     *
     * @return the joXSI Major Version
     */
    public static int getJoXSIMajorVersion()
    {
        return JOXSI_MAJOR_VERSION;
    }

    /**
     * Returns the joXSI Minor Version.
     *
     * @return the joXSI Minor Version
     */
    public static int getJoXSIMinorVersion()
    {
        return JOXSI_MINOR_VERSION;
    }

    /**
     * Returns the joXSI Minor Version.
     *
     * @return the joXSI Minor Version
     */
    public static String getJoXSIVersionString()
    {
        return String.valueOf(JOXSI_MAJOR_VERSION) + '.' + String.valueOf(JOXSI_MINOR_VERSION);
    }
}

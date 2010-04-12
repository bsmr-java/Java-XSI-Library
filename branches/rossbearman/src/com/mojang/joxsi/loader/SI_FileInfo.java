package com.mojang.joxsi.loader;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_FileInfo extends Template
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(SI_FileInfo.class.getName());
    public String projectName;
    public String userName;
    public String savedDateTime;
    public String originator;

    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        projectName = (String)it.next();
        userName = (String)it.next();
        savedDateTime = (String)it.next();
        originator = (String)it.next();

        if (logger.isLoggable(Level.FINER))
        {
            logger.finer("FileInfo: " + this);
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + " - projectName: "
            + projectName + ", userName: " + userName
            + ", savedDateTime: " + savedDateTime + ", originator: " + originator;
    }
}

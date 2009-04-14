package com.mojang.joxsi.demo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Encapsulates methods for the retrieval and update of properties associated
 * with the ModelDisplayer.
 * 
 * @author Tribuadore
 */
public class ModelDisplayerProperties
{
    // Properties filename
    private final static String PROPERTIES_FILE = "modeldisplayer.properties";
    
    // Recent XSI models
    private static List<String> recentxsis = null;
    
    /**
     * Prevent public instantiation.
     */
    private ModelDisplayerProperties()
    {   
    }
    
    /**
     * Called internally to do late reading of persisted properties
     */
    private static void initialise()
    {
        if (recentxsis != null)
            return;
        
        Properties properties = new Properties();
        String xsipath = "";
       
        try
        {
            // Read the properties configuration
            FileInputStream file = new FileInputStream(PROPERTIES_FILE);
            properties.load(file);
            file.close();
        }
        catch (FileNotFoundException e)
        {
        }
        catch (IOException e)
        {
        }
        
        // Read the list of recent XSIs
        recentxsis = new ArrayList<String>();
        for (int i = 0; xsipath != null; i++)
        {
            xsipath = properties.getProperty("RECENT_XSI_" + i);
            if (xsipath != null)
                recentxsis.add(xsipath);
        }
    }
    
    /**
     * Called internally to persist the current state of properties to disk
     */
    public static void save()
    {
        initialise();            
        Properties properties = new Properties();
        int recentxsi_num = 0;
        
        for (String xsipath : recentxsis)
            properties.setProperty("RECENT_XSI_" + recentxsi_num++, xsipath);
        
        try
        {
            // Write the properties to disk
            FileOutputStream file = new FileOutputStream(PROPERTIES_FILE);
            properties.store(file, "# Properties file for ModelDisplayer demo");
            file.close();
        }
        catch (FileNotFoundException e)
        {
        }
        catch (IOException e)
        {
        }
    }
    
    /**
     * Get the most recent XSI files opened by the user.
     * 
     * @return a list of XSI paths
     */
    public static List<String> getRecentXSIs()
    {
        initialise();
        // Make a copy of the xsis list
        List<String> xsis = new ArrayList<String>();
        xsis.addAll(recentxsis);
        return xsis;
    }
    
    /**
     * Clears the recent XSI history.
     */
    public static void clearRecentXSIs()
    {
        initialise();
        recentxsis.clear();
    }
    
    /**
     * Adds an XSI path to the list of recent XSIs.
     * 
     * @param xsipaths
     *              List of recent XSI paths
     */
    public static void updateRecentXSI(List<String> xsipaths)
    {
        initialise();
        recentxsis.clear();
        recentxsis.addAll(xsipaths);
        save();
    }
}

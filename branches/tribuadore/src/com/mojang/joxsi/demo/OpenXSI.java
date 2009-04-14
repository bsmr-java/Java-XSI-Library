package com.mojang.joxsi.demo;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Provides a prompt for users to select .xsi files from the file system.
 * If a file is chosen, all models it contains are loaded and added to the
 * list of known models by calling ModelDisplayer.addScene().
 * 
 * @author Tribuadore
 */
public class OpenXSI
{
    // Keeps track of the last path navigated to
    private static String staticpath = ".";

    /**
     * Custom FileFilter used by OpenXSI.open().
     */
    private static class XSIFilter extends FileFilter
    {

        // Accept only xsi files
        public boolean accept(File f)
        {
            String name = f.getName().toLowerCase();
            
            if (f.isDirectory() || name.endsWith(".xsi"))
                return true;

            return false;
        }

        // The description of this filter
        public String getDescription()
        {
            return "XSI 3D model";
        }
    }

    /**
     * Prompts the user for a XSI file and loads all models contained.
     * 
     * @param mdf
     *              An instance of ModelDisplayerFrame
     */
    public static void open(ModelDisplayerFrame mdf)
    {
        // Ask the user to choose a file
        JFileChooser fc = new JFileChooser(staticpath);
        fc.addChoosableFileFilter(new OpenXSI.XSIFilter());
        fc.setAcceptAllFileFilterUsed(false);
        
        // If a file is chosen, add it to ModelDisplayer
        if (fc.showOpenDialog(mdf) == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();              
            mdf.getModelDisplayer().setShowScene(file.getPath());
            ModelDisplayerProperties.save();
            OpenXSI.staticpath = file.getParent();
        }
    }
}

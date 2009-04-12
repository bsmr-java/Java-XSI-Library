package com.mojang.joxsi.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.mojang.joxsi.Scene;
import com.mojang.joxsi.loader.ParseException;

/**
 * Provides a prompt for users to select .xsi file from the file system.
 * If a file is chosen, all models it contains are loaded and added the list
 * of known models by calling ModelDisplayer.addScene().
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
        
        // If a file is chosen, load it using Scene.load() and add it to the
        // loaded models using ModelDisplayer.addScene().
        if (fc.showOpenDialog(mdf) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = fc.getSelectedFile();
                FileInputStream xsi = new FileInputStream(file);
                Scene model = Scene.load(xsi, file.getParent());                
                mdf.getModelDisplayer().addScene(file.getName(), model);                
                OpenXSI.staticpath = file.getParent();
            }
            catch (IOException e) {}
            catch (ParseException e) {}
        }
    }
}

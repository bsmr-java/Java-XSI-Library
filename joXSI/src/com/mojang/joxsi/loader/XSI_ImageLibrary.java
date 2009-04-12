package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_ImageLibrary extends Template
{
    /** Number of images in the library. */
    public int image_count;
    /** Array of XSI_Images. */
    public XSI_Image[] images;

    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        image_count = ((Integer)it.next()).intValue();

        images = new XSI_Image[image_count];
        int i=0;
        for (Iterator<Template> it2 = getAll(Template.XSI_Image).iterator(); it2.hasNext(); i++)
        {
            images[i] = (XSI_Image)it2.next();
        } 
    }

    @Override
    public String toString()
    {
        return super.toString() + ", Number of Images: " + image_count;
    }
}
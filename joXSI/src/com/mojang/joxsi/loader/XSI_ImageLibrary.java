package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_ImageLibrary extends Template
{
	public int image_count;
	public XSI_Image[] images;
	
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		image_count = ((Integer)it.next()).intValue();
		
		images = new XSI_Image[image_count];
		int i=0;
		for (Iterator it2 = getAll(Template.XSI_Image).iterator(); it2.hasNext(); i++)
		{
			images[i] = (XSI_Image)it2.next();
		} 
	}
}
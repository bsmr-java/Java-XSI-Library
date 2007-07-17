package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores the cluster’s type.
 * <p>This template is not available for SOFTIMAGE|3D.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_ClusterInfo extends Template
{
    /** Possible values are: “VERTEX” “POLY” “EDGE” “SUBSURFACE”. */
	public String type;

	public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
		type = (String)it.next(); 
		if(!type.equals("VERTEX") && 
		   !type.equals("POLY") &&
		   !type.equals("EDGE") &&
		   !type.equals("SUBSURFACE"))
		    throw new ParseException("Illegal XSI_ClusterInfo: "+type);
		
	}	
}
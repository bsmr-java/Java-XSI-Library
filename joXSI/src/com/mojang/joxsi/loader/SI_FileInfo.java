package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_FileInfo extends Template
{
	public String projectName;
	public String userName;
	public String savedDateTime;
	public String originator;
	
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		projectName = (String)it.next();
		userName = (String)it.next();
		savedDateTime = (String)it.next();
		originator = (String)it.next();
	}
}
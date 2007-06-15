package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_Material extends Template implements Material
{
	public class Connection implements Serializable
	{
		public String name;
		public String source;
	}
	
	public int cnx_number;
	public Connection[] connections;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		cnx_number = ((Integer)it.next()).intValue();
		connections = new Connection[cnx_number];
		for (int i=0; i<cnx_number; i++)
		{
			connections[i] = new Connection();
			connections[i].name = (String)it.next();
			connections[i].source = (String)it.next();
		}
	}
}
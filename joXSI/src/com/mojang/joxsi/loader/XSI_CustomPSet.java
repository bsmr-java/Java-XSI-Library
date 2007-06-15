package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_CustomPSet extends Template
{
	public String propagation;
	public int field_count;
	public Field[] fields;
	
	public class Field implements Serializable
	{
		public String name;
		public String type;
		public String value; 
	}

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		propagation = (String)it.next();
		field_count = ((Integer)it.next()).intValue();
		
		fields = new Field[field_count];
		for (int i=0; i<field_count; i++)
		{
			fields[i] = new Field();
			fields[i].name = (String)it.next();
			fields[i].type = (String)it.next();
			fields[i].value = it.next().toString();
		}
	}
}
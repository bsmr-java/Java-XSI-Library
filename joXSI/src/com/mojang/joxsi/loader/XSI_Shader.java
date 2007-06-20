package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_Shader extends Template
{
	public class Parameter implements Serializable
	{
		public String name;
		public String type;
		public Object value;

        public String toString()
        {
            return value + " (" + type + ")";
        }
	}
	
	public class Connection implements Serializable
	{
		public String name;
		public String point;
		public String type;

        public String toString() {
            if ((type == null || type.length() == 0) && (point == null || point.length() == 0))
            {
                return "";
            } else
            {
                return point + " (" + type + ")";
            }
        }
	}
	
	public String path;
	public int output;
	public int param_number;
	public int cnx_number;
	public Parameter[] parameters;
	public Connection[] connections;
	public Map parameterMap = new HashMap();
	public Map connectionMap = new HashMap();

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		
		path = (String)it.next();
		output = ((Integer)it.next()).intValue();
		param_number = ((Integer)it.next()).intValue();
		cnx_number = ((Integer)it.next()).intValue();
		
		parameters = new Parameter[param_number];
		for (int i=0; i<param_number; i++)
		{
			parameters[i] = new Parameter();
			parameters[i].name = (String)it.next();
			parameters[i].type = (String)it.next();
			parameters[i].value = it.next();
			
			parameterMap.put(parameters[i].name, parameters[i]);
		}

		connections = new Connection[cnx_number];
		for (int i=0; i<cnx_number; i++)
		{
			connections[i] = new Connection();
			connections[i].name = (String)it.next();
			connections[i].point = (String)it.next();
			connections[i].type = (String)it.next();

			connectionMap.put(connections[i].name, connections[i]);
		}
	}

    public Connection getConnection(String name)
    {
    	return (Connection)connectionMap.get(name);
    }

    public String toString()
    {
        return super.toString()
                + " - path: " + path + ", output: " + output + ", param_number: " + param_number + ", cnx_number: " + cnx_number
                + ", parameters± " + parameterMap + ", connections: " + connectionMap;
    }
}
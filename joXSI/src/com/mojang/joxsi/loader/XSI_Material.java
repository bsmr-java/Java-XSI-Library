package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Defines a SOFTIMAGE|XSI material. This template contains the list of 
 * connections and the name of the sources (if connected).
 * 
 * <p>This new material template is closer to the XSI conceptual model. 
 * In XSI, a material is essentially a placeholder for shaders. 
 * So now there is an entity that is a placeholder for both realtime and mental 
 * ray shaders in the dotXSI file format as well.
 * See also {@link XSI_MaterialInfo }.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_Material extends Template implements Material
{
	public class Connection implements Serializable
	{
        /** Name of connection. */
		public String name;
        /** Name of connection source. */
		public String source;

        /**
         * Returns the name of the connection source.
         * 
         * @return the name of the connection source.
         */
        @Override
        public String toString() {
            return source;
        }
	}

    /** Number of connections (that is, <cnx_name, <cnx_source> pairs) listed in this template. */
	public int cnx_number;
	public Connection[] connections;

    @Override
	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		cnx_number = ((Integer)it.next()).intValue();
		connections = new Connection[cnx_number];
		for (int i=0; i<cnx_number; i++)
		{
			connections[i] = new Connection();
			connections[i].name = (String)it.next();
			connections[i].source = (String)it.next();
		}
	}

    /**
     * Returns the clsss name and the number of connections pairs.
     * 
     * @return the clsss name and the number of connections pairs.
     */
    @Override
    public String toString()
    {
        String lConnectionDetails = "";
        for (Connection item : connections) {
            if (item != null && item.toString().length() > 0)
            {
                lConnectionDetails += item;
                lConnectionDetails += ", ";
            }
        }
        return super.toString() + " - cnx_number: " + cnx_number + ", details: " + lConnectionDetails;
    }
}
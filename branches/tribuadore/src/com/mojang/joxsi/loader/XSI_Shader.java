package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Defines a SOFTIMAGE|XSI shader.
 * 
 * <p>These are embedded inside the XSI_Material template. The connection name provides
 * the path to connect the parameter of a particular shader to another shader or image.
 * 
 * <p>If the shader parameters are animated, their fcurves are exported to the dotXSI file.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_Shader extends Template
{
	/**
     * Indicates that the {@link Connection#type } of this {@link Connection } is an Image.
     */
    private static final String CONNECTION_TYPE_IMAGE = "IMAGE";
    /**
     * Indicates that the {@link Connection#type } of this {@link Connection } is a Shader.
     */
    private static final String CONNECTION_TYPE_SHADER = "SHADER";
    /**
     * Indicates that the {@link Parameter#type } of this {@link Parameter } is a String.
     */
    private static final String PARAMETER_TYPE_STRING = "STRING";
    /**
     * Indicates that the {@link Parameter#type } of this {@link Parameter } is a Float.
     */
    private static final String PARAMETER_TYPE_FLOAT = "FLOAT";
    /**
     * Indicates that the {@link Parameter#type } of this {@link Parameter } is a Integer.
     */
    private static final String PARAMETER_TYPE_INTEGER = "INTEGER";
    /**
     * Indicates that the {@link Parameter#type } of this {@link Parameter } is a Byte.
     */
    private static final String PARAMETER_TYPE_BYTE = "BYTE";
    /**
     * Indicates that the {@link Parameter#type } of this {@link Parameter } is a Boolean.
     */
    private static final String PARAMETER_TYPE_BOOLEAN = "BOOLEAN";

    public class Parameter implements Serializable
	{
        /** Parameter name. */
		public String name;
        /**
         * Parameter type. Possible values are:<br>
         * � �BOOLEAN�<br>
         * � �BYTE�<br>
         * � �INTEGER�<br>
         * � �FLOAT�<br>
         * � �STRING�
         */
		public String type;
        /** Parameter value. */
		public Object value;

		/**
         * Returns a String containing the parameter value and type.
         * 
         * @return a String containing the parameter value and type.
		 */
        @Override
        public String toString()
        {
            return value + " (" + type + ")";
        }
	}

	public class Connection implements Serializable
	{
        /**
         * Name of connection source.
         * <p>Note: If nothing is connected, this value is an empty string (��).
         */
		public String name;
        /** ID of connection point (index number). */
		public String point;
        /**
         * Type of connection source. Possible values are:<br>
         * � �SHADER� = shader is connected<br>
         * � �IMAGE� = image is connected<br>
         * � �� = nothing is connected
         */
		public String type;

        /**
         * Returns the Connection point and type or an empty String if there is
         * no connection.
         * 
         * @return the Connection point and type or an empty String if there is
         *         no connection.
         */
        @Override
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

    /** XSI path name. */
	public String path;
    /**
     * Output type. Possible values are:<br>
     * � 0 = Unknown<br>
     * � 1 = Boolean<br>
     * � 2 = Integer<br>
     * � 3 = Scalar<br>
     * � 4 = Color<br>
     * � 5 = Vector<br>
     * � 6 = Texture space<br>
     * � 7 = Texture<br>
     * � 8 = String<br>
     * � 9 = Filename<br>
     * � 10 = Lens<br>
     * � 11 = Light<br>
     * � 12 = Material<br>
     * � 13 = Model
     */
	public int output;
    /** All parameters that are part of this operator. */
	public int param_number;
    /** Number of connection points. */
	public int cnx_number;
    /** Array of Parameters. */
	public Parameter[] parameters;
    /** Array of Connections. */
	public Connection[] connections;
	public Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();
	public Map<String, Connection> connectionMap = new HashMap<String, Connection>();

	@Override
    public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();

		path = (String)it.next();
		output = ((Integer)it.next()).intValue();
		param_number = ((Integer)it.next()).intValue();
		cnx_number = ((Integer)it.next()).intValue();

		if(output < 0 || output > 13)
            throw new ParseException("Illegal output in XSI_Shader: "+output);

		parameters = new Parameter[param_number];
		for (int i=0; i<param_number; i++)
		{
			parameters[i] = new Parameter();
			parameters[i].name = (String)it.next();
			parameters[i].type = (String)it.next();
			parameters[i].value = it.next();

			parameterMap.put(parameters[i].name, parameters[i]);

			if(!parameters[i].type.equals(PARAMETER_TYPE_BOOLEAN) &&
			   !parameters[i].type.equals(PARAMETER_TYPE_BYTE) &&
			   !parameters[i].type.equals(PARAMETER_TYPE_INTEGER) &&
			   !parameters[i].type.equals(PARAMETER_TYPE_FLOAT) &&
			   !parameters[i].type.equals(PARAMETER_TYPE_STRING))
			    throw new ParseException("Illegal parameter type in XSI_Shader: "+parameters[i].type);
		}

		connections = new Connection[cnx_number];
		for (int i=0; i<cnx_number; i++)
		{
			connections[i] = new Connection();
			connections[i].name = (String)it.next();
			connections[i].point = (String)it.next();
			connections[i].type = (String)it.next();

			connectionMap.put(connections[i].name, connections[i]);

			if(!connections[i].type.equals(CONNECTION_TYPE_SHADER) &&
			   !connections[i].type.equals(CONNECTION_TYPE_IMAGE) &&
			   !connections[i].type.equals(""))
			    throw new ParseException("Illegal connection type in XSI_Shader: "+connections[i].type);
		}
	}

    /**
     * Returns the Connection with the given name.
     * 
     * @param name
     *            Connection name.
     * @return the Connection with the given name.
     */
    public Connection getConnection(String name)
    {
    	return connectionMap.get(name);
    }

    /**
     * Returns the Parameter with the given name.
     * 
     * @param name
     *            Parameter name.
     * @return the Parameter with the given name.
     */
    public Parameter getParameter(String name)
    {
        return parameterMap.get(name);
    }

    /**
     * Returns a String that describes the Shader including the Parameters and
     * Connections.
     * 
     * @return a String that describes the Shader including the Parameters and
     *         Connections.
     */
    @Override
    public String toString()
    {
        return super.toString()
                + " - path: " + path + ", output: " + output + ", param_number: " + param_number + ", cnx_number: " + cnx_number
                + ", parameters� " + parameterMap + ", connections: " + connectionMap;
    }
}
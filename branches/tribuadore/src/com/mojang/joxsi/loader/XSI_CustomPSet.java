package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Stores a custom property added to an XSI object.
 * 
 * <p>When this template is imported into or exported from SOFTIMAGE|3D, 
 * it is converted to user data with the {@link XSI_CustomPSet } tag. Here is the format 
 * of the SOFTIMAGE|XSI user data. Note that this user data will not have the same 
 * import or export behavior as the regular user data mechanism.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * @see XSI_CustomParamInfo
 */
public class XSI_CustomPSet extends Template
{
    /**
     * Specifies whether the property is either just for the object it is on 
     * only or for the object it is on plus all its children--this status is displayed 
     * in XSI’s Explorer. If a custom parameter is on a group, it will be exported as BRANCH, 
     * since dotXSI does not export groups). Possible values are:<br>
     * • BRANCH = property is shared by the object and all its children<br>
     * • NODE = property is added only to the object
     */
	public String propagation;
    
	/** Number of parameters in the custom property. */
    public int field_count;
    /** Array of parameters. */
	public Field[] fields;
	
	public class Field implements Serializable
	{
        /** ame of a custom parameter. */
		public String name;
        /**
         * Parameter type. Possible values are:<br>
         * • Text<br>
         * • Boolean<br>
         * • Integer<br>
         * • Small integer number<br>
         * • Floating point number
         * <p>Note: For more information on how dotXSI handles variant data type conversion, 
         * see How Does dotXSI Deal with Variant Types.
         */
		public String type;
        /** Value for the parameter. */
		public String value; 

	    @Override
	    public String toString()
	    {
	        return "Field [name: " + name + ", value: " + value + ", type: " + type + ']';
	    }
	}

	@Override
    public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
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

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", propagation: " + propagation + ", fieldCount: " + field_count
                + ", type: " + Arrays.toString(fields);
    }
}
package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Supplements the {@link XSI_CustomPSet } template with additional information.
 * <p>This template is not available in SOFTIMAGE|3D.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_CustomParamInfo extends Template
{
    /**
     * Minimum value.<br>
     * Note: For string parameters, this value must be set to empty string (““).
     */
	public float min;
    /**
     * Maximum value.<br>
     * For string parameters, this value must be set to empty string (““).
     */
	public float max;
    /**
     * Bit flags corresponding to the capabilities of the parameter. Same as “siCapabilities” in the object model.
     * Flag values can be cumulative. You can combine them with bitwise “OR”.
     * <p>Possible values are:<br>
     * 1 = Animatable<br>
     * 2 = Read only<br>
     * 4 = Persistable<br>
     * 8 = Not inspectable<br>
     * 16 = Silent<br>
     * 128 = Not Pset inspectable<br>
     * 256 = Texturable
     */
	public int capabilities;

	@Override
    public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
		min = ((Number)it.next()).floatValue();
		max = ((Number)it.next()).floatValue();
		capabilities = ((Integer)it.next()).intValue();
		if(capabilities < 0) // TODO xwalk.chm shows very large numbers || capabilities > 415)
		{
		    throw new ParseException("Illegal capabilities in XSI_CustomParamInfo: "+capabilities); 
		}
	}

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", min: " + min + ", max: " + max + ", capabilities: " + capabilities;
    }
}
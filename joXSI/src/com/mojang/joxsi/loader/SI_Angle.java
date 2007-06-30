package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Indicates how angle values are expressed in the file.
 * If not specified, the values in the file should be considered as being expressed in degrees.
 * 
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Angle extends Template
{
    /** Indicates angle values are expressed in Degrees in the file. */
    public static final int DEGREES = 0;
    /** Indicates angle values are expressed in Radians in the file. */
    public static final int RADIANS = 1;
    
    /**
     * Specifies angle representation:<br>
     * 0 = degrees<br>
     * 1 = radians
     */
	public int type;

    @Override
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		type = ((Integer)it.next()).intValue();
	}

    /**
     * Returns the angle representation for the model.
     * 
     * @return the angle representation for the model.
     * @ost $result == DEGREES || $result == RADIANS
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the angle representation for the model.
     * 
     * @param aType
     *            the angle representation for the model.
     * @pre aType == DEGREES || aType == RADIANS
     */
    public void setType(final int aType) {
        type = aType;
    }

    @Override
    public String toString() {
        return super.toString() + " - type: " + type;
    }
}
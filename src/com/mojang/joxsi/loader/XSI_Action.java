package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores source action parameters.
 * <p>SOFTIMAGE|3D does not export this template. 
 * If an XSI file contains this template, SOFTIMAGE|3D ignores it.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * @see XSI_Mixer
 */
public class XSI_Action extends Template
{
    /** Start time. */
    public float start;
    /** End time. */
    public float end;
    /**
     * 0 = FCurve action source<br>
     * 1 = Static value action source<br>
     * 2 = Expression source<br>
     * 3 = ClusterKey source<br>
     * 4 = Constraint source<br>
     * 5 = Compound action item source<br>
     * 6 = Shape compound action item source<br>
     * 7 = no flagged source
     */
    public int type;

    @Override
    public void parse(RawTemplate block) throws ParseException
    {
        Iterator<Object> it = block.values.iterator();
        start = ((Float)it.next()).floatValue();
        end = ((Float)it.next()).floatValue();
        type = ((Integer)it.next()).intValue();

        if(type < 0 || type > 7)
            throw new ParseException("Illegal type in XSI_Action: "+type);
    }

    /**
     * Returns the type of action as descriptive text.
     * 
     * @param aType
     *            the type of action
     * @return the type of action as descriptive text.
     * @pre $aType >= 0 && aType <= 7
     * @post $none
     */
    public String getTypeDescription(int aType)
    {
        String lDescription;

        switch (aType)
        {
            case 0:
                lDescription = "FCurve action source";
                break;
            case 1:
                lDescription = "Static value action source";
                break;
            case 2:
                lDescription = "Expression source";
                break;
            case 3:
                lDescription = "ClusterKey source";
                break;
            case 4:
                lDescription = "Constraint source";
                break;
            case 5:
                lDescription = "Compound action item source";
                break;
            case 6:
                lDescription = "Shape compound action item";
                break;
            case 7:
                lDescription = "Fno flagged source";
                break;
            default:
                lDescription = "Unknown action source type: " + aType;
                break;
        }
        return lDescription;
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return super.toString() + ", type: " + getTypeDescription(type) + ", start: " + start + ", end: " + end;
    }    
}
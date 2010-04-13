package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores branch and node visibility fcurves. 
 * This template does not store light and camera visibility.
 * <p>In SOFTIMAGE|XSI only node information is stored.
 * 
 * <p>optional fcurve must be inside the SI_Visibility template 
 * the node visibility fcurve gives the visibility through time of the single
 * element. the node vis fcurve info supersedes any branch visibility fcruve 
 * for the elemement to which it is applied. 
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Visibility extends Template
{
    /**
     * Determines whether visible or not. Possible values are:<br>
     * • 0 = Not visible.<br>
     * • 1 = Visible.
     */
    public boolean visibility;

    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        visibility = ((Integer)it.next()).intValue()!=0;
    }

    @Override
    public String toString()
    {
        return super.toString() + "visibility: " + visibility;
    }
}
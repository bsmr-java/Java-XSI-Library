package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Visibility extends Template
{
    public boolean visibility;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        visibility = ((Integer)it.next()).intValue()!=0;
    }
}
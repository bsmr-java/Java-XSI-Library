
package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * TODO Use the parsed values.
 */
public class SI_Constraint extends Template
{
    private String objectName;
    private String constraintType;
    private int nbConstrainingObjects;
    private String constrainingObjectName;
    
    public void parse(RawTemplate rawtemplate) 
    {
    	Iterator iterator = rawtemplate.values.iterator();
    	objectName = (String) iterator.next();
    	constraintType = (String) iterator.next();
    	nbConstrainingObjects = ((Integer) iterator.next()).intValue();
    	constrainingObjectName = (String) iterator.next();
    }
}

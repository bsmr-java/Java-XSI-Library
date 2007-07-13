
package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores constraints. This class is a container for a template in the dotXSI
 * file format, as specified by XSIFTK template reference. <br>
 * <table border="1" cellpadding="5" cellspacing="0"> <tbody>
 * <tr>
 * <td><b>&lt;constraintType&gt;</b> </td>
 * <td><b>Applies to</b> </td>
 * </tr>
 * <tr>
 * <td> POSITION </td>
 * <td> Lights, cameras, models </td>
 * </tr>
 * <tr>
 * <td> SCALING </td>
 * <td> Models </td>
 * </tr>
 * <tr>
 * <td> DIRECTION </td>
 * <td> Models </td>
 * </tr>
 * <tr>
 * <td> ORIENTATION </td>
 * <td> Models </td>
 * </tr>
 * <tr>
 * <td> UP_VECTOR </td>
 * <td> Cameras, models </td>
 * </tr>
 * <tr>
 * <td> PREFERED_AXIS </td>
 * <td> Models </td>
 * </tr>
 * <tr>
 * <td> INTEREST </td>
 * <td> Lights, cameras </td>
 * </tr>
 * </tbody></table>
 * <p>
 * It's very sparsely documented.
 * TODO Use the parsed values.
 * @author Notch 
 * @author Egal
 */
public class SI_Constraint extends Template
{
    /** Nbject name. */
    public  String objectName;
    /** Constraint type. */
    public String constraintType;
    /** Number of Constraining Objects. */
    public int nbConstrainingObjects;
    /** Constraining Object Name. */
    public String constrainingObjectName;
    
    public void parse(RawTemplate rawtemplate) throws ParseException 
    {
    	Iterator<Object> iterator = rawtemplate.values.iterator();
    	objectName = (String) iterator.next();
    	constraintType = (String) iterator.next();
    	nbConstrainingObjects = ((Integer) iterator.next()).intValue();
    	constrainingObjectName = (String) iterator.next();
    	
    	if(!constraintType.equals("POSITION") &&
    	   !constraintType.equals("SCALING") &&
    	   !constraintType.equals("DIRECTION") &&
    	   !constraintType.equals("ORIENTATION") &&
    	   !constraintType.equals("UP_VECTOR") &&
    	   !constraintType.equals("PREFERED_AXIS") &&
    	   !constraintType.equals("INTEREST"))
    	    throw new ParseException("Illegal constrainType in SI_Constraint: "+constraintType);
    }
}

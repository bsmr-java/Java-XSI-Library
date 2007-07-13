package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Identifies the global material of {@link SI_Model }. In the dotXSI file format, 
 * <code>SI_Model</code> corresponds to a model in SOFTIMAGE|3D and an object in SOFTIMAGE|XSI. 
 * The template is always embedded inside <code>SI_Model</code>.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_GlobalMaterial extends Template
{
    /** Material for a 3D model or an XSI object. */
    public String referencedMaterial;
    /**
     * Indicates how the material propagation occurs. In the case of
     * SOFTIMAGE|3D, everything is always exported using BRANCH (there is no
     * such thing as a NODE propagated material). In the case of SOFTIMAGE|XSI,
     * the material propagation can be any of the following:<br>
     * • BRANCH = material is shared throughout the branch (ie., object and all its nodes)<br>
     * • NODE = material is applied only to the object’s node • INHERITED = material has
     *  been inherited from a parent object
     */
    public String propagation;

    public void parse(RawTemplate block) throws ParseException
    {
        Iterator<Object> it = block.values.iterator();
        referencedMaterial = (String)it.next();
        propagation = (String)it.next();
        
        if(!propagation.equals("BRANCH") &&
           !propagation.equals("NODE") &&
           !propagation.equals("INHERITED"))
            throw new ParseException("Illegal propagation in SI_GlobalMaterial: "+propagation);
    }

    public String toString() {
        return super.toString() + " - referencedMaterial: " + referencedMaterial + ", propagation: " + propagation;
    }
}
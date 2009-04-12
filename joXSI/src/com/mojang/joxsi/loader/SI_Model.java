package com.mojang.joxsi.loader;

/**
 * Contains all the information for one of the following objects:<br>
 * • A mesh, surface mesh, curve list, patch surface or a null<br>
 * • A root, effector, or joint of an IK chain<br>
 * • A SOFTIMAGE|XSI model
 * 
 * <p><code>SI_Model</code> replaces the Frame template in version 2.0 of the dotXSI file.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * 
 * @see SI_Transform
 * @see SI_Mesh
 * @see SI_Null
 * @see XSI_CurveList
 * @see SI_PatchSurface
 * @see XSI_SurfaceMesh
 * @see SI_IK_Effector
 * @see SI_IK_Joint
 * @see SI_IK_Root
 * @see SI_Constraint
 * @see SI_Instance
 */
public class SI_Model extends Template
{
    @Override
    public void parse(RawTemplate block)
    {
    }
}
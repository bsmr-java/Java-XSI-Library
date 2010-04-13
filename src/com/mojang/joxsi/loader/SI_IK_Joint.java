package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores IK joint information.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <pre>
 * SI_IK_Joint &lt;joint name&gt;
 * {
 * &lt;solver type (2D | 3D | DEFAULT)&gt;,
 * &lt;length&gt;
 * &lt;preferred rotation x&gt;, 
 * &lt;preferred rotation y&gt;, 
 * &lt;preferred rotation z&gt;,
 * &lt;rotation limit activation&gt;,
 * &lt;rotation limit min x&gt;, 
 * &lt;rotation limit min y&gt;, 
 * &lt;rotation limit min z&gt;,
 * &lt;rotation limit max x&gt;, 
 * &lt;rotation limit max y&gt;, 
 * &lt;rotation limit max z&gt;,
 * &lt;pseudo-root&gt;,
 * &lt;stiffness activation&gt;,
 * &lt;stiffness&gt;,
 * }
 * 
 * SI_IK_Joint R_foot { 
 *                      "2D", 
 *                      1.297064, 
 *                      0.000000, 
 *                      -0.000000, 
 *                      -152.342743, 
 *                      0, 
 *                      -180.000000, 
 *                      -180.000000, 
 *                      -180.000000, 
 *                      180.000000, 
 *                      180.000000, 
 *                      180.000000, 
 *                      0, 
 *                      0, 
 *                      0.000000, 
 *                  }
 * </pre>
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_IK_Joint extends Template
{
    public String solver_type; 
    public float length;
    public float pref_rot_x, pref_rot_y, pref_rot_z;
    public boolean rotation_limit_activation;
    public float rot_limit_min_x, rot_limit_min_y, rot_limit_min_z; 
    public float rot_limit_max_x, rot_limit_max_y, rot_limit_max_z; 
    public boolean pseudo_root;
    public boolean stiffness_activation;
    public float stiffness;


    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        solver_type = (String)it.next();
        length = ((Float)it.next()).floatValue();
        pref_rot_x = ((Float)it.next()).floatValue();
        pref_rot_y = ((Float)it.next()).floatValue();
        pref_rot_z = ((Float)it.next()).floatValue();
        rotation_limit_activation = ((Integer)it.next()).intValue()!=0;

        rot_limit_min_x = ((Float)it.next()).floatValue();
        rot_limit_min_y = ((Float)it.next()).floatValue();
        rot_limit_min_z = ((Float)it.next()).floatValue();
        rot_limit_max_x = ((Float)it.next()).floatValue();
        rot_limit_max_y = ((Float)it.next()).floatValue();
        rot_limit_max_z = ((Float)it.next()).floatValue();

        pseudo_root = ((Integer)it.next()).intValue()!=0;
        stiffness_activation = ((Integer)it.next()).intValue()!=0;
        stiffness = ((Float)it.next()).floatValue();
    }
}
package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
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
	

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
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
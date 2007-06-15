package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_LightInfo extends Template
{
	public boolean falloff_active;
	public int mode;
	public float start;
	public float end;
	public boolean shadows_active;
	public float umbra;
	public boolean useLight_as_energy;
	public float photon_factor;
	public float intensity;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();

		falloff_active = ((Integer)it.next()).intValue()!=0;
		mode = ((Integer)it.next()).intValue();
		start = ((Float)it.next()).floatValue();
		end = ((Float)it.next()).floatValue();
		shadows_active = ((Integer)it.next()).intValue()!=0;
		umbra = ((Float)it.next()).floatValue();
		useLight_as_energy = ((Integer)it.next()).intValue()!=0;
		photon_factor = ((Float)it.next()).floatValue();
		intensity = ((Float)it.next()).floatValue();
	}
}
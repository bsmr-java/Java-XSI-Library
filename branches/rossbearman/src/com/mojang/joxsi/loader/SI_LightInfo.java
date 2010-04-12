package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This template supplements the information in the {@link SI_Light } template. 
 * It includes falloff, shadows, and photon information.
 * All parameters but <code>mode</code> are animatable.
 * <p>Fcurve names are:<br>
 * • FALLOFF_ACTIVE<br>
 * • FALLOFF_START<br>
 * • FALLOFF_END<br>
 * • SHADOWS_ENABLED<br>
 * • UMBRA<br>
 * • LIGHT_AS_ENERGY<br>
 * • ENERGY_FACTOR<br>
 * • INTENSITY
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_LightInfo extends Template
{
    /** Attenuation. */
    public boolean falloff_active;
    /** 0 - Linear
     *  1 - Use Exponent type
     */
    public int mode;
    public float start;
    public float end;
    public boolean shadows_active;
    /** 0 - 1, .75 default */
    public float umbra;
    /** True or false */
    public boolean useLight_as_energy;
    /** 0 - 100000, default .75 */
    public float photon_factor;
    public float intensity;

    @Override
    public void parse(RawTemplate block) throws ParseException
    {
        Iterator<Object> it = block.values.iterator();

        falloff_active = ((Integer)it.next()).intValue()!=0;
        mode = ((Integer)it.next()).intValue();
        start = ((Float)it.next()).floatValue();
        end = ((Float)it.next()).floatValue();
        shadows_active = ((Integer)it.next()).intValue()!=0;
        umbra = ((Float)it.next()).floatValue();
        useLight_as_energy = ((Integer)it.next()).intValue()!=0;
        photon_factor = ((Float)it.next()).floatValue();
        intensity = ((Float)it.next()).floatValue();

        if(mode < 0 || mode > 1)
            throw new ParseException("Illegal mode in SI_LightInfo: "+mode);
        if(umbra < 0.0f || umbra > 1.0f)
            throw new ParseException("Illegal umbra in SI_LightInfo: "+umbra);
        if(photon_factor < 0 || photon_factor > 100000)
            throw new ParseException("Illegal photon_factor in SI_LightInfo: "+photon_factor);

    }

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", mode: " + mode + ", start: " + start + ", end: " + end
            + ", shadows_active: " + shadows_active + ", umbra: " + umbra + ", useLightAsEnergy: " + useLight_as_energy
            + ", photonFactor: " + photon_factor + ", intensity: " + intensity + ", falloff_active: " + falloff_active;
    }
}
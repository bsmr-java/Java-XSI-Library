package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores the rotation flag for the effector of an IK chain. 
 * This flag affects the way the effector rotation is exported. 
 * In SOFTIMAGE|XSI this flag refers to the Effector Rotation Relative to 
 * Last Bone option. If the last bone inherits its rotation, 
 * the flag is set to True. False if not. This means that rotation in the {@link SI_FCurve } 
 * template will be local if this flag is True and global if this flag is False.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_IK_Effector extends Template
{
    /**
     * This flag affects the way the effector rotation is exported. In
     * SOFTIMAGE|XSI this flag refers to the Effector Rotation Relative to Last
     * Bone option. If the last bone inherits its rotation, the flag is set to
     * True. False if not. This means that rotation in the {@link SI_FCurve }
     * template will be local if this flag is True and global if this flag is
     * False. *
     */
    public boolean rotation_flag;

    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        rotation_flag = ((Integer)it.next()).intValue()!=0;
    }
}
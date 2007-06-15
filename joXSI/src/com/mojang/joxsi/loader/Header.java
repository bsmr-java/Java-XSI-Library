package com.mojang.joxsi.loader;

import java.io.Serializable;

/**
 * The header of a dotXSI file.
 */
public class Header implements Serializable
{
    public int majorVersion;
    public int minorVersion;
    public String formatType;
    public String compressionType;
    public int floatSize;
}
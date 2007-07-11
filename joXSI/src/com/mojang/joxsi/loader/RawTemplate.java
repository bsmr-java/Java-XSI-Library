package com.mojang.joxsi.loader;

import java.util.ArrayList;
import java.util.List;

/**
 * A temporary item used for parsing an XSI file.
 */
class RawTemplate
{
    public final String name;
    public final String info;
    public final List<Object> values = new ArrayList<Object>();
    /** RootTemplate used for creating the root RawTemplate in the DotXSILoader.. */
    public static final String ROOT_TEMPLATE = "RootTemplate";

    public RawTemplate(String name, String info)
    {
        this.name = name;
        this.info = info;
    }
}
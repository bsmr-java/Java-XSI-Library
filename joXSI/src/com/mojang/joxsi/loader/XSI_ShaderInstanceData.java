package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * When materials are shared, shaders with texture projections have shader instance data. 
 * This instance data is stored in the <code>XSI_ShaderInstanceData</code> template.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <pre>
 * XSI_ShaderInstanceData { 
   <sObjectName>, 
   <nbParameters>, 
   <parameter>,
}

Example

SI_MaterialLibrary MATLIB-tex_test { 
   1, 
   XSI_Material DefaultLib.Material { 
       ...
       XSI_Shader Image { 
          "Softimage.txt2d-image-explicit.1", 
          4, 
          30, 
          21, 
          "Name","STRING","Image",
          "tspace_id","STRING","Texture_Projection",
          ...
          XSI_ShaderInstanceData { 
              "Model1.sphere",  // Full name of an XSI object
              1,                   // Number of instance values
              "tspace_id","STRING","Texture_Projection1", // Instance value
          }
       }
       ...
   }
}

 * </pre>
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_ShaderInstanceData extends Template
{
	public class Parameter implements Serializable
	{
		public String name;
		public String type;
		public Object value;
	}
	
	public String objectName;
	public int output;
	public int param_number;
	public Parameter[] parameters;
	public Map<String, Parameter> parameterMap = new HashMap<String, Parameter>();

    @Override
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		
        objectName = (String)it.next();
		param_number = ((Integer)it.next()).intValue();
		
		parameters = new Parameter[param_number];
		for (int i=0; i<param_number; i++)
		{
			parameters[i] = new Parameter();
			parameters[i].name = (String)it.next();
			parameters[i].type = (String)it.next();
			parameters[i].value = it.next();
			
			parameterMap.put(parameters[i].name, parameters[i]);
		}
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString() + " - objectName: " + objectName + ", Output: " + output + ", Number of parameters: " + param_number;
    }
}
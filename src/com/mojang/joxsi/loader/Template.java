package com.mojang.joxsi.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public abstract class Template
{
    public static final String SI_Ambience = "SI_Ambience";
    public static final String SI_Angle = "SI_Angle";
    public static final String SI_Camera = "SI_Camera";
    public static final String SI_Cluster = "SI_Cluster";
    public static final String SI_Constraint = "SI_Constraint";
    public static final String SI_CoordinateSystem = "SI_CoordinateSystem";
    public static final String SI_ElementUserData = "SI_ElementUserData";
    public static final String SI_Envelope = "SI_Envelope";
    public static final String SI_EnvelopeList = "SI_EnvelopeList";
    public static final String SI_FileInfo = "SI_FileInfo";
    public static final String SI_FCurve = "SI_FCurve";
    public static final String SI_Fog = "SI_Fog";
    public static final String SI_GlobalMaterial = "SI_GlobalMaterial";
    public static final String SI_IK_Effector = "SI_IK_Effector";
    public static final String SI_IK_Joint = "SI_IK_Joint";
    public static final String SI_IK_Root = "SI_IK_Root";
    public static final String SI_ImageClip = "SI_ImageClip";
    public static final String SI_Instance = "SI_Instance";
    public static final String SI_Light = "SI_Light";
    public static final String SI_LightInfo = "SI_LightInfo";
    public static final String SI_Material = "SI_Material";
    public static final String SI_MaterialLibrary = "SI_MaterialLibrary";
    public static final String SI_Mesh = "SI_Mesh";
    public static final String SI_Model = "SI_Model";
    public static final String SI_Null = "SI_Null";
    public static final String SI_NurbsCurve = "SI_NurbsCurve";
    public static final String SI_NurbsSurface = "SI_NurbsSurface";
    public static final String SI_PatchSurface = "SI_PatchSurface";
    public static final String SI_PolygonList = "SI_PolygonList";
    public static final String SI_Scene = "SI_Scene";
    public static final String SI_Shape = "SI_Shape";
    public static final String SI_ShapeAnimation = "SI_ShapeAnimation";
    public static final String SI_SubelementUserDataPolygon = "SI_SubelementUserDataPolygon";
    public static final String SI_SubelementUserDataVertex = "SI_SubelementUserDataVertex";
    public static final String SI_Texture2D = "SI_Texture2D";
    public static final String SI_Transform = "SI_Transform";
    public static final String SI_TriangleList = "SI_TriangleList";
    public static final String SI_TriStrip = "SI_TriStrip";
    public static final String SI_TriStripList = "SI_TriStripList";
    public static final String SI_UserDataFormat = "SI_UserDataFormat";
    public static final String SI_Visibility = "SI_Visibility";
    public static final String XSI_Action = "XSI_Action";
    public static final String XSI_ActionClip = "XSI_ActionClip";
    public static final String XSI_ClusterInfo = "XSI_ClusterInfo";
    public static final String XSI_CurveList = "XSI_CurveList";
    public static final String XSI_CustomPSet = "XSI_CustomPSet";
    public static final String XSI_CustomParamInfo = "XSI_CustomParamInfo";
    public static final String XSI_Extrapolation = "XSI_Extrapolation";
    public static final String XSI_FXOperator = "XSI_FXOperator";
    public static final String XSI_FXTree = "XSI_FXTree";
    public static final String XSI_Image = "XSI_Image";
    public static final String XSI_ImageData = "XSI_ImageData";
    public static final String XSI_ImageFX = "XSI_ImageFX";
    public static final String XSI_ImageLibrary = "XSI_ImageLibrary";
    public static final String XSI_Material = "XSI_Material";
    public static final String XSI_MaterialInfo = "XSI_MaterialInfo";
    public static final String XSI_Mixer = "XSI_Mixer";
    public static final String XSI_NurbsProjection = "XSI_NurbsProjection";
    public static final String XSI_NurbsTrim = "XSI_NurbsTrim";
    public static final String XSI_Shader = "XSI_Shader";
    public static final String XSI_ShaderInstanceData = "XSI_ShaderInstanceData";
    public static final String XSI_StaticValues = "XSI_StaticValues";
    public static final String XSI_SurfaceMesh = "XSI_SurfaceMesh";
    public static final String XSI_TimeControl = "XSI_TimeControl";
    public static final String XSI_Track = "XSI_Track";
    public static final String XSI_UserData = "XSI_UserData";
    public static final String XSI_UserDataList = "XSI_UserDataList";

    public Template parent;
    public String template_type;
    public String template_info;
    protected Header dot_xsi_header;

    public List<Template> templates = new ArrayList<Template>();

    public void parseBlock(Header header, RawTemplate block) throws ParseException 
    {
        this.dot_xsi_header = header;
        template_type = block.name;
        template_info = block.info;
        
        try
        {
            parse(block);
        }
        catch (ParseException e)
        {
            throw e;
        }
        

        for (Iterator<Object> it = block.values.iterator(); it.hasNext();)
        {
            Object o = it.next();
            if (o instanceof Template)
            {
                templates.add((Template)o);
                ((Template)o).parent = this;
            }
        }
    }

    public Template get(String templateType)
    {
        Template result = null;

        for (Iterator<Template> it = templates.iterator(); it.hasNext();)
        {
            Template template = it.next();
            if (template.template_type.equals(templateType))
            {
                if (result != null)
                    System.out.println("WARNING: Found multiple instances of " + templateType + " in " + this+" ["+result+" and "+template+"]");
                else
                    result = template;
            }
        }

        return result;
    }

    public Template getStartsWith(String templateType, String name)
    {
        Template result = null;

        for (Iterator<Template> it = templates.iterator(); it.hasNext();)
        {
            Template template = it.next();
            if (template.template_type.equals(templateType) && template.template_info.startsWith(name))
            {
                if (result != null)
                    System.out.println("WARNING: Found multiple instances of " + templateType + " in " + this+" ["+result+" and "+template+"]");
                else
                    result = template;
            }
        }

        return result;
    }

    public Template get(String templateType, String name)
    {
        Template result = null;

        for (Iterator<Template> it = templates.iterator(); it.hasNext();)
        {
            Template template = it.next();
            if (template.template_type.equals(templateType) && template.template_info.equals(name))
            {
                if (result != null)
                    System.out.println("WARNING: Found multiple instances of " + templateType + " in " + this+" ["+result+" and "+template+"]");
                else
                    result = template;
            }
        }

        return result;
    }

    /**
     * Returns a List of all Templates in the current model whose name matches
     * the given String.
     * 
     * @param templateType
     *            the search String.
     * @return a List of all Templates in the current model whose name matches
     *         the given String.
     */
    public List<Template> getAll(String templateType)
    {
        List<Template> result = new ArrayList<Template>();

        for (Iterator<Template> it = templates.iterator(); it.hasNext();)
        {
            Template template = it.next();
            if (template.template_type.equals(templateType))
                result.add(template);
        }

        return result;
    }

    /**
     * Returns a List of all Templates in the current model with
     * the given Class.
     * 
     * Works like getAll(String), but without string matching and casting.
     * 
     * @param type the Class of templates to list
     * @return a List of all Templates in the current model whose class matches.
     * 
     */
    public <T extends Template> List<T> getAll(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Template t : templates)
        {
            if (type.isInstance(t)) {
                result.add(type.cast(t));
            }
        }
        return result;
    }

    /**
     * Returns a List of all Templates in the current model whose name starts
     * with the given String.
     * 
     * @param templateType
     *            the search String.
     * @return a List of all Templates in the current model whose name starts
     *         with the given String.
     */
    public List<Template> getAllStartingWith(String templateType)
    {
        List<Template> result = new ArrayList<Template>();

        for (Iterator<Template> it = templates.iterator(); it.hasNext();)
        {
            Template template = it.next();
            if (template.template_type.startsWith(templateType))
                result.add(template);
        }

        return result;
    }

    /**
     * Parses a <code>Template</code> from the {@link RawTemplate }.
     * 
     * @param block
     *            the raw template.
     */
    public abstract void parse(RawTemplate block) throws ParseException;

    /**
     * Returns the root template.
     * 
     * @return the root template.
     */
    public Template getRoot() {
        if (parent != null)
            return parent.getRoot();
        else
            return this;
    }

    /**
     * Returns the parent of the current template.
     * 
     * @return the parent of the current template.
     */
    public Template getParent() {
        return parent;
    }

    /**
     * @return the template_type
     */
    public String getTemplate_type()
    {
        return template_type;
    }

    /**
     * @return the template_info
     */
    public String getTemplate_info()
    {
        return template_info;
    }

    @Override
    public String toString()
    {
        return template_type + " " + template_info;
    }
}
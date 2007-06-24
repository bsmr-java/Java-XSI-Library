package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.ListIterator;

/**
 * This class is a container for a template in the dotXSI file format, as
 * specified by XSIFTK template reference.
 * <p>
 * Specifies a shape: vertex positions, normals, colors, and (for version 3.5
 * and beyond) texture coordinates.
 * </p>
 * <p>
 * For versions 3.5 and later, TEX_COORD_UV# indicates that the shape contains a
 * set of UVs. TEX_COORD_UV0, … TEX_COORD_UVn extends the SI_Shape template. The
 * name of the UV domain follows the TEX_COORD_UV ID.
 * </p>
 * <p>
 * There are two possible layouts for the information:
 * </p>
 * <p>
 * Ordered</span>—used to define an original shape
 * </p>
 * <p>
 * Indexed</span>—used to specify shape animation in SI_ShapeAnimation
 * </p>
 * <p>
 * It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Shape extends Template
{
	public static final String POSITION = "POSITION";
	public static final String NORMAL = "NORMAL";
	public static final String COLOR = "COLOR";
	public static final String TEX_COORD_UV = "TEX_COORD_UV";
	
	public static final String INDEXED  = "INDEXED";
	public static final String ORDERED = "ORDERED";

    /**
     * Number of arrays in the template. There is one array for every kind of
     * information.
     */
    public int nbShapeArrays;
    /**
     * Specifies how the information is laid out in the template. Possible
     * values are:<br>
     * • ORDERED = Defines an original shape.<br>
     * • INDEXED = Specifies shape animation in SI_ShapeAnimation.
     */
    public String layout;
    /** Array of {@link ShapeArrays }. */
    public ShapeArray[] shapeArrays;
    public boolean isIndexed; 

    /**
     * TODO XSI v3.5 added material to the Shape Array so that needs to be supported
     */
	public class ShapeArray implements Serializable
	{
        /**
         * Number of this kind of element in the array. For example, there is
         * one array of position values for each vertex in the template.
         */
		public int nbElements;
        /**
         * Specifies what kind of information appears in this section. Possible
         * values are: <br>
         * • POSITION = Array of positions for vertices. <br>
         * • NORMAL = Array of normals information (see ni below). <br>
         * • COLOR = Template contains Color information (see ci below).
         * • TEX_COORD_UV# = Template contains texture UV coordinates information.
         * The number sign (#) represents the number of the texture UV coordinates
         * starting at 0. Note: The TEX_COORD_UV# element is only available in
         * v3.5 and beyond.
         */
		public String elements;
        /**
         * Index (zero-based) of an element (position, normal, color, or uv
         * coordinate) in the corresponding array of the original shape. Note:
         * This is used for indexed forms in shape animation templates only .
         */
		public int[] indexes;
        /**
         * The structure of the array depends on what element was specified for
         * this block of data:<br>
         * • For POSITION, value_arrayi takes the form: <posxi>,<posyi>,<poszi>.<br>
         * • For NORMAL, value_arrayi takes the form: <xi>,<yi>,<zi>.<br>
         * • For COLOR, value_arrayi takes the form: <ri>,<gi>,<bi>,<wi>.<br>
         * • For TEX_COORD_UV#, value_arrayi takes the form: <ui>,<vi>. <br>
         * Note: The TEX_COORD_UV# element is only available in v3.5 and beyond.
         */
		public float[] values;

		/**
         * 
         * @param type
         * @return boolean
		 */
        public boolean isType(String type)
		{
			return elements.startsWith(type);
		}
	}

    @Override
    public void parse(RawTemplate block)
    {
        ListIterator it = block.values.listIterator();
        nbShapeArrays = ((Integer)it.next()).intValue();
		layout = (String)it.next();
		isIndexed = layout.equals(INDEXED);
		
		shapeArrays = new ShapeArray[nbShapeArrays];
		for (int i=0; i<nbShapeArrays; i++)
		{
			shapeArrays[i] = new ShapeArray();
			shapeArrays[i].nbElements = ((Integer)it.next()).intValue();
			shapeArrays[i].elements = (String)it.next();

			int len = 0;			
			if (shapeArrays[i].isType(POSITION)) len = 3; // x, y, z 
			if (shapeArrays[i].isType(NORMAL)) len = 3; // x, y, z 
			if (shapeArrays[i].isType(COLOR)) len = 4; // r, g, b, a
			if (shapeArrays[i].isType(TEX_COORD_UV)) len = 2; // u, v
			
			shapeArrays[i].values = new float[shapeArrays[i].nbElements*len];
			if (isIndexed)
			{
				shapeArrays[i].indexes = new int[shapeArrays[i].nbElements];
			}
			
			Object o = it.next();
			if (o instanceof Float)
			{
				it.previous();
			}
			else
			{
//				System.out.println("Skipping "+o);
			}

			for (int j=0; j<shapeArrays[i].nbElements; j++)
			{
				if (isIndexed)
				{
					shapeArrays[i].indexes[j] = ((Number)it.next()).intValue();
				}

				for (int k=0; k<len; k++)
				{
					shapeArrays[i].values[j*len+k] = ((Float)it.next()).floatValue();
				}
			}
		}
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString() + ", Number of Shapes: " + nbShapeArrays;
    }
}
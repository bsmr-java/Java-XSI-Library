package com.mojang.joxsi.renderer.shaders;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GL;

/**
 * Wrapper for a GLSL shader program.
 * <code>
 * Program program = new Program();
 * program.addVertexShader(gl, ShaderSourceCode.fromResource("/programs/sample.vertex.shader"));
 * program.addFragmentShader(gl, ShaderSourceCode.fromResource("/programs/sample.fragment.shader"));
 * this.program = program.link(gl);
 * </code>
 * 
 * @author Notch
 * @author Egal TODO if driver supports OpenGL 2.0 then use new functions for
 *         compiling, linking, attaching programs. gl.glGetString(GL.GL_VERSION)
 *         returns the OpenGL version
 * @author Martinus added Enable, Disable and getAttribLocation to be for more generic use
 */
public class Program
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(Program.class.getName());

    /** Buffer for getting the shader and program info log. */
    private static final byte[] b = new byte[65536];

    private static final int[] res = new int[1];

    private ArrayList<Integer> shaders = new ArrayList<Integer>();

    private String linkResult = "";

    private String lastCompilationResult = "";

    private boolean linked = false;

    /**
     * Does this graphics driver support OpenGL so we can use the new functions
     * for using shaders and programs, including creating, linking, and
     * compiling.<br>
     */
    private static boolean hasOpenGL2 = false;

    /**
     * Use OpenGL 1 Extension functions for compiling and linking GLSL shaders
     * and programs.
     */
    public Program()
    {
        this(false);
    }

    /**
     * 
     * @param useOpenGL2
     *            when <code>true</code> then use OpenGL 2 functions for
     *            compiling and linking GLSL shaders and programs. When
     *            <code>false</code> use OpenGL 1 Extension functions for
     *            compiling and linking GLSL shaders and programs.
     */
   public Program(final boolean useOpenGL2)
   {
       hasOpenGL2 = useOpenGL2;
       if (logger.isLoggable(Level.CONFIG))
       {
           if (hasOpenGL2)
               logger.config("Will use OpenGL 2 functions for compiling and linking GLSL shaders and programs");
           else
               logger.config("Will use OpenGL 1 Extension functions for compiling and linking GLSL shaders and programs");
       }
   }

    /**
     * 
     * @param gl
     * @param vertexShaderSource
     * @throws ShaderCompilationError
     */
    public void addVertexShader(GL gl, ShaderSourceCode vertexShaderSource) throws ShaderCompilationError
    {
        if (linked)
            throw new ShaderCompilationError("Can't add shaders to a linked program.");

        int shaderObject;
        if (hasOpenGL2)
        {
            shaderObject = compileShader(gl, vertexShaderSource, GL.GL_VERTEX_SHADER);
        }
        else
        {
            shaderObject = compileShader(gl, vertexShaderSource, GL.GL_VERTEX_SHADER_ARB);
        }
        shaders.add(new Integer(shaderObject));
    }

    /**
     * 
     * @param gl
     * @param fragmentShaderSource
     * @throws ShaderCompilationError
     */
    public void addFragmentShader(GL gl, ShaderSourceCode fragmentShaderSource) throws ShaderCompilationError
    {
        if (linked)
            throw new ShaderCompilationError("Can't add shaders to a linked program.");

        int shaderObject;
        if (hasOpenGL2)
        {
            shaderObject = compileShader(gl, fragmentShaderSource, GL.GL_FRAGMENT_SHADER);
        }
        else
        {
            shaderObject = compileShader(gl, fragmentShaderSource, GL.GL_FRAGMENT_SHADER_ARB);
        }
        shaders.add(new Integer(shaderObject));
    }

    /**
     * Links program.
     * 
     * @param gl
     * @return program object ID
     * @throws ProgramLinkException
     *             TODO If OpenGL version >= 2.0 glValidateProgram,
     *             glGetProgramInfoLog
     */
    public int link(GL gl) throws ProgramLinkException
    {
        if (linked)
            throw new ProgramLinkException("Can't link a linked program.");
        if (shaders.size() == 0)
            throw new ProgramLinkException("Can't link a program with 0 shaders.");
        linked = true;
        int programObject;

        if (hasOpenGL2)
        {
            programObject = gl.glCreateProgram();
            for (int i = 0; i < shaders.size(); i++)
                gl.glAttachShader(programObject, shaders.get(i).intValue());
            gl.glLinkProgram(programObject);
            gl.glValidateProgram(programObject);

            // Get the program link result
            gl.glGetProgramInfoLog(programObject, b.length, new int[]
            { 0 }, 0, b, 0);
            gl.glGetProgramiv(programObject, GL.GL_LINK_STATUS, res, 0);
            gl.glValidateProgram(programObject);
            String report = new String(b).trim();
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Link result for program object " + programObject + ": " + report);
                int[] result = new int[10];
                logger.fine("link - glIsProgram: " + gl.glIsProgram(programObject));
                gl.glGetProgramiv(programObject, GL.GL_LINK_STATUS, result, 0);
                logger.fine("link - GL_LINK_STATUS: " + result[0]);
                gl.glGetProgramiv(programObject, GL.GL_VALIDATE_STATUS, result, 0);
                logger.fine("link - GL_VALIDATE_STATUS: " + result[0]);
                gl.glGetProgramiv(programObject, GL.GL_INFO_LOG_LENGTH, result, 0);
                logger.fine("link - GL_INFO_LOG_LENGTH: " + result[0]);
                gl.glGetProgramiv(programObject, GL.GL_ATTACHED_SHADERS, result, 0);
                logger.fine("link - GL_ATTACHED_SHADERS: " + result[0]);
                int[] count = new int[1];
                gl.glGetAttachedShaders(programObject, result.length, count, 0, result, 0);
                // TODO
                for (int i : result)
                {
                    if (i != 0)
                        logger.fine("link - glGetAttachedShaders: " + i);
                }
                gl.glGetProgramiv(programObject, GL.GL_ACTIVE_ATTRIBUTES, result, 0);
                logger.fine("link - GL_ACTIVE_ATTRIBUTES: " + result[0]);
                // TODO glGetActiveAttrib
                gl.glGetProgramiv(programObject, GL.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, result, 0);
                logger.fine("link - GL_ACTIVE_ATTRIBUTE_MAX_LENGTH: " + result[0]);
                gl.glGetProgramiv(programObject, GL.GL_ACTIVE_UNIFORMS, result, 0);
                logger.fine("link - GL_ACTIVE_UNIFORMS: " + result[0]);
                // TODO glGetActiveUniform
                gl.glGetProgramiv(programObject, GL.GL_ACTIVE_UNIFORM_MAX_LENGTH, result, 0);
                logger.fine("link - GL_ACTIVE_UNIFORM_MAX_LENGTH: " + result[0]);
                gl.glGetProgramiv(programObject, GL.GL_DELETE_STATUS, result, 0);
                logger.fine("link - GL_DELETE_STATUS: " + result[0]);
            }
            if (res[0] == 0)
                throw new ProgramLinkException(report);
            linkResult = report;
        }
        else
        {
            programObject = gl.glCreateProgramObjectARB();
            for (int i = 0; i < shaders.size(); i++)
                gl.glAttachObjectARB(programObject, shaders.get(i).intValue());
            gl.glLinkProgramARB(programObject);

            // Get the program link result
            gl.glGetInfoLogARB(programObject, b.length, new int[]
            { 0 }, 0, b, 0);
            gl.glGetObjectParameterivARB(programObject, GL.GL_OBJECT_LINK_STATUS_ARB, res, 0);
            gl.glValidateProgramARB(programObject);
            String report = new String(b).trim();
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Link result for program object " + programObject + ": " + report);
            }
            if (res[0] == 0)
                throw new ProgramLinkException(report);
            linkResult = report;
        }
        return programObject;
    }

    /**
     * Compile Shader.
     * 
     * @param gl
     * @param shaderSource
     * @param type
     * @return Shader object ID
     * @throws ShaderCompilationError
     *             TODO If OpenGL version >= 2.0 use gl.glGetShaderInfoLog()
     */
    private int compileShader(GL gl, ShaderSourceCode shaderSource, int type) throws ShaderCompilationError
    {
        int shaderObject;

        if (hasOpenGL2)
        {
            shaderObject = gl.glCreateShader(type);
            gl.glShaderSource(shaderObject, shaderSource.stringCount, shaderSource.strings, shaderSource.stringLengths, 0);
            gl.glCompileShader(shaderObject);

            // Get the shader compile result
            gl.glGetInfoLogARB(shaderObject, b.length, new int[]
            { 0 }, 0, b, 0);
            gl.glGetObjectParameterivARB(shaderObject, GL.GL_OBJECT_COMPILE_STATUS_ARB, res, 0);
            String report = new String(b).trim();

            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Compile result for shader object " + shaderObject + ": " + report);
                int[] result = new int[1];
                logger.fine("compileShader - glIsShader: " + gl.glIsShader(shaderObject));
                gl.glGetShaderiv(shaderObject, GL.GL_SHADER_TYPE, result, 0);
                if (result[0] == GL.GL_VERTEX_SHADER)
                    logger.fine("compileShader - GL_SHADER_TYPE: Vertex Shader");
                else if (result[0] == GL.GL_FRAGMENT_SHADER)
                    logger.fine("compileShader - GL_SHADER_TYPE: Fragment Shader");
                else
                    logger.warning("compileShader - unknown Shader type: " + result[0]);
                gl.glGetShaderiv(shaderObject, GL.GL_SHADER_SOURCE_LENGTH, result, 0);
                logger.fine("compileShader - GL_SHADER_SOURCE_LENGTH: " + result[0]);
            }
            if (res[0] == 0)
                throw new ShaderCompilationError(report);
            linkResult = report;
        }
        else
        {
            shaderObject = gl.glCreateShaderObjectARB(type);
            gl.glShaderSourceARB(shaderObject, shaderSource.stringCount, shaderSource.strings, shaderSource.stringLengths, 0);
            gl.glCompileShaderARB(shaderObject);

            gl.glGetInfoLogARB(shaderObject, b.length, new int[]
            { 0 }, 0, b, 0);
            gl.glGetObjectParameterivARB(shaderObject, GL.GL_OBJECT_COMPILE_STATUS_ARB, res, 0);
            String report = new String(b).trim();
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Compile result for shader object " + shaderObject + ": " + report);
            }
            if (res[0] == 0)
                throw new ShaderCompilationError(report);
            lastCompilationResult = report;
        }
        return shaderObject;
    }
    
    /**
     * Gets location (index) of the attribute in the shaderObject
     * 
     * @param gl
     * @param index of the shaderObject, returned by link(gl);
     * @param name of the attribute
     * @return Shader object ID
     */
    public int getAttribLocation(GL gl, int shaderIndex, String attribName)
    {
        if (hasOpenGL2)
        {
            //TODO add openGL2 way of enabling shader here
        }
        else 
        {
            return gl.glGetAttribLocationARB(shaderIndex, attribName);
        }    
        return 0;
    }
    
    /**
     * Enables shader based on openGL version.
     * 
     */
    
    public void Enable(GL gl, int shaderIndex)
    {
        if (hasOpenGL2)
        {
            //TODO add openGL2 way of enabling shader here
        }
        else 
        {
            gl.glUseProgramObjectARB(shaderIndex);
        }        
    }
    
    /**
     * Disables shader based on openGL version.
     * 
     */
    
    public void Disable(GL gl)
    {
        if (hasOpenGL2)
        {
            //TODO add openGL2 way of disabling shader here
        }
        else
        {
            gl.glUseProgramObjectARB(0);
        }
    }

    /**
     * Returns last compilation report.
     * 
     * @return last compilation report
     */
    public String getLastCompilationReport()
    {
        return lastCompilationResult;
    }

    /**
     * Returns link report.
     * 
     * @return link report
     */
    public String getLinkReport()
    {
        return linkResult;
    }
}
package com.mojang.joxsi;

import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * Test Compatibility of the graphiccard and loads the shaders
 * 
 * @author Milbo
 * 
 */
public class GLSLshaders
{

    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(GLSLshaders.class.getName());

    GL gl = null;
    public boolean fragmentShaderSupported;
    public boolean vertexShaderSupported;
    public int programObjectVertex;
    public int programObjectFragment;
    public int waveAttrib;
    public int textureAttrib;

    public List<GLSLshaders> shaderVector = new Vector<GLSLshaders>();

    public GLSLshaders(GL gl)
    {
        this.gl = gl;
        chooseProfiles();
    }

    // GL_ARB_depth_texture GL_ARB_fragment_program
    // GL_ARB_fragment_program_shadow
    // GL_ARB_fragment_shader GL_ARB_half_float_pixel GL_ARB_imaging
    // GL_ARB_multisample
    // GL_ARB_multitexture GL_ARB_occlusion_query GL_ARB_point_parameters
    // GL_ARB_point_sprite
    // GL_ARB_shadow GL_ARB_shader_objects GL_ARB_shading_language_100
    // GL_ARB_texture_border_clamp
    // GL_ARB_texture_compression GL_ARB_tex

    private void chooseProfiles()
    {
        String[] vertexSURL = null;
        String[] fragmentSURL = null;

        String extensions = gl.glGetString(GL.GL_EXTENSIONS);
        logger.info("Your supported extensions: " + extensions);
        if (vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1)
        {
            logger.info("GL_ARB_vertex_shader is supported");
            vertexSURL = new String[]
            { "wave.glsl" };
        }
        if (fragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1)
        {
            logger.info("GL_ARB_fragment_shader is supported");
            fragmentSURL = new String[]
            { "directTexture.glsl" };
        }
        if (extensions.indexOf("GL_ARB_multitexture") != -1)
        {
            // loadShaders(gl,"shaderthatneedsmultitexture",true);
        }

        // TODO:  Make a graceful message if the shader doesn't work correctly
        // this is just a hack to make it not load shader atm
        // lando
        if (vertexSURL != null && fragmentSURL != null)
        {
            loadShaderProgram(vertexSURL, fragmentSURL);
        }
    }

    private void loadShaderProgram(String[] vertexSURL, String[] fragmentSURL)
    {

        int shaderprogram = gl.glCreateProgram();

        for (int i = 0; i < vertexSURL.length; i++)
        {
            String vsrc = loadVertexShader(vertexSURL[i]);
            int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
            gl.glShaderSource(v, 1, new String[]
            { vsrc }, null);
            gl.glCompileShader(v);
            gl.glAttachShader(shaderprogram, v);

            programObjectVertex = gl.glCreateProgramObjectARB();
            gl.glAttachObjectARB(programObjectVertex, v);
            gl.glLinkProgramARB(programObjectVertex);
            gl.glValidateProgramARB(programObjectVertex);
            checkLogInfo(gl, v);
            String name = vertexSURL[i].replaceAll(".glsl", "");
            if (name.equals("wave"))
            {
                waveAttrib = gl.glGetAttribLocationARB(programObjectVertex, name);
            }
        }

        for (int i = 0; i < fragmentSURL.length; i++)
        {
            String fsrc = loadFragmentShader(fragmentSURL[i]);
            int f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
            gl.glShaderSource(f, 1, new String[]
            { fsrc }, null);
            gl.glCompileShader(f);
            gl.glAttachShader(shaderprogram, f);

            // programObjectFragment = gl.glCreateProgramObjectARB();
            gl.glAttachObjectARB(programObjectVertex, f);
            gl.glLinkProgramARB(programObjectVertex);
            gl.glValidateProgramARB(programObjectVertex);
            checkLogInfo(gl, f);
            // String name = vertexSURL[i].replaceAll(".glsl", "");
            // if(fragmentSURL[i].equals("directTexture")){
            // textureAttrib = gl.glGetAttribLocationARB(programObjectVertex,
            // name);
            // }
            gl.glLinkProgram(shaderprogram);
            gl.glValidateProgram(shaderprogram);
            gl.glUseProgram(shaderprogram);
        }

    }

    private String loadVertexShader(String enabledShader)
    {

        BufferedReader brv = getBufferedReader(enabledShader);
        String vsrc = "";
        String line;
        try
        {
            while ((line = brv.readLine()) != null)
            {
                vsrc += line + "\n";
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return vsrc;
    }

    private String loadFragmentShader(String enabledShader)
    {

        BufferedReader brf = getBufferedReader(enabledShader);
        String fsrc = "";
        String line;
        try
        {
            while ((line = brf.readLine()) != null)
            {
                fsrc += line + "\n";
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fsrc;
    }

    /**
     * Important to see the compiling messages
     * 
     * @author nehe
     * @param gl
     * @param obj
     * 
     */
    private void checkLogInfo(GL gl, int obj)
    {
        IntBuffer iVal = BufferUtil.newIntBuffer(1);
        gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);

        int length = iVal.get();

        if (length <= 1)
        {
            return;
        }

        ByteBuffer infoLog = BufferUtil.newByteBuffer(length);

        iVal.flip();
        gl.glGetInfoLogARB(obj, length, iVal, infoLog);

        byte[] infoBytes = new byte[length];
        infoLog.get(infoBytes);
        // just easier to find in black can be deleted
        System.out.println("GLSL Validation >> " + new String(infoBytes));
        // logger.info("GLSL Validation >> " + new String(infoBytes));
    }

    /**
     * Trys to load the file from the jar, if it is not existent it loads it
     * from disk
     * 
     * @author Milbo
     * @param url
     * @return BufferedReader
     */
    public BufferedReader getBufferedReader(String url)
    {

        BufferedReader in = null;
        InputStream fileStream;
        try
        {
            fileStream = ClassLoader.getSystemResourceAsStream(url);
            Reader reader = new InputStreamReader(fileStream); // wrap a Reader
            // around the
            // InputStream
            in = new BufferedReader(reader); // wrap a BufferedReader around
            // the Reader
        }
        catch (Exception e)
        {
            try
            {
                in = new BufferedReader(new FileReader(url));
            }
            catch (FileNotFoundException e1)
            {
                logger.warning("GLSLshaders.getBufferedReader; URL: " + url + " couldnt be found: " + e1);
                in = null;
            }
        }
        return in;
    }

}

package com.mojang.joxsi;

import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;


/** Test Compatibility of the graphiccard and loads the shaders
 * 
 * @author Milbo
 *
 */
public class GLSLshaders {

    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(GLSLshaders.class.getName());
    
    GL gl = null;
	public boolean fragmentShaderSupported;
	public boolean vertexShaderSupported;
	public int programObjectVertex;
    public int programObjectFragment;
	public int waveAttrib;
	public int textureAttrib;
	
//	public Map<String, GLSLshaders> shaderMap = new HashMap<String, GLSLshaders>();
	public Vector<GLSLshaders> shaderVector = new Vector<GLSLshaders>();



	public GLSLshaders(GL gl) {
	    this.gl = gl; 
	    chooseProfiles();
	}

// GL_ARB_depth_texture GL_ARB_fragment_program GL_ARB_fragment_program_shadow 
// GL_ARB_fragment_shader GL_ARB_half_float_pixel GL_ARB_imaging GL_ARB_multisample 
// GL_ARB_multitexture GL_ARB_occlusion_query GL_ARB_point_parameters GL_ARB_point_sprite 
// GL_ARB_shadow GL_ARB_shader_objects GL_ARB_shading_language_100 GL_ARB_texture_border_clamp 
// GL_ARB_texture_compression GL_ARB_tex

	 private void chooseProfiles() {
	    String[] vertexSURL = null;
	    String[] fragmentSURL = null;
	    
	    String extensions = gl.glGetString(GL.GL_EXTENSIONS);
	    logger.info("Your supported extensions: "+extensions);
	    if(vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1){
	        logger.info("GL_ARB_vertex_shader is supported");
	        vertexSURL = new String[]{"wave.glsl"};
//	   	    loadShaders("wave.glsl",true);
	   	     
	    }
	    if(fragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1){
	        logger.info("GL_ARB_fragment_shader is supported");
	            fragmentSURL = new String[]{"directTexture.glsl"};
//	            fragmentSURL = new String[]{};
//	            loadShaders(gl,"someshader",false);        
	    }
	    if(extensions.indexOf("GL_ARB_multitexture") != -1){
//	   	 loadShaders(gl,"shaderthatneedsmultitexture",true);
	    }

	    loadShaderProgram(vertexSURL,fragmentSURL);
	}
	 
	 private void loadShaderProgram(String[] vertexSURL, String[] fragmentSURL){
	     
//        int v, f;
        int shaderprogram = gl.glCreateProgram();
        
        for(int i=0; i < vertexSURL.length;i++){
            String vsrc = loadVertexShader(vertexSURL[i]);
            int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
            gl.glShaderSource(v, 1, new String[]{vsrc}, null);
            gl.glCompileShader(v);  
            gl.glAttachShader(shaderprogram, v);
            //Hmm wie baue ich das jetzt hier ein?
            programObjectVertex = gl.glCreateProgramObjectARB();
            gl.glAttachObjectARB(programObjectVertex, v);
            gl.glLinkProgramARB(programObjectVertex);
            gl.glValidateProgramARB(programObjectVertex);
            checkLogInfo(gl, v);
            String name = vertexSURL[i].replaceAll(".glsl", "");
            if(name.equals("wave")){
                waveAttrib = gl.glGetAttribLocationARB(programObjectVertex, name);
            }
        }
        
        for(int i=0; i < fragmentSURL.length;i++){
            int f = loadFragmentShader(fragmentSURL[i]);
            gl.glAttachShader(shaderprogram, f);
            
//            programObjectFragment = gl.glCreateProgramObjectARB();
            gl.glAttachObjectARB(programObjectVertex, f);
            gl.glLinkProgramARB(programObjectVertex);
            gl.glValidateProgramARB(programObjectVertex);
            checkLogInfo(gl, f);
//            String name = vertexSURL[i].replaceAll(".glsl", "");
//            if(fragmentSURL[i].equals("directTexture")){
//                textureAttrib = gl.glGetAttribLocationARB(programObjectVertex, name);
//            }
            gl.glLinkProgram(shaderprogram);
            gl.glValidateProgram(shaderprogram);
            gl.glUseProgram(shaderprogram);
        }
  
    }

    private String loadVertexShader(String enabledShader) {
        
        int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
        BufferedReader brv = getBufferedReader(enabledShader);
//        BufferedReader brv = new BufferedReader(new FileReader("vertexshader.glsl"));
        String vsrc = "";
        String line;
        try
        {
            while ((line=brv.readLine()) != null) {
                vsrc += line + "\n";
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
     
//        gl.glShaderSource(v, 1, vsrc, (int[])null);
        gl.glShaderSource(v, 1, new String[]{vsrc}, null);
        gl.glCompileShader(v);
                 
        return vsrc;
    }
	   
	 private int loadFragmentShader(String enabledShader){
	     
	     int f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
	     BufferedReader brf = getBufferedReader(enabledShader);
//	     BufferedReader brf = new BufferedReader(new FileReader("fragmentshader.glsl"));
	     String fsrc = "";
	     String line;
	     try
        {
            while ((line=brf.readLine()) != null) {
               fsrc += line + "\n";
             }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//	     gl.glShaderSource(f, 1, new String[]{fsrc}, null);
	     gl.glShaderSource(f, 1, new String[]{fsrc}, null);
	     gl.glCompileShader(f);
	     
	     return f;
	 }

	
	/**
	 * @author nehe changed by Milbo
	 * @param gl
	 */
	private void loadShaders(String enabledShader, boolean Vertex) {
	
		String name;
		String shaderSource;
		if(Vertex){
			logger.info("Loading VertexShader: "+enabledShader);
		}else{
			logger.info("Loading FragmentShader: "+enabledShader);
		}
		
//	   for (int i=0;i<enabledShaders.length;i++){
	      	
		   try {
		   	BufferedReader shaderReader = getBufferedReader(enabledShader);
		   	StringWriter shaderWriter = new StringWriter();
		
		   	String line = shaderReader.readLine();
		   	while (line != null) {
		        shaderWriter.write(line);
		        shaderWriter.write("\n");
		        line = shaderReader.readLine();
		   	}
		   	shaderSource = shaderWriter.getBuffer().toString();
			} catch (IOException e) {
			    throw new RuntimeException(e);
			}
		
			if (shaderSource != null) {
				int shader;
				if(Vertex){
					shader = gl.glCreateShaderObjectARB(GL.GL_VERTEX_SHADER_ARB);
				}else{
					shader = gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
				}
	
				gl.glShaderSourceARB(shader, 1, new String[]{shaderSource}, (int[]) null, 0);
				
				gl.glCompileShaderARB(shader);
				checkLogInfo(gl, shader);
				
				programObjectVertex = gl.glCreateProgramObjectARB();
				
				gl.glAttachObjectARB(programObjectVertex, shader);
				gl.glLinkProgramARB(programObjectVertex);
				gl.glValidateProgramARB(programObjectVertex);
				checkLogInfo(gl, programObjectVertex);
 
				name = enabledShader.replaceAll(".glsl", "");
				waveAttrib = gl.glGetAttribLocationARB(programObjectVertex, name);
				
//				shaderMap.put(name, this);
//				shaderVector.add(this);
	      }
//	   }
	}
	

	
	/**
	 * 
	 * @author nehe
	 * @param gl
	 * @param obj
	 */
	private void checkLogInfo(GL gl, int obj) {
	   IntBuffer iVal = BufferUtil.newIntBuffer(1);
	   gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, iVal);
	
	   int length = iVal.get();
	
	   if (length <= 1) {
	       return;
	   }
	
	   ByteBuffer infoLog = BufferUtil.newByteBuffer(length);
	
	   iVal.flip();
	   gl.glGetInfoLogARB(obj, length, iVal, infoLog);
	
	   byte[] infoBytes = new byte[length];
	   infoLog.get(infoBytes);
	   //just easier to find in black can be deleted 
	   System.out.println("GLSL Validation >> " + new String(infoBytes));
//	   logger.info("GLSL Validation >> " + new String(infoBytes));
	}

	/**
	 * Trys to load the file from the jar, if it is not existent it loads it from disk
	 * @author Milbo
	 * @param url
	 * @return BufferedReader
	 */
	public BufferedReader getBufferedReader (String url){
		
		BufferedReader in = null;
		InputStream fileStream;
		try {
			fileStream = ClassLoader.getSystemResourceAsStream(url);
			Reader reader = new InputStreamReader(fileStream); //wrap a Reader around the InputStream
			in = new BufferedReader(reader); //wrap a BufferedReader around the Reader
		} catch (Exception e) {
		try {
			in = new BufferedReader(new FileReader(url));
		} catch (FileNotFoundException e1) {
			logger.warning("GLSLshaders.getBufferedReader; URL: "+url+" couldnt be found: "+e1);
				in = null;
			}	
		}
		return in;
	}
	
}

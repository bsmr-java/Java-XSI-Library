package com.mojang.joxsi;

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

public class GLSLshaders {

	public boolean fragmentShaderSupported;
	public boolean vertexShaderSupported;
	public int programObject;
	public int waveAttrib;

//	public Map<String, GLSLshaders> shaderMap = new HashMap<String, GLSLshaders>();
	public Vector<GLSLshaders> shaderVector = new Vector<GLSLshaders>();

	public GLSLshaders(GL gl) {
		
      chooseProfiles(gl);
	}

// GL_ARB_depth_texture GL_ARB_fragment_program GL_ARB_fragment_program_shadow 
// GL_ARB_fragment_shader GL_ARB_half_float_pixel GL_ARB_imaging GL_ARB_multisample 
// GL_ARB_multitexture GL_ARB_occlusion_query GL_ARB_point_parameters GL_ARB_point_sprite 
// GL_ARB_shadow GL_ARB_shader_objects GL_ARB_shading_language_100 GL_ARB_texture_border_clamp 
// GL_ARB_texture_compression GL_ARB_tex

	 private void chooseProfiles(GL gl) {
			
	    String extensions = gl.glGetString(GL.GL_EXTENSIONS);
	    System.out.println("Your supported extensions: "+extensions);
	    if(fragmentShaderSupported = extensions.indexOf("GL_ARB_fragment_shader") != -1){
		    //loadShaders(gl,"someshader",false);	   	 
	    }
	    if(vertexShaderSupported = extensions.indexOf("GL_ARB_vertex_shader") != -1){
	   	 loadShaders(gl,"wave.glsl",true);
	    }
	    if(extensions.indexOf("GL_ARB_multitexture") != -1){
//	   	 loadShaders(gl,"shaderthatneedsmultitexture",true);
	    }

	}
	
	
	/**
	 * @author nehe changed by Milbo
	 * @param gl
	 */
	private void loadShaders(GL gl, String enabledShader, boolean Vertex) {
	
		String name;
		String shaderSource;
		if(Vertex){
			System.out.println("Loading VertexShader: "+enabledShader);
		}else{
			System.out.println("Loading FragmentShader: "+enabledShader);
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
				
				programObject = gl.glCreateProgramObjectARB();
				
				gl.glAttachObjectARB(programObject, shader);
				gl.glLinkProgramARB(programObject);
				gl.glValidateProgramARB(programObject);
				checkLogInfo(gl, programObject);
 
				name = enabledShader.replaceAll(".glsl", "");
				waveAttrib = gl.glGetAttribLocationARB(programObject, name);
				
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
	   System.out.println("GLSL Validation >> " + new String(infoBytes));
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
			System.out.println("GLSLshaders.getBufferedReader; URL: "+url+" couldnt be found: "+e1);
				in = null;
			}	
		}
		return in;
	}
	
}

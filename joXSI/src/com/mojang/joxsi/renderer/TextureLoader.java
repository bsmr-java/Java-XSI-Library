package com.mojang.joxsi.renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;

/**
 * A simple textureloader.
 */
public class TextureLoader
{
    private GL gl;
    private GLU glu;
    private Map loadedTextures = new HashMap();

    /**
     * Creates a new TextureLoader
     * 
     * @param gl a valid GL object
     * @param glu a valid GLU object
     */
    public TextureLoader(GL gl, GLU glu)
    {
        this.gl = gl;
        this.glu = glu;
    }

    /**
     * Loads a texture by name, and return its opengl texture object id
     * 
     * <p>Because this method gets called once per frame for all textures, it has to
     * return as fast as possible.<br>
     * This class caches all loaded textures in a Map, and checks if its already loaded before loading the texture.   
     * 
     * @param textureName the name the name of the texture to load. this is assumed to be a filename
     * @return the texture object id, or -1 if the texture couldn't be loaded
     */
    public int loadTexture(String textureName)
    {
        // Check if an id already exists for this texture name
        Integer i = (Integer)loadedTextures.get(textureName);
        if (i != null)
        {
            // Yes, it has been loaded before. Return that id.
            return i.intValue();
        }
        else
        {
            // Nope.. let's load the texture..
            try
            {
                final InputStream lTextureAsStream = getClass().getResourceAsStream("/"+textureName);
                if (lTextureAsStream == null)
                {
                    System.out.println("Could not read texture: " + textureName);
                    loadedTextures.put(textureName, Integer.valueOf(-1));
                    return -1;
                }
                // Load the texture image
                BufferedImage img = ImageIO.read(lTextureAsStream);
                // Rip the bytes from the texture into a byte buffer
                ByteBuffer buffer = ripImage(img);

                // Generate a new texture object
                int[] tmp = new int[1];
                gl.glGenTextures(1, tmp, 0);
                int id = tmp[0];
                
                // Bind the texture and set up mipmapping
                boolean hasAlpha = img.getColorModel().hasAlpha();
                int width = img.getWidth();
                int height = img.getHeight();
                gl.glBindTexture(GL.GL_TEXTURE_2D, id);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST);
                
                // Upload the texture data and build mipmaps
                if (hasAlpha)
                {
                    glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 4, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer.position(0));
                }
                else
                {
                    glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 3, width, height, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer.position(0));
                }
                
                // Put the texture object id in the map, and return it.
                loadedTextures.put(textureName, Integer.valueOf(id));
                System.out.println("Loaded texture id: " + id + " - " + textureName);
                return id;
            }
            catch (IOException e)
            {
                // Something went wrong! Log and remember that the texture by that name can't be loaded.
                e.printStackTrace();
                loadedTextures.put(textureName, Integer.valueOf(-1));
                return -1;
            }
        }
    }

    /**
     * Converts an image into an opengl friendly format, and reads the byte data.
     * 
     * @param img the image to rip
     * @return the ripped image, as a direct ByteBuffer
     */
    private ByteBuffer ripImage(BufferedImage img)
    {
        boolean hasAlpha = img.getColorModel().hasAlpha();
        int width = img.getWidth();
        int height = img.getHeight();

        // Create a new BufferedImage in a format opengl likes.
        BufferedImage targetImage;
        if (hasAlpha)
        {
            targetImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }
        else
        {
            targetImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }

        // Render the original image upside down to the new image.
        Graphics2D gr = (Graphics2D)targetImage.getGraphics();
        gr.scale(1, -1);
        gr.translate(0, -height);
        gr.setComposite(AlphaComposite.Src); // Makes sure the alpha channel gets transferred
        gr.drawImage(img, 0, 0, null);
        gr.dispose();

        // Get the raw bytes and put them in a direct buffer.
        byte[] data = (byte[])targetImage.getRaster().getDataElements(0, 0, width, height, null);
        ByteBuffer bb = BufferUtil.newByteBuffer(data.length);
        bb.put(data);
        return bb;
    }
}
package com.mojang.joxsi.renderer;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;

import com.mojang.joxsi.Envelope;
import com.mojang.joxsi.Material;
import com.mojang.joxsi.Model;
import com.mojang.joxsi.Scene;
import com.mojang.joxsi.TriangleList;
import com.mojang.joxsi.loader.SI_Transform;

/**
 * A simple JOGL based renderer class.
 */
public class JoglSceneRenderer
{
    private EnvelopeBuilder envelopeBuilder;
    private TextureLoader textureLoader;
    private GL gl;
    private Map<String, Scene> nullattachmentMap =  new HashMap<String, Scene>();

    /**
     * Creates a new JoglSceneRenderer
     * 
     * @param gl
     *        a valid GL object for rendering
     * @param textureLoader
     *        the textureloader to be used for loading textures.
     */
    public JoglSceneRenderer(GL gl, TextureLoader textureLoader)
    {
        this.gl = gl;
        this.textureLoader = textureLoader;
        envelopeBuilder = new EnvelopeBuilder();
    }

    /**
     * Recursively renders a model and all its sub models.
     * 
     * @param model
     *        the model to be rendered
     */
    private void renderModel(Model model)
    {
        // If the model has an active transform, push the matrix and apply it.
        if (model.animated != null)
        {
            gl.glPushMatrix();
            applyTransform(model.animated);
        }
        if (nullattachmentMap.containsKey(model.name)) 
        {
            render(nullattachmentMap.get(model.name));
        }
        
        // Render all trianglelists in all meshes.
        for (int i = 0; i < model.meshes.length; i++)
        {
            for (int j = 0; j < model.meshes[i].triangleLists.length; j++)
            {
                renderTriangleList(model.meshes[i].triangleLists[j]);
            }
        }

        // Render all sub models
        for (int i = 0; i < model.models.length; i++)
        {
            renderModel(model.models[i]);
        }

        // Pop the matrix if needed before returning.
        if (model.animated != null)
        {
            gl.glPopMatrix();
        }
    }

    /**
     * Switches to the specified material
     * 
     * @param material
     *        the material to switch to.
     */
    private void setupMaterial(Material material)
    {
        if (material.imageName != null)
        {
            // This material has a texture, so load it and bind it.
            int id = textureLoader.loadTexture(material.imageName);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glBindTexture(GL.GL_TEXTURE_2D, id);
        }
        else
        {
            // No texture, disable TEXTURE_2D
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    /**
     * Renders a trianglelist. This is where the actual rendering takes place.
     * 
     * @param triangleList
     *        the trianglelist to be rendered
     */
    private void renderTriangleList(TriangleList triangleList)
    {
        if (triangleList.vertices == 0) return; // This should probably be done before calling this method

        // Switch to the right material.
        setupMaterial(triangleList.scene.getMaterial(triangleList.material));

        // Build the buffers for the triangle list.
        // This applies all envelopes if there are any
        envelopeBuilder.buildBuffers(triangleList);

        // Set up the active arrays.
        // This can be optimised into some kind of state control to minimise state changes.
        // Vertex array pointer
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, envelopeBuilder.vertexBuffer2.position(0));

        // Normal array pointer
        if (triangleList.hasNormals)
        {
            gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            gl.glNormalPointer(GL.GL_FLOAT, 0, envelopeBuilder.normalBuffer2.position(0));
        }

        // Color array pointer
        if (triangleList.hasColors)
        {
            gl.glEnableClientState(GL.GL_COLOR_ARRAY);
            gl.glColorPointer(4, GL.GL_FLOAT, 0, envelopeBuilder.colorBuffer2.position(0));
        }

        // Texture unit pointers. This won't work too good yet, as the material only binds a a texture to the first texture unit.
        for (int t = 0; t < 4; t++)
        {
            if (triangleList.hasTexCoords[t])
            {
                gl.glClientActiveTexture(GL.GL_TEXTURE0 + t);
                gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, envelopeBuilder.texCoordBuffers2[t].position(0));
                gl.glEnable(GL.GL_TEXTURE_2D);
            }
            gl.glClientActiveTexture(GL.GL_TEXTURE0);
        }

        // Render the arrays! This is where it all happens, everything else is just setup. ;)
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, triangleList.vertices);

        // Disable all vertex array states
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        if (triangleList.hasNormals) gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
        if (triangleList.hasColors) gl.glDisableClientState(GL.GL_COLOR_ARRAY);
        for (int t = 0; t < 4; t++)
        {
            if (triangleList.hasTexCoords[t])
            {
                gl.glClientActiveTexture(GL.GL_TEXTURE0 + t);
                gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
                gl.glDisable(GL.GL_TEXTURE_2D);
            }
            gl.glClientActiveTexture(GL.GL_TEXTURE0);
        }
    }

    /**
     * Builds the deformer (bone) matrix for an envelope.
     * 
     * @param envelope
     *        the envelope to build the deformer matrix for.
     */
    private void updateDeformerMatrix(Envelope envelope)
    {
        // How to calculate the deformer matrix in six simple steps: 
        // 1. Start with the identity matrix
        // 2. Remove the current local transform of the envelope (skin mesh)
        // 3. Add the current global transform of the deformer (bone)
        // 4. Remove the global basepose transform of the deformer
        // 5. Add the current local transform of the envelope
        // 6. Copy the modelview matrix into the deformer matrix in the envelope

        gl.glLoadIdentity();
        applyInverseTransform(envelope.envelopeModel.animated);
        recursivelyTransformTo(envelope.deformerModel);
        applyInverseTransform(envelope.deformerModel.basepose);
        applyTransform(envelope.envelopeModel.animated);
        gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, envelope.deformationMatrix, 0);
    }

    /**
     * Recursively transforms to a model by recursively transforming to its parents's local transform, then the model's.
     * <p>
     * This effectively adds the global transform of the model.
     * 
     * @param model
     *        the model to transform to
     */
    private void recursivelyTransformTo(Model model)
    {
        if (model.parent != null) recursivelyTransformTo(model.parent);
        applyTransform(model.animated);
    }

    /**
     * Applies a transform to the current matrix stack.
     * 
     * @param transform
     *        the transform to add
     */
    public void applyTransform(SI_Transform transform)
    {
        gl.glTranslatef(transform.transX, transform.transY, transform.transZ);
        gl.glRotatef(transform.rotZ, 0, 0, 1);
        gl.glRotatef(transform.rotY, 0, 1, 0);
        gl.glRotatef(transform.rotX, 1, 0, 0);
        gl.glScalef(transform.scalX, transform.scalY, transform.scalZ);
    }

    /**
     * Applies a the inverse of atransform to the current matrix stack.
     * 
     * @param transform
     *        the transform to add
     */
    private void applyInverseTransform(SI_Transform transform)
    {
        // Just do applyTransform backwards with inverted or negative values
        gl.glScalef(1 / transform.scalX, 1 / transform.scalY, 1 / transform.scalZ);
        gl.glRotatef(-transform.rotX, 1, 0, 0);
        gl.glRotatef(-transform.rotY, 0, 1, 0);
        gl.glRotatef(-transform.rotZ, 0, 0, 1);
        gl.glTranslatef(-transform.transX, -transform.transY, -transform.transZ);
    }

    /**
     * Renders a scene by recursively rendering all models in the scene after some setup.
     * 
     * @param scene
     *        the scene to be rendered
     */
    public void render(Scene scene)
    {
        // If there are any envelopes, calculate their deformer matrices
        if (scene.envelopes != null)
        {
            // updateDeformerMatrix loads the identity matrix, so push the matrix.
            gl.glPushMatrix();
            {
                for (int i = 0; i < scene.envelopes.length; i++)
                {
                    updateDeformerMatrix(scene.envelopes[i]);
                }
            }
            gl.glPopMatrix();
        }

        // Iterate over all models, and render them
        for (int i = 0; i < scene.models.length; i++)
        {
            renderModel(scene.models[i]);
        }
    }
    
    /**
     * Add an attachment to the list of attachments for NULL attaching
     * @param name the short name of the NULL
     * @param scene containing the model(s) to be attached to a NULL 
     */
    public void addNullAttachment(String name, Scene scene)
    {
        nullattachmentMap.put(name, scene);
    }
    
    /**
     * Remove an attachment from the list of attachments for NULL attaching
     * @param name the short name of the NULL
     */
    public void removeNullAttachment(String name)
    {
        nullattachmentMap.remove(name);
    }
    
}
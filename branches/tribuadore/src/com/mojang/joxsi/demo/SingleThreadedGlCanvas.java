package com.mojang.joxsi.demo;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.util.logging.Level;

import javax.media.opengl.AWTGraphicsConfiguration;
import javax.media.opengl.AWTGraphicsDevice;
import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.glu.GLU;

import com.mojang.joxsi.renderer.shaders.Program;

/**
 * An abstract baseclass for a singlethreaded opengl canvas.
 * 
 * <p>
 * To use it, create a subclass, make it visible by sticking it in a window,
 * then run the run() method from the thread you wish to use for all opengl
 * rendering.<br>
 */
public abstract class SingleThreadedGlCanvas extends Canvas implements Runnable
{
    private GLDrawable drawable;
    private GLContext context;
    private boolean ok = false;

    /**
     * Creates a new SingleThreadedGlCanvas
     */
    public SingleThreadedGlCanvas()
    {
        this(null);
    }

    /**
     * Creates a new SingleThreadedGlCanvas with the specified GLCapabilities
     * 
     * @param capabilities
     *            the GLCapabilities this canvas should have
     */
    public SingleThreadedGlCanvas(GLCapabilities capabilities)
    {
        this(capabilities, null, null, null);
    }

    /**
     * Creates a new SingleThreadedGlCanvas with the specified GLCapabilities,
     * and some other options
     * 
     * @param capabilities
     *            the GLCapabilities this canvas should have
     * @param chooser
     *            a GLCapabilitiesChooser used for selecting a good
     *            GLCapabilities
     * @param shareWith
     *            the canvas should share this context
     * @param device
     *            the canvas should exist in this GraphicsDevice
     */
    public SingleThreadedGlCanvas(GLCapabilities capabilities, GLCapabilitiesChooser chooser, GLContext shareWith,
            GraphicsDevice device)
    {
        super(unwrap((AWTGraphicsConfiguration) GLDrawableFactory.getFactory().chooseGraphicsConfiguration(capabilities,
                chooser, new AWTGraphicsDevice(device))));
        drawable = GLDrawableFactory.getFactory().getGLDrawable(this, capabilities, chooser);
        context = drawable.createContext(shareWith);
    }

    /**
     * Is called by awt when the canvas is realized.
     * 
     * <p>
     * Overridden to be able to detect this event. Passes the event on to the
     * drawable
     */
    @Override
    public void addNotify()
    {
        super.addNotify();
        drawable.setRealized(true);
        ok = true;
    }

    /**
     * Overridden to make sure it's empty.
     */
    @Override
    public void update(Graphics g)
    {
    }

    /**
     * Overridden to make sure it's empty.
     */
    @Override
    public void paint(Graphics g)
    {
    }

    /**
     * Helper method to make processing of AWT related things like input events
     * and repainting happen in some enviroments (like Linux). Calling it once
     * every frame should be enough to make the AWT part of the application
     * process smoothly enough.
     */
    protected void releaseContextAndMakeItcurrentAgain()
    {
        context.release();
        Thread.yield();
        try
        {
            while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT)
            {
                Thread.sleep(10);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Runs the rendering loop.
     * 
     * <p>
     * This is called when the context has been created and made current.<br>
     * This method shouldn't return until the application is closing.
     * 
     * @param gl
     *            a valid GL object
     * @param glu
     *            a valid GLU object
     * @param lshaders
     */
    protected abstract void renderLoop(GL gl, GLU glu, Program shaderProgram);

    /**
     * Swaps the opengl buffers.
     */
    protected void swapBuffers()
    {
        drawable.swapBuffers();
    }

    /**
     * Sets up the context and makes it current, then calls the renderloop
     * method
     */
    public void run()
    {
        try
        {
            // Wait until the canvas is both visible and realized.
            while (!ok || !isVisible())
            {
                Thread.sleep(100);
            }

            // Wait until the context is current
            while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT)
            {
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            // well... this should be handled better, I guess.
            e.printStackTrace();
        }

        try
        {
            // Create GL and GLU objects
            GL gl = context.getGL();
            GLU glu = new GLU();            
            Program shaderProgram = new Program();           
            
            setupGLstates(gl);
            int[] lIntArray =
            { 0, 0 };
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_COORDS, lIntArray, 0);
            if (ModelDisplayer.logger.isLoggable(Level.INFO))
            {
                ModelDisplayer.logger.info("GL_MAX_TEXTURE_COORDS: " + lIntArray[0]);
            }

            // Call render loop
            renderLoop(gl, glu, shaderProgram);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }
        finally
        {
            // Release and destroy the context
            context.release();
            context.destroy();

            // Shut down the jvm.. This probably shouldn't be here.
            // TODO Make sure the rest of the system shuts down 
            // Using more than one canvas so we have to force program shutdown.
            System.exit(0);
        }
    }

    /**
     * Setting Up the GL states could be set in relation to Usersettings and
     * available Graphiccards later
     * 
     * @param gl
     */
    private void setupGLstates(GL gl)
    {
        // Setup GL States
        // Black Background
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // Depth Buffer Setup
        gl.glClearDepth(1.0f);
        // The Type Of Depth Testing (Less Or Equal)
        gl.glDepthFunc(GL.GL_LEQUAL);
        // Enable Depth Testing
        gl.glEnable(GL.GL_DEPTH_TEST);
        // Select Smooth Shading
        gl.glShadeModel(GL.GL_SMOOTH);
        // Set Perspective Calculations To Most Accurate
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        // Draw Our Mesh In Wireframe Mode
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    }

    /**
     * Gets the GraphicsConfiguration of an AWTGraphicsConfiguration, unless the
     * AWTGraphicsConfiguration is null.
     * 
     * @param config
     *            the AWTGraphicsConfiguration we want the GraphicsConfiguration
     *            from
     * @return the GraphicsConfiguration, or null if AWTGraphicsConfiguration is
     *         null
     */
    private static GraphicsConfiguration unwrap(AWTGraphicsConfiguration config)
    {
        if (config == null)
        {
            return null;
        }
        return config.getGraphicsConfiguration();
    }
}
package com.mojang.joxsi.demo;

import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

/**
 * An abstract baseclass for a singlethreaded opengl canvas.
 * 
 * <p>To use it, create a subclass, make it visible by sticking it in a window, then run the run() method from the
 * thread you wish to use for all opengl rendering.<br>
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
     * @param capabilities the GLCapabilities this canvas should have
     */
    public SingleThreadedGlCanvas(GLCapabilities capabilities)
    {
        this(capabilities, null, null, null);
    }

    /**
     * Creates a new SingleThreadedGlCanvas with the specified GLCapabilities, and some other options
     * 
     * @param capabilities the GLCapabilities this canvas should have
     * @param chooser a GLCapabilitiesChooser used for selecting a good GLCapabilities
     * @param shareWith the canvas should share this context
     * @param device the canvas should exist in this GraphicsDevice
     */
    public SingleThreadedGlCanvas(GLCapabilities capabilities, GLCapabilitiesChooser chooser, GLContext shareWith, GraphicsDevice device)
    {
        super(unwrap((AWTGraphicsConfiguration)GLDrawableFactory.getFactory().chooseGraphicsConfiguration(capabilities, chooser, new AWTGraphicsDevice(device))));
        drawable = GLDrawableFactory.getFactory().getGLDrawable(this, capabilities, chooser);
        context = drawable.createContext(shareWith);
    }

    /**
     * Is called by awt when the canvas is realized.
     * 
     * <p>Overridden to be able to detect this event. Passes the event on to the drawable
     */
    public void addNotify()
    {
        super.addNotify();
        drawable.setRealized(true);
        ok = true;
    }

    /**
     * Overridden to make sure it's empty.
     */
    public void update(Graphics g)
    {
    }

    /**
     * Overridden to make sure it's empty.
     */
    public void paint(Graphics g)
    {
    }

    /**
     * Runs the rendering loop.
     * 
     * <p>This is called when the context has been created and made current.<br>
     * This method shouldn't return until the application is closing.
     * 
     * @param gl a valid GL object
     * @param glu a valid GLU object
     */
    protected abstract void renderLoop(GL gl, GLU glu);

    /**
     * Swaps the opengl buffers.
     */
    protected void swapBuffers()
    {
        drawable.swapBuffers();
    }

    /**
     * Sets up the context and makes it current, then calls the renderloop method
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

        // Create GL and GLU objects
        GL gl = context.getGL();
        GLU glu = new GLU();

        // Call render loop
        renderLoop(gl, glu);
        
        // Release and destroy the context
        context.release();
        context.destroy();
        
        // Shut down the jvm.. This probably shouldn't be here.
        System.exit(0);
    }

    /**
     * Gets the GraphicsConfiguration of an AWTGraphicsConfiguration, unless the AWTGraphicsConfiguration is null.
     * 
     * @param config the AWTGraphicsConfiguration we want the GraphicsConfiguration from
     * @return the GraphicsConfiguration, or null if AWTGraphicsConfiguration is null
     */
    private static GraphicsConfiguration unwrap(AWTGraphicsConfiguration config)
    {
        if (config == null) { return null; }
        return config.getGraphicsConfiguration();
    }
}
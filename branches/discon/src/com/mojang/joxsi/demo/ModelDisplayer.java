package com.mojang.joxsi.demo;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.mojang.joxsi.Action;
import com.mojang.joxsi.GLSLshaders;
import com.mojang.joxsi.Scene;
import com.mojang.joxsi.loader.ParseException;
import com.mojang.joxsi.renderer.JoglSceneRenderer;
import com.mojang.joxsi.renderer.TextureLoader;

/**
 * A simple model displayer demo application.
 * 
 * @author Notch
 * @author Egal
 * @author Milbo
 */
public class ModelDisplayer extends SingleThreadedGlCanvas implements MouseListener, MouseMotionListener, MouseWheelListener,
        KeyListener
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(ModelDisplayer.class.getName());
    
    private static ConsoleHandler ch = new ConsoleHandler();
    
    private static FileHandler fh;
    
    /* * Classname used in some logging statements. */
    private static final String CLASS_NAME = ModelDisplayer.class.getName();

    private Scene scene;

    private int xDragStart;

    private int yDragStart;

    private float xRot;

    private float yRot;

    private float xCamera;

    private float yCamera;

    private float zCamera;

    private float zoomDistance = 4.1f;

    private static boolean stop = false;

    private JoglSceneRenderer sceneRenderer;

    private static long time = 0l;

    /** Diffuse scene light zero. */
    private float[] diffuseSceneLight0 = new float[] { 1, 1, 1, 1 };

    /** Should the Diffuse scene light zero be shown. */
    private boolean diffuseSceneLightFlag0 = true;

    /** Ambient scene lighting zero. */
    private float[] ambientSceneLight0 = new float[] { 0.25f, 0.25f, 0.25f, 1 };

    /** Should the Ambient scene light zero be shown. */
    private boolean ambientSceneLightFlag0 = true;

    /** Position of the Scene light zero */
    private float[] positionSceneLight0 = new float[] { 0, 0.7f, 0.7f, 0 };

    private boolean moreLight;

    private boolean grid;

    private boolean vertexshader;

    private int showModel;

    private int showAction;
    
    private boolean blend = true;
    /** If <code>true</code> render the {@link #groundTexture } or a plain colour. If <code>false</code> then draw a grid. */
    private boolean drawGround = true;
    /** If <code>true</code> render a plain background . If <code>false</code> then draw nothing. */
    private boolean drawBackground = true;
    /** Name of texture to render on the ground if {@link #drawGround } is <code>true</code>.  */
    private String groundTexture;
    /** Enable or disable AntiAliasing. */
    private boolean useAntiAliasing = false;
    /** Enable or disable AnisoropicFiltering. */
    private boolean useAnisotropicFiltering = false;
    /** How much Anisotropic Filtering to use. */
    private float anisotropicFilteringLevel = 0.0F;
    /** The textureLoader. */
    private TextureLoader textureLoader;
    
    private Action action;

    // Hmm just for testing
    private static final float TWO_PI = (float) (Math.PI * 2);

    private float wave_movement = 0.0f;
    /** Size of the grid on the ground. */
    private int SIZE = 32;

    private float[][][] mesh = new float[SIZE][SIZE][3];

    /**
     * TODO JavaDoc.
     * 
     * @param scene
     */
    public ModelDisplayer(Scene scene)
    {
        this.scene = scene;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        showModel = -1;
        showAction = -1;
        action = null;

        yCamera = -1.0f;
        createMesh();
    }

    /**
     * TODO JavaDoc.
     * 
     * @param modelNr
     */
    public void setShowModel(int modelNr)
    {
        if (modelNr >= scene.models.length)
        {
            showModel = -1;
            logger.warning("Illegal Model: " + modelNr + " (Model out of bound, max: " + scene.models.length + ")");
        }

        else
            showModel = modelNr;
        showAction = -1;
    }

    /**
     * TODO JavaDoc.
     * 
     * @param actionNr
     */
    public void setShowAction(int actionNr)
    {
        if (showModel >= 0)
        {
            if (actionNr >= scene.models[showModel].actions.length)
                showAction = -1;
            else
                showAction = actionNr;
        }
        updateAction();
    }

    /**
     * TODO JavaDoc.
     */
    public void updateAction()
    {
        /*
         * Pyro Small change, only do the search when there hasn't been set a
         * model and action which has to be showed
         */
        if (showModel == -1 || showAction == -1)
        {
            // Find all actions in all models in the root of the scene,
            // then store the first one in the 'action' object
            for (int i = 0; i < scene.models.length; i++)
            {
                logger.info("Number of actions in model " + i + ": " + scene.models[i].actions.length);
                for (int j = 0; j < scene.models[i].actions.length; j++)
                {
                    Action a = scene.models[i].actions[j];
                    if (action == null)
                    {
                        action = a;
                        showModel = i;
                        showAction = j;
                    }
                    logger.info("Length of action: " + a.getName() + ": " + a.getLength());
                }
            }
        }
        else
        {
            action = scene.models[showModel].actions[showAction];
        }
    }

    /**
     * TODO JavaDoc.
     */
    public void stopProgram() {
        stop = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        xDragStart = e.getX();
        yDragStart = e.getY();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Zoom in or out if the mousewheel is moved. Pressing the Ctrl key zooms at
     * 1/10th of normal.
     * 
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int modifiersEx = e.getModifiersEx();
        if ((modifiersEx & InputEvent.CTRL_DOWN_MASK) > 0)
        {
            zoomDistance += e.getUnitsToScroll() * 0.01f;
        }
        else
        {
            zoomDistance += e.getUnitsToScroll() * 0.1f;
        }
        if (zoomDistance < 0.01f) zoomDistance = 0.01f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e)
    {
        // Some magic for looking around

        int xDrag = e.getX() - xDragStart;
        int yDrag = e.getY() - yDragStart;
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) > 0)
        {
            xRot += xDrag;
            yRot += yDrag;
            if (yRot < -90) yRot = -90;
            if (yRot > 90) yRot = 90;
        }
        if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) > 0)
        {
            float xs = (float) Math.sin(xRot * Math.PI / 180.0f);
            float xc = (float) Math.cos(xRot * Math.PI / 180.0f);
            // TODO Unused float ys = (float)Math.sin(yRot * Math.PI / 180.0f);
            // TODO Unused float yc = (float)Math.cos(yRot * Math.PI / 180.0f);
            xCamera -= (xDrag * xc) * zoomDistance * zoomDistance / 100;
            zCamera -= (xDrag * xs) * zoomDistance * zoomDistance / 100;

            yCamera += (yDrag) * zoomDistance * zoomDistance / 100;
        }

        xDragStart = e.getX();
        yDragStart = e.getY();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent aEvent)
    {
        int keyCode = aEvent.getKeyCode();
        /*
        String keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";

        int modifiersEx = aEvent.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0)
        {
            modString += " (" + tmpString + ")";
        }
        else
        {
            modString += " (no extended modifiers)";
        }
        */
        // logger.info("keyPressed: " + keyString + ", " + modString);

        float xs = (float) Math.sin(xRot * Math.PI / 180.0f);
        float xc = (float) Math.cos(xRot * Math.PI / 180.0f);
        float zs = (float) Math.sin((90 - xRot) * Math.PI / 180.0f);
        float zc = (float) Math.cos((90 - xRot) * Math.PI / 180.0f);

        switch (keyCode)
            {
            case 'A':
                ambientSceneLightFlag0 = !ambientSceneLightFlag0;
                break;
            case 'D':
                diffuseSceneLightFlag0 = !diffuseSceneLightFlag0;
                break;
            case 'L':
                moreLight = !moreLight;
                break;
            case 'G':
                grid = !grid;
                break;
            case 'V':
                vertexshader = !vertexshader;
                break;
            case 'N':
                useAntiAliasing = !useAntiAliasing;
                break;
            case 'I':
                useAnisotropicFiltering = !useAnisotropicFiltering;
                if (logger.isLoggable(Level.FINER))
                {
                    logger.finer("Anisotropic Filtering level " + anisotropicFilteringLevel + ": " + useAnisotropicFiltering);
                }
                break;
            case 'B':
                blend = !blend;
                break;
            case 'F':
                drawGround = !drawGround;
                break;
            case 'W':
                drawBackground = !drawBackground;
                break;
                
            // Arrow keys movement
            case 37: // Left arrow
                xCamera += (2 * xc * zoomDistance) * zoomDistance * zoomDistance / 100;
                zCamera += (2 * xs * zoomDistance) * zoomDistance * zoomDistance / 100;
                break;
            case 38: // Up arrow
                xCamera -= (2 * zc * zoomDistance) * zoomDistance * zoomDistance / 100;
                zCamera += (2 * zs * zoomDistance) * zoomDistance * zoomDistance / 100;
                break;
            case 39: // Right arrow
                xCamera -= (2 * xc * zoomDistance) * zoomDistance * zoomDistance / 100;
                zCamera -= (2 * xs * zoomDistance) * zoomDistance * zoomDistance / 100;
                break;
            case 40: // Down arrow
                xCamera += (2 * zc * zoomDistance) * zoomDistance * zoomDistance / 100;
                zCamera -= (2 * zs * zoomDistance) * zoomDistance * zoomDistance / 100;
                break;
            // Numpad movement
            case 98: // Numpad 2
                yRot -= 2;
                break;
            case 100: // Numpad 4
                xRot += 2;
                break;
            case 102: // Numpad 6
                xRot -= 2;
                break;
            case 104: // Numpad 8
                yRot += 2;
                break;
            case 107: // Numpad +
                zoomDistance = zoomDistance * 0.9f;
                if (zoomDistance < 0.01f)
                {
                    zoomDistance = 0.01f;
                    logger.info("Resetting zoomDistance to 0.01");
                }
                break;
            case 109: // Numpad -
                zoomDistance = zoomDistance * 1.1f;
                break;

            case 27:    // escape
                stop = true;
                break;
                
            default:
                break;
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent aEvent)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent aEvent)
    {
    }

    /**
     * TODO JavaDoc.
     * Just for testing purposes.
     */
    public void createMesh()
    {
        // Create Our Mesh
        for (int x = 0; x < SIZE; x++)
        {
            for (int z = 0; z < SIZE; z++)
            {
                // We Want To Center Our Mesh Around The Origin
                mesh[x][z][0] = (float) (SIZE / 2) - x;
                // Set The Y Values For All Points To 0
                mesh[x][z][1] = 0.0f;
                // We Want To Center Our Mesh Around The Origin
                mesh[x][z][2] = (float) (SIZE / 2) - z;
            }
        }
    }

    /**
     * This is where all the rendering magic happens. Only handles two actions
     * at the moment.
     * TODO JavaDoc.
     * 
     * @param gl
     * @param glu
     * @param aShaders
     * 
     * @see SingleThreadedGlCanvas#renderLoop(GL, GLU, GLSLshaders)
     */
    protected void renderLoop(GL gl, GLU glu, GLSLshaders aShaders)
    {
        if (action == null) updateAction();

        int frames = 0;
        long start = System.currentTimeMillis();

        // Action action = null;
        // Log some details of the model
        int lNumberOfModels = 0;
        if (scene != null && scene.models != null)
        {
            lNumberOfModels = scene.models.length;
        }
        logger.info("Number of models: " + lNumberOfModels);
        int lNumberOfEnvelopes = 0;
        if (scene.envelopes != null)
        {
            lNumberOfEnvelopes = scene.envelopes.length;
        }
        logger.info("Number of envelopes in the scene: " + lNumberOfEnvelopes);
        logger.info("Number of images in the scene: " + scene.images.size());
        logger.info("Number of materials in the scene: " + scene.materials.size());

        logger.info("Model: " + showModel + "\tAction: " + showAction);

        // Create a new JoglSceneRenderer with a default TextureLoader
        sceneRenderer = new JoglSceneRenderer(gl, new TextureLoader(scene.basePath, gl, glu));

        // Create a textureLoader for the displayer
        textureLoader = new TextureLoader(null, gl, glu);

        // Check if Anisotropic filtering is supported by the GPU
        if( gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic") )   
        {
          float max[] = new float[1];
          gl.glGetFloatv( GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0 );
          logger.info("GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT: " + max[0]);
          anisotropicFilteringLevel = max[0];
        }
        else
        {
            // Anisotropic filtering is not supported by the GPU
            useAnisotropicFiltering = false;
            anisotropicFilteringLevel = 0.0F;
        }

        // Run main loop until the stop flag is raised.
        while (!stop)
        {
            // Set up viewport and frustum
            int width = getWidth();
            int height = getHeight();

            final float h = (float) width / (float) height;

            gl.glViewport(0, 0, width, height);
            gl.glDepthMask(true);
            gl.glEnable(GL.GL_DEPTH_TEST);

            // Clear Screen And Depth Buffer
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
            // Set Matrix to GL_PROJECTION
            gl.glMatrixMode(GL.GL_PROJECTION);
            // Reset The Projection Matrix
            gl.glLoadIdentity();
            glu.gluPerspective(70.0f, h, 0.1f, 1000.0f);
            // Set Matrix to GL_MODELVIEW
            gl.glMatrixMode(GL.GL_MODELVIEW);
            // Reset The Modelview Matrix
            gl.glLoadIdentity();

            // Translate to camera
            gl.glTranslatef(0, 0, -(zoomDistance * zoomDistance));
            gl.glRotatef(yRot, 1, 0, 0);
            gl.glRotatef(xRot, 0, 1, 0);
            gl.glTranslatef(xCamera, yCamera, zCamera);

            if (useAntiAliasing)
            {
                // Hint that we want the best smoothing
                gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
                gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
                gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

                // Enable Antialiasing
                gl.glEnable(GL.GL_POINT_SMOOTH);
                gl.glEnable(GL.GL_LINE_SMOOTH);
                gl.glEnable(GL.GL_POLYGON_SMOOTH);
            }
            else
            {
                // Hint that we want the fastest smoothing
                gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
                gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
                gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
                // Disable Antialiasing
                gl.glDisable(GL.GL_POINT_SMOOTH);
                gl.glDisable(GL.GL_LINE_SMOOTH);
                gl.glDisable(GL.GL_POLYGON_SMOOTH);
            }
            if (useAnisotropicFiltering && anisotropicFilteringLevel > 0)
            {
                gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicFilteringLevel);
            }
            else
            {
                gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 0.0F);
            }

            float hs = SIZE/2;
            if (drawBackground)
            {
                // Draw a background
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
                gl.glColor3f(0.4f, 0.50f, 0.8f);
                gl.glBegin(GL.GL_QUADS);
                    gl.glVertex3f(-hs, -hs, -hs);
                    gl.glVertex3f(hs, -hs, -hs);
                    gl.glVertex3f(hs, hs, -hs);
                    gl.glVertex3f(-hs, hs, -hs);
                gl.glEnd();
                gl.glBegin(GL.GL_QUADS);
                    gl.glVertex3f(-hs, -hs, hs);
                    gl.glVertex3f(-hs, -hs, -hs);
                    gl.glVertex3f(-hs, hs, -hs);
                    gl.glVertex3f(-hs, hs, hs);
                gl.glEnd();
            }
            
            // Draw the ground
            if (drawGround)
            {
                int texId = -1;
                if (groundTexture != null)
                   texId = textureLoader.loadTexture(groundTexture);

                if (texId != -1)
                {
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
                    gl.glColor3f(1.0f, 1.0f, 1.0f);
                }
                else
                    gl.glColor3f(0.3f, 0.7f, 0.4f);
                
                gl.glBegin(GL.GL_QUADS);
                    gl.glTexCoord2f(1.0f*SIZE, 0.0f);       gl.glVertex3f(-hs, 0f, hs);
                    gl.glTexCoord2f(1.0f*SIZE, 1.0f*SIZE);  gl.glVertex3f(-hs, 0f, -hs);
                    gl.glTexCoord2f(0.0f, 1.0f*SIZE);       gl.glVertex3f(hs, 0f, -hs);
                    gl.glTexCoord2f(0.0f, 0.0f);            gl.glVertex3f(hs, 0f, hs);
                gl.glEnd();
                if (texId != -1)   
                    gl.glDisable(GL.GL_TEXTURE_2D);
            }
            else
            {
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
                // lshaders.vertexShaderSupported=true;
                // Programming the GPU with the Vertexshader for the object drawn
                // later
                if (aShaders.vertexShaderSupported && vertexshader)
                {
                    gl.glUseProgramObjectARB(aShaders.programObject);
                }
    
                // Start Drawing Mesh, this is only for learning and testing shaders
                gl.glColor3f(0.5f, 0.5f, 0.5f);
                for (int x = 0; x < SIZE - 1; x++)
                {
                    // Draw A Triangle Strip For Each Column Of Our Mesh
                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                    for (int z = 0; z < SIZE - 1; z++)
                    {
                        // Test if Shader is supported and activated
                        if (aShaders.vertexShaderSupported && vertexshader)
                        {
                            // Set The Wave Parameter Of Our Shader To The
                            // Incremented
                            // Wave Value From Our Main Program
                            gl.glVertexAttrib1f(aShaders.waveAttrib, wave_movement);
                            gl.glColor3f(0.5f, 0.0f, 1.0f);
                        }
                        // Draw Vertex
                        gl.glVertex3f(mesh[x][z][0], mesh[x][z][1], mesh[x][z][2]);
                        // Draw Vertex
                        gl.glVertex3f(mesh[x + 1][z][0], mesh[x + 1][z][1], mesh[x + 1][z][2]);
                        wave_movement += 0.00001f; // Increment Our Wave Movement
                        if (wave_movement > TWO_PI)
                        { // Prevent Crashing
                            wave_movement = 0.0f;
                        }
                    }
                    gl.glEnd();
                }
                // Setting the GPU shader 0 to object drawn before
                if (aShaders.vertexShaderSupported && vertexshader)
                {
                    gl.glUseProgramObjectARB(0);
                }
            }
            

            // // Draw a grid on the grid for the base plane
            // gl.glBegin(GL.GL_LINES);
            // {
            // float z = 10;
            // for (int x = -32; x <= 32; x++)
            // {
            //
            // if ((x & 3) == 0)
            // {
            // gl.glColor3f(0.5f, 0.5f, 0.5f);
            // }
            // else
            // {
            // gl.glColor3f(0.25f, 0.25f, 0.25f);
            // }
            //
            // gl.glVertex3f(x * z, 0, -32 * z);
            // gl.glVertex3f(x * z, 0, 32 * z);
            // gl.glVertex3f(-32 * z, 0, x * z);
            // gl.glVertex3f(32 * z, 0, x * z);
            //  
            // }
            // }
            // gl.glEnd();

            // Set up the lights
            if (diffuseSceneLightFlag0)
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuseSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[]
                { 0.0f, 0.0f, 0.0f }, 0);
            }
            if (ambientSceneLightFlag0)
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambientSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]
                { 0.0f, 0.0f, 0.0f }, 0);
            }
            if (moreLight)
            {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuseSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, new float[]
                { 0.1f, 0.1f, 0.1f }, 0);

            }
            if (grid)
            {
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            }
            else
            {
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            }
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, positionSceneLight0, 0);
            gl.glEnable(GL.GL_LIGHT0);
            gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, positionSceneLight0, 0);
            gl.glEnable(GL.GL_LIGHT1);
            gl.glEnable(GL.GL_LIGHTING);
            
            // If an animation was found in the setup, apply it now.
            if (action != null)
            {
                action.apply((System.currentTimeMillis() - start) / 4000.0f * action.getLength());
            }

            // Discon - Enable blending
            if (blend)
            {
                gl.glEnable(GL.GL_BLEND);
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            }
            
            // Render the scene
            sceneRenderer.render(scene);

            if (blend)
            {
                gl.glDisable(GL.GL_BLEND);
            }
            
            // Disable lighting
            gl.glDisable(GL.GL_LIGHTING);

            // Calculate the fps.
            frames++;
            long now = System.currentTimeMillis();
            if (now - start > 4000)
            {
                // This is more precise
                start = now;
                // start += 4000;
                if (logger.isLoggable(Level.FINEST)) logger.finest(frames / 4 + " fps");
                // TODO
                logger.info(frames / 4 + " fps");
                frames = 0;
            }
            // This makes the mouse and key listeners more responsive
            releaseContextAndMakeItcurrentAgain();

            // Swap buffers
            swapBuffers();
        }
    }

    /**
     * Main entry point of the application.
     * 
     * @param args
     *            the arguments passed from the commandline
     * @throws IOException
     *             if the model can't be loaded.
     * @throws ParseException
     *             if the parsing fails for any reason
     */
    public static void main(String[] args) throws IOException, ParseException
    {
        final String methodName = "main";
        fh = new FileHandler("modeldisplayer.log");
        logger.addHandler(ch);
        logger.addHandler(fh);
        TimeIt timer = new TimeIt();
        String groundTexture = null;
        
        
        Scene scene = null;
        if (args.length == 0)
        {
            logger.info("No arguments. We're probably run from webstart, so load the default model");
            // No arguments. We're probably run from webstart, so load the
            // default model
            scene = Scene.load(ModelDisplayer.class.getResourceAsStream("/DanceMagic.xsi"));
        }
        else
        {
            // Just the possibility to load more Models at start for better time
            // measurement
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].startsWith("-"))    // Options
                {
                    // -ground texture/terrain/enchanted-grass.jpg
                    String option = args[i].substring(1).toLowerCase();
                    
                    if (option.equals("ground"))
                    {
                        if (i+1 < args.length)
                        {
                            groundTexture = args[++i];
                            continue;
                        }
                    }
                }
                // Models
                else
                {
                    logger.info("Going to load '" + args[i] + "' as a model");
                    final InputStream lResourceAsStream = ModelDisplayer.class.getResourceAsStream("/" + args[i]);
                    if (lResourceAsStream != null)
                    {
                        logger.info("Going to load '" + args[i] + "' as a model from "
                                + ModelDisplayer.class.getResource("/" + args[i]));
    
                        String basePath = null;
                        int lastSlashIndex = args[i].lastIndexOf('/');
                        if (lastSlashIndex != -1)
                            basePath = args[i].substring(0, lastSlashIndex+1);
                        
                        scene = Scene.load(lResourceAsStream, basePath);
                    }
                    else
                    {
                        logger.throwing(CLASS_NAME, methodName,
                                new IllegalArgumentException("Cannot load model from this location: " + args[i]));
                    }
                }
            }
        }

        time = timer.getTime();
        logger.info("Time to create scene: " + time + "ms");
        // Set up a JFrame for a TemplateTree showing the entire scene
        JFrame frame1 = new JFrame("Templates");
        frame1.setSize(200, 500);
        frame1.add(new JScrollPane(new TemplateTree(scene.root)));
        frame1.setVisible(true);

        // Set up a JFrame for a ModelDisplayer, and start the modeldisplayer
        JFrame frame = new ModelDisplayerFrame("Model Display", scene.models);
        ModelDisplayer canvas = new ModelDisplayer(scene);
        canvas.groundTexture = groundTexture;
        frame.getContentPane().add(canvas);
        new Thread(canvas).start();

        frame.setLocation(200, 30);
        frame.setSize(512, 384);
        frame.setVisible(true);

        // Add a hook for listening to the windowclose event
        frame.addWindowListener(new WindowAdapter()
        {
            /*
             * (non-Javadoc)
             * @see java.awt.event.WindowAdapter#windowClosing(WindowEvent)
             */
            public void windowClosing(WindowEvent e)
            {
                // Set the stop flag to true.
                stop = true;
            }
        });
    }
}
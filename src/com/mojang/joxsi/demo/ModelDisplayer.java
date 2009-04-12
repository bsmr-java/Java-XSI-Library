package com.mojang.joxsi.demo;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.mojang.joxsi.Action;
import com.mojang.joxsi.Scene;
import com.mojang.joxsi.loader.ParseException;
import com.mojang.joxsi.renderer.JoglSceneRenderer;
import com.mojang.joxsi.renderer.TextureLoader;
import com.mojang.joxsi.renderer.shaders.*;

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
    // Setup a static logger instance that is available from anywhere to use
    public final static String LOG_PROPERTIES_FILE = "logging.properties";
    public final static Logger logger;

    static
    {
        // Configure java.util.logging and get a Logger instance
        System.setProperty("java.util.logging.config.file", LOG_PROPERTIES_FILE);
        LogManager logManager = LogManager.getLogManager();
        try
        {
            logManager.readConfiguration();
        }
        catch (SecurityException e)
        {
        }
        catch (IOException e)
        {
        }
        logger = Logger.getLogger(ModelDisplayer.class.getName());

    }

    // List of Scenes (models) and current displayed Scene pointers
    private List<Scene> scenes = new ArrayList<Scene>();
    private Scene scene;
    private Scene tool;
    private Scene tool2;

    // JFrame's for the TemplateTree and ModelDisplayerFrame
    private static JFrame templateTreeFrame;
    private static ModelDisplayerFrame modelDisplayerFrame;
    private TemplateTree templateTree;

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
    private boolean vertexshader = false;
    private int showModel;
    private int showAction;
    private boolean blend = true;

    /**
     * If <code>true</code> render the {@link #groundTexture } or a plain colour.
     * If <code>false</code> then draw a grid.
     */
    private boolean drawGround = true;
    /**
     * If <code>true</code> render a plain background . If <code>false</code>
     * then draw nothing.
     */
    private boolean drawBackground = true;
    /**
     * Name of texture to render on the ground if {@link #drawGround } is
     * <code>true</code>.
     */
    private String groundTexture;
    /** Enable or disable AntiAliasing. */
    private boolean useAntiAliasing = false;
    /** Enable or disable AnisoropicFiltering. */
    private boolean useAnisotropicFiltering = false;
    /** How much Anisotropic Filtering to use. */
    private int anisotropicFilteringLevel = 0;
    /**
     * Determines the value for calling glLightModel with parameter
     * GL_LIGHT_MODEL_COLOR_CONTROL. <code>false</code> (GL.GL_SINGLE_COLOR)
     * specifies that a single color is generated from the lighting computation
     * for a vertex. <code>true</code> (G.#GL_SEPARATE_SPECULAR_COLOR) specifies
     * that the specular color computation of lighting be stored separately from
     * the remainder of the lighting computation. The specular color is summed
     * into the generated fragment's color after the application of texture
     * mapping (if enabled). The initial value is false (GL.GL_SINGLE_COLOR).
     */
    private boolean useSeparateSpecularColour = false;
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
    public ModelDisplayer()
    {
        templateTreeFrame = new JFrame("Templates");
        templateTreeFrame.setSize(200, 500);
        templateTree = new TemplateTree();
        templateTreeFrame.add(new JScrollPane(templateTree));
        templateTreeFrame.setVisible(true);

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
     * Sets the currently displaying Scene.
     * 
     * @param scene
     *            index of the Scene to switch to
     */
    public void setShowScene(int sceneIndex)
    {
        // Give up if index out of bounds
        if (sceneIndex < 0 || sceneIndex >= this.scenes.size()) return;

        // Set the current Scene instance to the indexed scene
        this.scene = this.scenes.get(sceneIndex);

        // Make sure the TextureLoader has the right basePath
        this.sceneRenderer.getTextureLoader().setBasePath(scene.basePath);

        // Set the Tree view to display selected model information
        templateTree.setTemplate(scene.root);
    }

    /**
     * Sets the currently displaying Model.
     * 
     * @param modelIndex
     */
    public void setShowModel(int modelIndex)
    {
        if (modelIndex >= scene.models.length)
        {
            showModel = -1;
            logger.warning("Illegal Model: " + modelIndex + " (Model out of bound, max: " + scene.models.length + ")");
        }

        else
            showModel = modelIndex;
        showAction = -1;
    }

    /**
     * TODO JavaDoc.
     * 
     * @param actionIndex
     */
    public void setShowAction(int actionIndex)
    {
        if (showModel >= 0)
        {
            if (actionIndex >= scene.models[showModel].actions.length)
                showAction = -1;
            else
                showAction = actionIndex;
        }
        updateAction();
    }

    /**
     * TODO JavaDoc.
     */
    public void updateAction()
    {
        // Do nothing if no scene is available
        if (scene == null) return;

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
    public void stopProgram()
    {
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
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
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
     * @see
     * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
     * )
     */
    public void mouseDragged(MouseEvent e)
    {
        // Some magic for looking around

        int xDrag = e.getX() - xDragStart;
        int yDrag = e.getY() - yDragStart;
        if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) > 0)
        {
            xRot += xDrag;
            yRot += yDrag;
            if (yRot < -90) yRot = -90;
            if (yRot > 90) yRot = 90;
        }
        if ((e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) > 0)
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
     * @see
     * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
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
         * String keyString = "key code = " + keyCode + " (" +
         * KeyEvent.getKeyText(keyCode) + ")";
         * 
         * int modifiersEx = aEvent.getModifiersEx(); String modString =
         * "extended modifiers = " + modifiersEx; String tmpString =
         * KeyEvent.getModifiersExText(modifiersEx); if (tmpString.length() > 0)
         * { modString += " (" + tmpString + ")"; } else { modString += " (no
         * extended modifiers)"; }
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
            case 'S':
                useSeparateSpecularColour = !useSeparateSpecularColour;
                if (logger.isLoggable(Level.FINER))
                {
                    logger.finer("Use separate specular colour: " + useSeparateSpecularColour);
                }
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

            case 27: // escape
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
     * TODO JavaDoc. Just for testing purposes.
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
     * at the moment. TODO JavaDoc.
     * 
     * @param gl
     * @param glu
     * @param aShaders
     * 
     * @see SingleThreadedGlCanvas#renderLoop(GL, GLU, GLSLshaders)
     */
    @Override
    protected void renderLoop(GL gl, GLU glu, Program shaderProgram)
    {
        if (action == null) updateAction();

        int frames = 0;
        long start = System.currentTimeMillis();

        // Action action = null;
        // Log some details of the model
        /*
         * //TRIBUADORE int lNumberOfModels = 0; if (scene != null &&
         * scene.models != null) { lNumberOfModels = scene.models.length; }
         * logger.info("Number of models: " + lNumberOfModels); int
         * lNumberOfEnvelopes = 0; if (scene.envelopes != null) {
         * lNumberOfEnvelopes = scene.envelopes.length; }
         * logger.info("Number of envelopes in the scene: " +
         * lNumberOfEnvelopes); logger.info("Number of images in the scene: " +
         * scene.images.size());
         * logger.info("Number of materials in the scene: " +
         * scene.materials.size());
         * 
         * logger.info("Model: " + showModel + "\tAction: " + showAction);
         */

        // Create a textureLoader for the displayer
        textureLoader = new TextureLoader(null, gl, glu);

        // Create a new JoglSceneRenderer with a default TextureLoader
        sceneRenderer = new JoglSceneRenderer(gl, textureLoader);
        if (tool != null) sceneRenderer.addNullAttachment("R_hand_null", tool); // MDL-Model
        if (tool2 != null) sceneRenderer.addNullAttachment("L_hand_null", tool2); // MDL-Model

        int maxTexture[] = new int[1];
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_COORDS, maxTexture, 0);
        logger.info("GL_MAX_TEXTURE_COORDS: " + maxTexture[0]);
        gl.glGetIntegerv(GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, maxTexture, 0);
        logger.info("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS: " + maxTexture[0]);

        // Log the Minimum and Maximum OpenGL Point Size
        float val[] = new float[2];
        gl.glGetFloatv(GL.GL_POINT_SIZE_RANGE, val, 0);
        logger.info("min point size=" + val[0] + " max=" + val[1]);

        // Check if Anisotropic filtering is supported by the GPU
        if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic"))
        {
            int max[] = new int[1];
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
            logger.info("GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT: " + max[0]);
            anisotropicFilteringLevel = max[0];
        }
        else
        {
            // Anisotropic filtering is not supported by the GPU
            useAnisotropicFiltering = false;
            anisotropicFilteringLevel = 0;
        }

        // SHADER CODE
        int shaderWaveAttrib = 0;
        int waveProgram = 0;

        try
        {
            ShaderSourceCode vertexShaderSource = ShaderSourceCode.fromResource("/wave.glsl");
            shaderProgram.addVertexShader(gl, vertexShaderSource);
            waveProgram = shaderProgram.link(gl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        logger.info("Compiled and Linked shader");
        shaderWaveAttrib = shaderProgram.getAttribLocation(gl, waveProgram, "wave");
        // END SHADER CODE

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

            float hs = SIZE / 2;
            if (drawBackground)
            {
                // Draw a blue background
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

                // Draw a grid on the background
                gl.glBegin(GL.GL_LINES);
                {
                    float z = 4;
                    int size = 4;
                    for (int x = -size; x <= size; x++)
                    {
                        if ((x & 3) == 0)
                        {
                            gl.glColor3f(0.9f, 0.3f, 0.3f);
                        }
                        else
                        {
                            gl.glColor3f(0.55f, 0.25f, 0.25f);
                        }
                        gl.glVertex3f(-hs, x * z, -size * z);
                        gl.glVertex3f(-hs, x * z, size * z);
                        gl.glVertex3f(-hs, -size * z, x * z);
                        gl.glVertex3f(-hs, size * z, x * z);
                    }
                }
                gl.glEnd();
                // Draw a grid on the background
                gl.glBegin(GL.GL_LINES);
                {
                    float z = 4;
                    int size = 4;
                    for (int x = -size; x <= size; x++)
                    {
                        if ((x & 3) == 0)
                        {
                            gl.glColor3f(0.9f, 1.0f, 0.2f);
                        }
                        else
                        {
                            gl.glColor3f(0.55f, 0.25f, 0.25f);
                        }
                        gl.glVertex3f(-size * z, x * z, -hs);
                        gl.glVertex3f(size * z, x * z, -hs);
                        gl.glVertex3f(x * z, -size * z, -hs);
                        gl.glVertex3f(x * z, size * z, -hs);
                    }
                }
                gl.glEnd();
            }

            // Both walls in the Background should stay filled. Grid is
            // interesting for model and ground
            setGridmode(gl);

            if (drawGround)
            {
                int texId = -1;
                if (groundTexture != null) texId = textureLoader.loadTexture(groundTexture);

                if (texId != -1)
                {
                    gl.glEnable(GL.GL_TEXTURE_2D);
                    gl.glActiveTexture(GL.GL_TEXTURE0);
                    gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
                    gl.glColor3f(1.0f, 1.0f, 1.0f);
                }
                else
                {
                    // Ground is green if there is no texture
                    gl.glColor3f(0.2f, 0.5f, 0.2f);
                    // Only if there is no texture it should be disabled
                    gl.glDisable(GL.GL_TEXTURE_2D);
                }

                // Programming the GPU with the Vertexshader for the object
                // drawn later
                if (vertexshader)
                {
                    // shaderProgram.Enable;
                    shaderProgram.Enable(gl, waveProgram);
                }

                for (int x = 0; x < SIZE - 1; x++)
                {
                    // Draw A Triangle Strip For Each Column Of Our Mesh
                    gl.glBegin(GL.GL_TRIANGLE_STRIP);
                    for (int z = 0; z < SIZE - 1; z++)
                    {
                        // Test if Shader is supported and activated
                        if (vertexshader)
                        {
                            // Set The Wave Parameter Of Our Shader To The
                            // Incremented Wave Value From Our Main Program
                            gl.glVertexAttrib1f(shaderWaveAttrib, wave_movement);

                            // Increment Our Wave Movement
                            wave_movement += 0.00001f;
                            if (wave_movement > TWO_PI)
                            {
                                // Prevent Crashing
                                wave_movement = 0.0f;
                            }
                        }

                        // Draw Vertex
                        if (z % 2 != 1)
                        {
                            gl.glTexCoord2f(1.0f, 1.0f);
                        }
                        else
                        {
                            gl.glTexCoord2f(1.0f, 0.0f);
                        }

                        gl.glVertex3f(mesh[x][z][0], mesh[x][z][1], mesh[x][z][2]);

                        // Draw Vertex
                        if (z % 2 != 1)
                        {
                            gl.glTexCoord2f(0.0f, 1.0f);
                        }
                        else
                        {
                            gl.glTexCoord2f(0.0f, 0.0f);
                        }
                        gl.glVertex3f(mesh[x + 1][z][0], mesh[x + 1][z][1], mesh[x + 1][z][2]);
                    }
                    gl.glEnd();
                }

                // Setting the GPU shader 0 it is like setting on null
                if (vertexshader)
                {
                    shaderProgram.Disable(gl);
                }
            }
            else
            {
                // Draw a grid
                gl.glBegin(GL.GL_LINES);
                {
                    float z = 4;
                    for (int x = -SIZE; x <= SIZE; x++)
                    {
                        if ((x & 3) == 0)
                        {
                            gl.glColor3f(0.2f, 1.0f, 0.2f);
                        }
                        else
                        {
                            gl.glColor3f(0.25f, 0.25f, 0.25f);
                        }
                        gl.glVertex3f(x * z, 0, -SIZE * z);
                        gl.glVertex3f(x * z, 0, SIZE * z);
                        gl.glVertex3f(-SIZE * z, 0, x * z);
                        gl.glVertex3f(SIZE * z, 0, x * z);
                    }
                }
                gl.glEnd();
            }

            if (useSeparateSpecularColour)
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL, GL.GL_SEPARATE_SPECULAR_COLOR);
            }
            else
            {
                gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL, GL.GL_SINGLE_COLOR);
            }

            // Set up the lights
            if (diffuseSceneLightFlag0)
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuseSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] { 0.0f, 0.0f, 0.0f }, 0);
            }
            if (ambientSceneLightFlag0)
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambientSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f }, 0);
            }
            if (moreLight)
            {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuseSceneLight0, 0);
            }
            else
            {
                gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, new float[] { 0.1f, 0.1f, 0.1f }, 0);

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

            // Render the scene if available
            if (scene != null) sceneRenderer.render(scene);

            // Disable lighting
            gl.glDisable(GL.GL_LIGHTING);

            // Calculate the FPS
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

    /*
     * Just for setting the Gridmode need to be called more than one time
     */
    private void setGridmode(GL gl)
    {
        if (grid)
        {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        }
        else
        {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        }
    }

    /**
     * Adds a Scene to the list of available Scenes.
     * 
     * @param scene
     *            The Scene
     */
    public void addScene(String filename, Scene scene)
    {
        scenes.add(scene);
        modelDisplayerFrame.addModels(filename, scene);
        setShowScene(this.scenes.size() - 1);
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
        String groundTexture = null;
        Scene tool = null;
        Scene tool2 = null;

        // Set up a JFrame for a ModelDisplayer, and start the modeldisplayer
        modelDisplayerFrame = new ModelDisplayerFrame("Model Display");
        ModelDisplayer canvas = new ModelDisplayer();
        canvas.tool = tool;
        canvas.tool2 = tool2;
        canvas.groundTexture = groundTexture;
        modelDisplayerFrame.getContentPane().add(canvas);
        new Thread(canvas).start();

        modelDisplayerFrame.setLocation(200, 30);
        modelDisplayerFrame.setSize(512, 384);
        modelDisplayerFrame.setVisible(true);

        // Possibly load Models at start for better time measurement
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].startsWith("-")) // Options
            {
                // -ground texture/terrain/enchanted-grass.jpg
                String option = args[i].substring(1).toLowerCase();

                if (option.equals("ground"))
                {
                    if (i + 1 < args.length)
                    {
                        groundTexture = args[++i];
                        continue;
                    }
                }
                if (option.startsWith("tool"))
                {
                    if (i + 1 < args.length)
                    {
                        logger.info("Going to load '" + args[++i] + "' as a tool model");
                        final InputStream lResourceAsStream = ModelDisplayer.class.getResourceAsStream("/" + args[i]);
                        if (lResourceAsStream != null)
                        {
                            logger.info("Going to load '" + args[i] + "' as a tool model from "
                                    + ModelDisplayer.class.getResource("/" + args[i]));

                            String basePath = null;
                            int lastSlashIndex = args[i].lastIndexOf('/');
                            if (lastSlashIndex != -1) basePath = args[i].substring(0, lastSlashIndex + 1);

                            if (option.equals("tool"))
                                tool = Scene.load(lResourceAsStream, basePath);
                            else if (option.equals("tool2")) tool2 = Scene.load(lResourceAsStream, basePath);

                        }
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
                    if (lastSlashIndex != -1) basePath = args[i].substring(0, lastSlashIndex + 1);

                    canvas.addScene("/", Scene.load(lResourceAsStream, basePath));
                }
                else
                {
                    logger.info("Model does not exist at location " + args[i]);
                }
            }
        }

        // Add a hook for listening to the windowclose event
        modelDisplayerFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                // Set the stop flag to true.
                stop = true;
            }
        });
    }
}
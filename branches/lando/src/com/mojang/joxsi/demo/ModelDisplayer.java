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
 * @author Notch
 * @author Egal
 * @author Milbo
 */
public class ModelDisplayer extends SingleThreadedGlCanvas implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    
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
    private float[] diffuseSceneLight0 = new float[] {1, 1, 1, 1};
    /** Should the Diffuse scene light zero be shown. */
    private boolean diffuseSceneLightFlag0 = true;
    /** Ambient scene lighting zero. */
    private float[] ambientSceneLight0 = new float[] {0.25f, 0.25f, 0.25f, 1};
    /** Should the Ambient scene light zero be shown. */
    private boolean ambientSceneLightFlag0 = true;
    /** Position of the Scene light zero */
    private float[] positionSceneLight0 = new float[] {0, 0.7f, 0.7f, 0};

 	private boolean moreLight;
	private boolean grid;
	private boolean vertexshader;
	
    //Hmm just for testing
    private static final float TWO_PI = (float) (Math.PI *2);
    private float wave_movement = 0.0f;
    private int SIZE=32;
    private float[][][] mesh = new float[SIZE][SIZE][3];


    public ModelDisplayer(Scene scene)
    {
        this.scene = scene;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        yCamera = -1.0f;
        createMesh();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
        xDragStart = e.getX();
        yDragStart = e.getY();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Zoom in or out if the mousewheel is moved.
     * Pressing the Ctrl key zooms at 1/10th of normal.
     *
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int modifiersEx = e.getModifiersEx();
        if ((modifiersEx & InputEvent.CTRL_DOWN_MASK) > 0)
        {
            zoomDistance += e.getUnitsToScroll() * 0.01f;
        } else
        {
            zoomDistance += e.getUnitsToScroll() * 0.1f;
        }
        if (zoomDistance < 0.1f) zoomDistance = 0.1f;
    }

    /*
     * (non-Javadoc)
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
            float xs = (float)Math.sin(xRot * Math.PI / 180.0f);
            float xc = (float)Math.cos(xRot * Math.PI / 180.0f);
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
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e)
    {
    }

    /* 
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent aEvent) {
        int keyCode = aEvent.getKeyCode();
        String keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";

        int modifiersEx = aEvent.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
        //System.out.println("keyPressed: " + keyString + ", " + modString);

        float xs = (float)Math.sin(xRot * Math.PI / 180.0f);
        float xc = (float)Math.cos(xRot * Math.PI / 180.0f);
        float zs = (float)Math.sin((90-xRot) * Math.PI / 180.0f);
        float zc = (float)Math.cos((90-xRot) * Math.PI / 180.0f);

        switch (keyCode)
        {
            case 65: // A
                ambientSceneLightFlag0 = !ambientSceneLightFlag0;
                break;
            case 68: // D
                diffuseSceneLightFlag0 = !diffuseSceneLightFlag0;
                break;
            case 76: //l
            	moreLight = !moreLight;
            	break;
            case 71: //g
            	grid=!grid;
            	break;
            case 86: //v
            	vertexshader=!vertexshader;
            	break;
            
            //Arrow keys movement
            case 37: //Left arrow
            	xCamera += (2 * xc * zoomDistance) * zoomDistance * zoomDistance / 100;
            	zCamera += (2 * xs * zoomDistance) * zoomDistance * zoomDistance / 100;
            	break;
            case 38: //Up arrow
            	xCamera -= (2 * zc * zoomDistance) * zoomDistance * zoomDistance / 100;
            	zCamera += (2 * zs * zoomDistance) * zoomDistance * zoomDistance / 100;
            	break;
            case 39: //Right arrow
            	xCamera -= (2 * xc * zoomDistance) * zoomDistance * zoomDistance / 100;
            	zCamera -= (2 * xs * zoomDistance) * zoomDistance * zoomDistance / 100;
            	break;
            case 40: //Down arrow
            	xCamera += (2 * zc * zoomDistance) * zoomDistance * zoomDistance / 100;
            	zCamera -= (2 * zs * zoomDistance) * zoomDistance * zoomDistance / 100;
            	break;
            //Numpad movement
            case 98: //Numpad 2
            	yRot -= 2;
            	break;
            case 100: //Numpad 4
            	xRot += 2;
            	break;
            case 102: //Numpad 6
            	xRot -= 2;
            	break;
            case 104: //Numpad 8
            	yRot += 2;
            	break;
            case 107: //Numpad +
            	zoomDistance = zoomDistance * 0.9f;
            	if(zoomDistance < 0.1f) {
            		zoomDistance = 0.1f;
            		System.out.println("Resetting zoomDistance to 0.1");
            	}
            	break;
            case 109: //Numpad -
            	zoomDistance = zoomDistance * 1.1f;
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
    public void keyReleased(KeyEvent aEvent) {
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent aEvent) {
    }
    
    //Just for testing purposes
    public void createMesh(){
       // Create Our Mesh
       for (int x = 0; x < SIZE; x++) {
           for (int z = 0; z < SIZE; z++) {
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
     * This is where all the rendering magic happens.
     * Only handles two actions at the moment.
     */
    protected void renderLoop(GL gl, GLU glu,GLSLshaders lshaders)
    {
 
        int frames = 0;
        long start = System.currentTimeMillis();

        Action action = null;
        // Log some details of the model
        int lNumberOfModels = 0;
        if (scene != null && scene.models != null)
        {
            lNumberOfModels = scene.models.length;
        }
        System.out.println("Number of models: " + lNumberOfModels);
        int lNumberOfEnvelopes = 0;
        if (scene.envelopes != null)
        {
            lNumberOfEnvelopes = scene.envelopes.length;
        }
        System.out.println("Number of envelopes in the scene: " + lNumberOfEnvelopes);
        System.out.println("Number of images in the scene: " + scene.images.size());
        System.out.println("Number of maerials in the scene: " + scene.materials.size());
        // Find all actions in all models in the root of the scene, then store the first one in the 'action' object
        for (int i = 0; i < scene.models.length; i++)
        {
            System.out.println("Number of actions in model " + i + ": " + scene.models[i].actions.length);
            for (int j = 0; j < scene.models[i].actions.length; j++)
            {
                Action a = scene.models[i].actions[j];
                if (action == null)
                {
                	action = a;
                }
                System.out.println("Length of action: " + a.getName() + ": " + a.getLength());
            }
        }

        // Create a new JoglSceneRenderer with a default TextureLoader
        sceneRenderer = new JoglSceneRenderer(gl, new TextureLoader(gl, glu));
 
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

            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            // lshaders.vertexShaderSupported=true;
            // Programming the GPU with the Vertexshader for the object drawn later
            if (lshaders.vertexShaderSupported && vertexshader)
            {
                gl.glUseProgramObjectARB(lshaders.programObject);
            }

            // Start Drawing Mesh, this is only for learning and testing shaders
            gl.glColor3f(0.5f, 0.5f, 0.5f);
            for (int x = 0; x < SIZE - 1; x++)
            {
                // Draw A Triangle Strip For Each Column Of Our Mesh
                gl.glBegin(GL.GL_TRIANGLE_STRIP);
                for (int z = 0; z < SIZE - 1; z++)
                {
                    //Test if Shader is supported and activated
                    if (lshaders.vertexShaderSupported && vertexshader)
                    {
                  	// Set The Wave Parameter Of Our Shader To The Incremented
                       // Wave Value From Our Main Program
                        gl.glVertexAttrib1f(lshaders.waveAttrib, wave_movement);
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
            if (lshaders.vertexShaderSupported && vertexshader)
            {
                gl.glUseProgramObjectARB(0);
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

            // Render the scene
            sceneRenderer.render(scene);

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
                System.out.println(frames / 4 + " fps and Time of creating scene: " + time);
                frames = 0;
            }
            // This makes the mouse and key listeners more responsive
            Thread.yield();

            // Swap buffers
            swapBuffers();
        }
    }

    /**
     * Main entry point of the application.
     *  
     * @param args the arguments passed from the commandline
     * @throws IOException if the model can't be loaded.
     * @throws ParseException if the parsing fails for any reason
     */
    public static void main(String[] args) throws IOException, ParseException
    {
   	 	TimeIt timer = new TimeIt();

        Scene scene = null;
        if (args.length == 0)
        {
            System.out.println("No arguments. We're probably run from webstart, so load the default model");
            // No arguments. We're probably run from webstart, so load the default model
            scene = Scene.load(ModelDisplayer.class.getResourceAsStream("/DanceMagic.xsi"));
        }
        else
        {
      	  //Just the possibility to load more Models at start for better time measurement
            for (int i = 0; i < args.length; i++)
            {
                System.out.println("Going to load '" + args[i] + "' as a model");
                final InputStream lResourceAsStream = ModelDisplayer.class.getResourceAsStream("/" + args[i]);
                if (lResourceAsStream != null)
                {
                    System.out.println("Going to load '" + args[i] + "' as a model from " + ModelDisplayer.class.getResource("/" + args[i]));
                    scene = Scene.load(lResourceAsStream);
                }
                else
                {
                    throw new IllegalArgumentException("Cannot load model from this location: " + args[i]);
                }
            }
        }

        time = timer.getTime();
        // System.err.println("Time of creating scene: "+time);
        // Set up a JFrame for a TemplateTree showing the entire scene
        JFrame frame1 = new JFrame("Templates");
        frame1.setSize(200, 500);
        frame1.add(new JScrollPane(new TemplateTree(scene.root)));
        frame1.setVisible(true);

        // Set up a JFrame for a ModelDisplayer, and start the modeldisplayer
        JFrame frame = new ModelDisplayerFrame("Model Display");
        ModelDisplayer canvas = new ModelDisplayer(scene);
        frame.add(canvas);
        new Thread(canvas).start();

        frame.setLocation(200, 30);
        frame.setSize(512, 384);
        frame.setVisible(true);
        
        // Add a hook for listening to the windowclose event
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e)
            {
                // Set the stop flag to true.
                stop = true;
            }
        });
    }
}
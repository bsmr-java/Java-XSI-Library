package com.mojang.joxsi.demo;

import java.awt.event.InputEvent;
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
import com.mojang.joxsi.Scene;
import com.mojang.joxsi.loader.ParseException;
import com.mojang.joxsi.renderer.JoglSceneRenderer;
import com.mojang.joxsi.renderer.TextureLoader;

/**
 * A simple model displayer demo application.
 */
public class ModelDisplayer extends SingleThreadedGlCanvas implements MouseListener, MouseMotionListener, MouseWheelListener
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

    public ModelDisplayer(Scene scene)
    {
        this.scene = scene;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        yCamera = -1.0f;
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        xDragStart = e.getX();
        yDragStart = e.getY();
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Zoom in or out if the mousewheel is moved.
     * Pressing the Ctrl key zooms at 1/10th of normal.
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

    public void mouseMoved(MouseEvent e)
    {
    }

    /**
     * This is where all the rendering magic happens.
     * Only handles two actions at the moment.
     */
    protected void renderLoop(GL gl, GLU glu)
    {
        int frames = 0;
        long start = System.currentTimeMillis();

        Action action = null;
        // Log some details of the model
        int lNumberOfModels = 0;
        if (scene.models != null)
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
            System.out.println("Number of actions in model " + i + ": " + scene.images.size());
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

            final float h = (float)width / (float)height;

            gl.glViewport(0, 0, width, height);
            gl.glDepthMask(true);
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glLoadIdentity();
            glu.gluPerspective(70.0f, h, 0.1f, 1000.0f);
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glLoadIdentity();
            
            // Translate to camera
            gl.glTranslatef(0, 0, -(zoomDistance * zoomDistance));
            gl.glRotatef(yRot, 1, 0, 0);
            gl.glRotatef(xRot, 0, 1, 0);
            gl.glTranslatef(xCamera, yCamera, zCamera);

            // Draw a grid on the grid for the base plane
            gl.glBegin(GL.GL_LINES);
            {
                float z = 10;
                for (int x = -32; x <= 32; x++)
                {
                    if ((x & 3) == 0)
                    {
                        gl.glColor3f(0.5f, 0.5f, 0.5f);
                    }
                    else
                    {
                        gl.glColor3f(0.25f, 0.25f, 0.25f);
                    }

                    gl.glVertex3f(x * z, 0, -32 * z);
                    gl.glVertex3f(x * z, 0, 32 * z);
                    gl.glVertex3f(-32 * z, 0, x * z);
                    gl.glVertex3f(32 * z, 0, x * z);
                }
            }
            gl.glEnd();

            // Set up the lights
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] {1, 1, 1, 1}, 0);
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[] {0.25f, 0.25f, 0.25f, 1}, 0);
            gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {0, 0.7f, 0.7f, 0}, 0);
            gl.glEnable(GL.GL_LIGHT0);
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
                start += 4000;
                System.out.println(frames / 4 + " fps");
                frames = 0;
            }
            
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
        Scene scene = null;
        if (args.length == 0)
        {
            System.out.println("No arguments. We're probably run from webstart, so load the default model");
            // No arguments. We're probably run from webstart, so load the default model
            scene = Scene.load(ModelDisplayer.class.getResourceAsStream("/DanceMagic.xsi"));
        }
        else
        {
            System.out.println("Going to load '" + args[0] + "' as a model");
            final InputStream lResourceAsStream = ModelDisplayer.class.getResourceAsStream("/" + args[0]);
            if (lResourceAsStream != null)
            {
                System.out.println("Going to load '" + args[0] + "' as a model from " + ModelDisplayer.class.getResource("/" + args[0]));
                scene = Scene.load(lResourceAsStream);
                
            }
            else
            {
                throw new IllegalArgumentException("Cannot load model from this location: " + args[0]);
            }
        }

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

        frame.setLocation(200, 0);
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
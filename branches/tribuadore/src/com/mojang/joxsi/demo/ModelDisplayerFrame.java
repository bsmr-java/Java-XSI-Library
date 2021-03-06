package com.mojang.joxsi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.mojang.joxsi.Model;
import com.mojang.joxsi.Scene;

public class ModelDisplayerFrame extends JFrame implements ActionListener
{
    /** logger - Logging instance. */
    private final static Logger logger = Logger.getLogger(ModelDisplayerFrame.class.getName());

    static {
        logger.setLevel(Level.OFF);
    }
    
    private final static String ACTION_ID_SEPERATOR = "___";
    private JMenu jmModels;

    ModelDisplayerFrame(String title)
    {
        this.setTitle(title);
        this.setJMenuBar(createMenuBar());
    }

    protected JMenuItem createMenuItem(String text, int mnemonic, String actionCommand)
    {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }

    protected JMenuItem createMenuItem(String text, String actionCommand)
    {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }

    protected JMenuBar createMenuBar()
    {
        // File section
        JMenuBar jmb = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);
        jmFile.getPopupMenu().setLightWeightPopupEnabled(false);
        jmFile.add(createMenuItem("Open XSI...", KeyEvent.VK_O, "Open XSI"));
        jmFile.add(createMenuItem("Close", KeyEvent.VK_C, "Close"));
        jmFile.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit"));

        // Models section
        this.jmModels = new JMenu("Models");
        jmFile.setMnemonic(KeyEvent.VK_M);
        jmModels.getPopupMenu().setLightWeightPopupEnabled(false);

        jmb.add(jmFile);
        jmb.add(jmModels);

        return jmb;
    }

    /**
     * Updates the Models menu.
     * 
     * @param xsiorder
     *              ordered list of xsi path's
     * @param scenes
     *              map of xsi path's to Scene
     */
    public void updateModels(List<String> xsiorder, Map<String, Scene> scenes)
    {
        String action;
        String text;
        int scenenum = 0;
        
        jmModels.removeAll();
        
        for (String xsipath : xsiorder)
        {
            Model[] models = scenes.get(xsipath).models;
        
            for (int i = 0; i < models.length; i++)
            {
                text = scenenum + ". " +  models[i].name + " [" + xsipath + "]";
                if (models[i].actions.length > 0)
                {
                    JMenu jmModelSub = new JMenu(text);
                    jmModelSub.getPopupMenu().setLightWeightPopupEnabled(false);
                    for (int j = 0; j < models[i].actions.length; j++)
                    {
                        text = models[i].actions[j].getName();
                        action = "scene" + ACTION_ID_SEPERATOR + xsipath + ACTION_ID_SEPERATOR + "model" + ACTION_ID_SEPERATOR+ i + ACTION_ID_SEPERATOR + "action" + ACTION_ID_SEPERATOR + j;
                        jmModelSub.add(createMenuItem(text, action));
                    }
                    this.jmModels.add(jmModelSub);
                }
                else
                {
                    action = "scene" + ACTION_ID_SEPERATOR + xsipath + ACTION_ID_SEPERATOR + "model" + ACTION_ID_SEPERATOR + i;
                    this.jmModels.add(createMenuItem(text, action));
                }
            }
            
            scenenum++;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {

        String action = e.getActionCommand();
        if (action == null)
        {
            logger.info("Empty ActionCommand ");
            return;
        }
        else
        {
            logger.info("ActionCommand: " + action + " source: " + e.getSource());
        }

        if (action.startsWith("scene"))
        {
            // Start the model specified after model_ and the action specified
            // after action_
            String[] nextToDo = action.split(ACTION_ID_SEPERATOR);
            String selectedScene = null;
            int selectedModel = -1;
            int selectedAction = -1;
            if (nextToDo.length >= 2) selectedScene = nextToDo[1];
            if (nextToDo.length >= 4) selectedModel = Integer.parseInt(nextToDo[3]);
            if (nextToDo.length >= 6) selectedAction = Integer.parseInt(nextToDo[5]);
            ModelDisplayer md = this.getModelDisplayer();
            if (md != null)
            {
                md.setShowScene(selectedScene);
                md.setShowModel(selectedModel);
                md.setShowAction(selectedAction);
            }
        }

        if (action.equals("Open XSI"))
            OpenXSI.open(this);
        else if (action.equals("Close"))
            getModelDisplayer().closeCurrentScene();
        else if (action.equals("Exit"))
            getModelDisplayer().stopProgram();
    }

    public ModelDisplayer getModelDisplayer()
    {
        ModelDisplayer md = null;
        for (int i = 0; i < this.getContentPane().getComponentCount(); i++)
        {
            if (this.getContentPane().getComponent(i).getClass().toString().endsWith("ModelDisplayer"))
            {
                md = (ModelDisplayer) this.getContentPane().getComponent(i);
            }
        }
        return md;
    }
}

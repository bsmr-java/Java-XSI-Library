package com.mojang.joxsi.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.mojang.joxsi.Model;
import com.mojang.joxsi.Scene;

public class ModelDisplayerFrame extends JFrame implements ActionListener
{
    private JMenu jmModels;
    private int sceneCount;

    ModelDisplayerFrame(String title)
    {
        this.sceneCount = 0;
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
        jmFile.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit"));

        // Models section
        this.jmModels = new JMenu("Models");
        jmModels.getPopupMenu().setLightWeightPopupEnabled(false);

        /**
         * Pyro Ditched the whole Animation menu as they are dependent on the
         * model which is showed at the current time. I made the Model menu
         * contain submenu's which show which animation can be shown.
         */
        // Animations Section
        /*
         * JMenu jmAnimations = new JMenu("Animations");
         * jmAnimations.getPopupMenu().setLightWeightPopupEnabled(false); //
         * TODO Dynamic list based on animations in xsi jmAnimations.add(
         * createMenuItem("Animation 1",KeyEvent.VK_1, "Animation 1"));
         * jmAnimations.add( createMenuItem("Animation 2",KeyEvent.VK_2,
         * "Animation 2")); jmAnimations.add(
         * createMenuItem("Animation 3",KeyEvent.VK_3, "Animation 3"));
         */

        jmb.add(jmFile);
        jmb.add(jmModels);
        // jmb.add(jmAnimations);

        return jmb;
    }

    /**
     * Addes models in the specified Scene to the Models menu.
     * 
     * @param scene
     */
    public void addModels(String filename, Scene scene)
    {
        Model[] models = scene.models;
        String action;
        String text;
        
        for (int i = 0; i < models.length; i++)
        {
            if (models[i].actions.length > 0)
            {
                text = models[i].name + " [" + filename + "]";
                JMenu jmModelSub = new JMenu(text);
                jmModelSub.getPopupMenu().setLightWeightPopupEnabled(false);
                for (int j = 0; j < models[i].actions.length; j++)
                {
                    text = models[i].actions[j].getName();
                    action = "scene_" + sceneCount + "_model_" + i + "_action_" + j;
                    jmModelSub.add(createMenuItem(text, action));
                }
                this.jmModels.add(jmModelSub);
            }
            else
            {
                text = models[i].name + " [" + filename + "]";
                action = "scene_" + sceneCount + "_model_" + i;
                this.jmModels.add(createMenuItem(text, action));
            }
        }
        
        // Increment the Scene counter
        sceneCount++;
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
            ModelDisplayer.logger.info("Empty ActionCommand ");
            return;
        }
        else
        {
            ModelDisplayer.logger.info("ActionCommand: " + action + " source: " + e.getSource());
        }

        if (action.startsWith("scene_"))
        {
            // Start the model specified after model_ and the action specified
            // after action_
            String[] nextToDo = action.split("_");
            int selectedScene = -1;
            int selectedModel = -1;
            int selectedAction = -1;
            if (nextToDo.length >= 2) selectedScene = Integer.parseInt(nextToDo[1]);
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
        {
            OpenXSI.open(this);
        }
        else if (action.equals("Exit"))
        {
            ModelDisplayer md = this.getModelDisplayer();
            md.stopProgram();
        }
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

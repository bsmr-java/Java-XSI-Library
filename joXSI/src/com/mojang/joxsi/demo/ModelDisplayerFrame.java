package com.mojang.joxsi.demo;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.mojang.joxsi.Model;

public final class ModelDisplayerFrame extends JFrame implements ActionListener
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(ModelDisplayerFrame.class.getName());

    ModelDisplayerFrame(final String title, final Model[] models)
    {
        this.setTitle(title);
        this.setJMenuBar(createMenuBar(models));
    }


    protected JMenuItem createMenuItem(final String text, final int mnemonic, final String actionCommand)
    { 
        final JMenuItem menuItem = new JMenuItem(text);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }

    protected JMenuItem createMenuItem(final String text, final String actionCommand)
    { 
        final JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(this);
        menuItem.setActionCommand(actionCommand);
        return menuItem;
    }

    protected JMenuBar createMenuBar(final Model[] models)
    {
        final JMenuBar jmb = new JMenuBar();
        
        // File section
        final JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);
        jmFile.getPopupMenu().setLightWeightPopupEnabled(false);

        jmFile.add(createMenuItem("Exit", KeyEvent.VK_X, "Exit"));
        
        //Materials
        final JMenu jmMaterials = new JMenu("Materials");
        jmMaterials.getPopupMenu().setLightWeightPopupEnabled(false);

        jmMaterials.add(createMenuItem("Modify Materials", "Modify Materials"));
        jmMaterials.add(createMenuItem("Reset Materials", "Reset Materials"));

        // Models section
        final JMenu jmModels = new JMenu("Models");
        jmModels.getPopupMenu().setLightWeightPopupEnabled(false);
        
        // TODO Dynamic list based on models in a specified location
        for(int i=0; i < models.length; i++)
        {
            if(models[i].actions.length > 0)
            {
                final JMenu jmModelSub = new JMenu(models[i].name);
                jmModelSub.getPopupMenu().setLightWeightPopupEnabled(false);
                for(int j=0; j<models[i].actions.length; j++)
                {
                    jmModelSub.add(createMenuItem(models[i].actions[j].getName(), "model_"+i+"_action_"+j));
                }
                jmModels.add(jmModelSub);
            }
            else
            {
                jmModels.add(createMenuItem(models[i].name, "model_"+i));
            }
        }

        jmb.add(jmFile);
        jmb.add(jmModels);
        jmb.add(jmMaterials);

        return jmb;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e)
    {
        final String action = e.getActionCommand();
        if (action == null)
        {
            logger.info("Empty ActionCommand ");
            return;
        }
        else
        {
            logger.info("ActionCommand: " + action+" source: "+e.getSource());
        }

        if(action.startsWith("model_"))
        {
            //Start the model specified after model_ and the action specified after action_
            final String[] nextToDo = action.split("_");
            int selectedModel  = -1;
            int selectedAction = -1;
            if(nextToDo.length >= 2)
                selectedModel = Integer.parseInt(nextToDo[1]);
            if(nextToDo.length >= 4)
                selectedAction = Integer.parseInt(nextToDo[3]);
            final ModelDisplayer md = this.getModelDisplayer();
            if(md != null)
            {
                md.setShowModel(selectedModel);
                md.setShowAction(selectedAction);
            }
        }

        if (action.equals("Exit"))
        {
            final ModelDisplayer md = this.getModelDisplayer();
            md.stopProgram();
        }

        if (action.equals("Modify Materials"))
        {
            final MaterialChangerFrame mc = new MaterialChangerFrame(this, this.getModelDisplayer().getScene().materials, this.getModelDisplayer().getMaterials());
            mc.setVisible(true);
        }

        if (action.equals("Reset Materials"))
        {
            final ModelDisplayer md = this.getModelDisplayer();
            md.resetMaterials();
            
        }
    }

    protected ModelDisplayer getModelDisplayer()
    {
        ModelDisplayer md = null;
        for(int i=0; i<this.getContentPane().getComponentCount(); i++)
        {
            if(this.getContentPane().getComponent(i).getClass().toString().endsWith("ModelDisplayer"))
            {
                md = (ModelDisplayer)this.getContentPane().getComponent(i);
            }
        }
        return md;
    }
}

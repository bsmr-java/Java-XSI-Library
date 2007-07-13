package com.mojang.joxsi.demo;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.mojang.joxsi.Model;

public class ModelDisplayerFrame extends JFrame implements ActionListener{

	ModelDisplayerFrame(String title, Model[] models)
	{
		this.setTitle(title);
		this.setJMenuBar(createMenuBar(models));
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
   
    protected JMenuBar createMenuBar(Model[] models)
    {
        // File section
        JMenuBar jmb = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);
        jmFile.getPopupMenu().setLightWeightPopupEnabled(false);

        jmFile.add( createMenuItem("Exit", KeyEvent.VK_X, "Exit"));
        
        // Models section
        JMenu jmModels = new JMenu("Models");
        jmModels.getPopupMenu().setLightWeightPopupEnabled(false);
// TODO Dynamic list based on models in xsi
        for(int i=0; i < models.length; i++) {
            if(models[i].actions.length > 0) {
                JMenu jmModelSub = new JMenu(models[i].name);
                jmModelSub.getPopupMenu().setLightWeightPopupEnabled(false);
                for(int j=0; j<models[i].actions.length; j++) {
                    jmModelSub.add(createMenuItem(models[i].actions[j].getName(), "model_"+i+"_action_"+j));
                }
                jmModels.add(jmModelSub);
            } else {
                jmModels.add(createMenuItem(models[i].name, "model_"+i));
            }
        }
        
        /**
         * Pyro
         * Ditched the whole Animation menu as they are dependent on the model which is
         * showed at the current time. I made the Model menu contain submenu's which show
         * which animation can be shown.
         */
        // Animations Section
        /*
        JMenu jmAnimations = new JMenu("Animations");
        jmAnimations.getPopupMenu().setLightWeightPopupEnabled(false);
// TODO Dynamic list based on animations in xsi
        jmAnimations.add( createMenuItem("Animation 1",KeyEvent.VK_1, "Animation 1"));
        jmAnimations.add( createMenuItem("Animation 2",KeyEvent.VK_2, "Animation 2"));
        jmAnimations.add( createMenuItem("Animation 3",KeyEvent.VK_3, "Animation 3"));
        */
        
        jmb.add(jmFile);
        jmb.add(jmModels);
        //jmb.add(jmAnimations);
        
        return jmb;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
 	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();
		if (action == null) {
				System.out.println("Empty ActionCommand ");
			return;
		}else{
			System.out.println("ActionCommand: " + action+" source: "+((Component) e.getSource()));
		}
		
		if(action.startsWith("model_")) {
		    //Start the model specified after model_ and the action specified after action_
		    String[] nextToDo = action.split("_");
		    int selectedModel  = -1;
		    int selectedAction = -1;
		    if(nextToDo.length >= 2)
		        selectedModel = Integer.parseInt(nextToDo[1]);
		    if(nextToDo.length >= 4)
		        selectedAction = Integer.parseInt(nextToDo[3]);
		    ModelDisplayer md = this.getModelDisplayer();
		    if(md != null) {
    		    md.setShowModel(selectedModel);
    		    md.setShowAction(selectedAction);
		    }
		}
		
		if(action.equals("Exit")) {
		    ModelDisplayer md = this.getModelDisplayer();
		    md.stopProgram();
		}
 	}
 	
 	private ModelDisplayer getModelDisplayer() {
 	    ModelDisplayer md = null;
 	    for(int i=0; i<this.getContentPane().getComponentCount(); i++) {
            if(this.getContentPane().getComponent(i).getClass().toString().endsWith("ModelDisplayer")) {
                md = (ModelDisplayer)this.getContentPane().getComponent(i);
            }
        }
 	    return md;
 	}
}

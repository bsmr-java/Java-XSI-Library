package com.mojang.joxsi.demo;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ModelDisplayerFrame extends JFrame {

	ModelDisplayerFrame(String title)
	{
		this.setTitle(title);
		this.setJMenuBar(createMenuBar());
	}
    
    
    protected JMenuItem createMenuItem(String text, int mnemonic)
    { 
    	JMenuItem menuItem = new JMenuItem(text);
    	menuItem.setMnemonic(mnemonic);
    	return menuItem;
    }
   
    protected JMenuBar createMenuBar()
    {
        // File section
        JMenuBar jmb = new JMenuBar();
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);
        jmFile.getPopupMenu().setLightWeightPopupEnabled(false);

        jmFile.add( createMenuItem("Exit", KeyEvent.VK_X));
        
        // Models section
        JMenu jmModels = new JMenu("Models");
        jmModels.getPopupMenu().setLightWeightPopupEnabled(false);
        // todo: dynamic list based on models in xsi
        jmModels.add( createMenuItem("Model A",KeyEvent.VK_A));
        jmModels.add( createMenuItem("Model B",KeyEvent.VK_B));
        jmModels.add( createMenuItem("Model C",KeyEvent.VK_C));
        
        // Animations Section
        JMenu jmAnimations = new JMenu("Animations");
        jmAnimations.getPopupMenu().setLightWeightPopupEnabled(false);
        	// todo: dynamic list based on animations in xsi
        jmAnimations.add( createMenuItem("Animation 1",KeyEvent.VK_1));
        jmAnimations.add( createMenuItem("Animation 2",KeyEvent.VK_2));
        jmAnimations.add( createMenuItem("Animation 3",KeyEvent.VK_3));
        jmb.add(jmFile);
        jmb.add(jmModels);
        jmb.add(jmAnimations);
        return(jmb);
    }
}

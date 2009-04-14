package com.mojang.joxsi.demo;

import java.util.logging.Logger;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.mojang.joxsi.loader.Template;

/**
 * A class that shows the contents of an XSI template as a JTree
 */
public class TemplateTree extends JTree
{
    /** logger - Logging instance. */
    private final static Logger logger = Logger.getLogger(TemplateTree.class.getName());
    
    /**
     * Creates an instance of TemplateTree.
     */
    public TemplateTree()
    {
        setModel(null);
    }
    
    /**
     * Sets the Template to show in the views TreeModel.
     * 
     * @param template
     *              The template to show
     */
    public void setTemplate(Template template)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(template);
        if (template != null)
        {
            populateTree(root, template);
            setModel(new DefaultTreeModel(root));
        }
        else
            setModel(null);
        validate();
    }

    /**
     * Recursively adds nodes for all sub templates for a template
     * 
     * @param node the node in the JTree to add branches to
     * @param template the template to read from
     */
    private void populateTree(DefaultMutableTreeNode node, Template template)
    {
        for (int i = 0; i < template.templates.size(); i++)
        {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(template.templates.get(i));
            node.add(child);
            logger.info("Added child to tree at level " + child.getLevel() + ": " + child);
            populateTree(child, template.templates.get(i));
        }
    }
}
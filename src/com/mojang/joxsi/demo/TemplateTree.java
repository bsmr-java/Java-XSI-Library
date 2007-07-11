package com.mojang.joxsi.demo;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.mojang.joxsi.loader.Template;

/**
 * A class that shows the contents of an XSI template as a JTree
 */
public class TemplateTree extends JTree
{
    /**
     * Creates a new TemplateTree, showing the contents of the specified Template.
     * 
     * @param template the template to show
     */
    public TemplateTree(Template template)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(template);
        populateTree(root, template);
        setModel(new DefaultTreeModel(root));
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
            System.out.println("Added child to tree at level " + child.getLevel() + ": " + child);
            populateTree(child, template.templates.get(i));
        }
    }
}
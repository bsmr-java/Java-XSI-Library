package com.mojang.joxsi.demo;

import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.mojang.joxsi.loader.Template;

/**
 * A class that shows the contents of an XSI template as a JTree.
 */
public final class TemplateTree extends JTree
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(TemplateTree.class.getName());
    /**
     * Creates a new TemplateTree, showing the contents of the specified Template.
     * 
     * @param template the template to show
     */
    public TemplateTree(final Template template)
    {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(template);
        populateTree(root, template);
        setModel(new DefaultTreeModel(root));
    }

    /**
     * Recursively adds nodes for all sub templates for a template.
     * 
     * @param node the node in the JTree to add branches to
     * @param template the template to read from
     */
    private void populateTree(final DefaultMutableTreeNode node, final Template template)
    {
        for (int i = 0; i < template.templates.size(); i++)
        {
            final DefaultMutableTreeNode child = new DefaultMutableTreeNode(template.templates.get(i));
            node.add(child);
            logger.info("Added child to tree at level " + child.getLevel() + ": " + child);
            populateTree(child, template.templates.get(i));
        }
    }
}
package com.mojang.joxsi.demo;

import java.util.Map;
import javax.swing.table.AbstractTableModel;

import com.mojang.joxsi.Material;

/*
 * Implementation of AbstractTableModel to represent the material data.
 *
 * @author Ross Bearman
 */

class MaterialTableModel extends AbstractTableModel {
    
    private String[] columnNames = {"Material Name", "Default Texture", "Current Texture"};    
    private String[][] materialData;

    /**
     * Creates a MaterialTableModel, converting a material HashMap into a String array.
     *
     * @param materials
     *        holds the name and object of each material loaded in the current scene
     */
    public MaterialTableModel(Map<String, Material> materials)
    {
        materialData = new String[materials.size()][3];

        int i = 0;
        for(Material material : materials.values())
        {
            materialData[i][0] = material.name;
            materialData[i][1] = material.defaultImageName;
            materialData[i][2] = material.imageName;
            i++;
        }
    }

    public int getRowCount() { return materialData.length; }
    public int getColumnCount() { return columnNames.length; }
    public String getColumnName(int index) { return columnNames[index]; }
    public Object getValueAt(int row, int col) { return materialData[row][col]; }
    
    /**
     * Prevents editing of the Name and Default Texture columns
     */
    public boolean isCellEditable(int row, int col) {
        if (col < 2)
            return false;
        else
            return true;
    }

    public void setValueAt(Object value, int row, int col) {

        materialData[row][col] = (String)value;
        fireTableCellUpdated(row, col);
    }

    public String[][] getMaterialData()
    {
        return materialData;
    }
}

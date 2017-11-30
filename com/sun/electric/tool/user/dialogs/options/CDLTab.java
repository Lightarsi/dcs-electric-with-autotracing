/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: CDLTab.java
 *
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Electric(tm) is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Electric(tm) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sun.electric.tool.user.dialogs.options;

import com.sun.electric.tool.simulation.SimulationTool;
import com.sun.electric.tool.user.dialogs.EDialog;
import com.sun.electric.tool.user.dialogs.PreferencesFrame;

import javax.swing.JPanel;

/**
 * Class to handle the "CDL" tab of the Preferences dialog.
 */
public class CDLTab extends PreferencePanel
{
	/** Creates new form CDLTab */
	public CDLTab(PreferencesFrame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();

		// make all text fields select-all when entered
	    EDialog.makeTextFieldSelectAllOnTab(cdlIncludeFile);
	    EDialog.makeTextFieldSelectAllOnTab(cdlLibraryName);
	    EDialog.makeTextFieldSelectAllOnTab(cdlLibraryPath);
	}

	/** return the panel to use for the user preferences. */
	public JPanel getUserPreferencesPanel() { return cdl; }

	/** return the name of this preferences tab. */
	public String getName() { return "CDL"; }

	/**
	 * Method called at the start of the dialog.
	 * Caches current values and displays them in the CDL tab.
	 */
	public void init()
	{
        cdlIncludeFile.setText(SimulationTool.getCDLIncludeFile());
		cdlLibraryName.setText(SimulationTool.getCDLLibName());
		cdlLibraryPath.setText(SimulationTool.getCDLLibPath());
		cdlConvertBrackets.setSelected(SimulationTool.isCDLConvertBrackets());
	}

	/**
	 * Method called when the "OK" panel is hit.
	 * Updates any changed fields in the CDL tab.
	 */
	public void term()
	{
		String nameNow = cdlLibraryName.getText();
		if (!nameNow.equals(SimulationTool.getCDLLibName())) SimulationTool.setCDLLibName(nameNow);

		nameNow = cdlLibraryPath.getText();
		if (!nameNow.equals(SimulationTool.getCDLLibPath())) SimulationTool.setCDLLibPath(nameNow);

        nameNow = cdlIncludeFile.getText();
        if (!nameNow.equals(SimulationTool.getCDLIncludeFile())) SimulationTool.setCDLIncludeFile(nameNow);

		boolean convertNow = cdlConvertBrackets.isSelected();
		if (convertNow != SimulationTool.isCDLConvertBrackets()) SimulationTool.setCDLConvertBrackets(convertNow);
	}

	/**
	 * Method called when the factory reset is requested.
	 */
	public void reset()
	{
		if (!SimulationTool.getFactoryCDLIncludeFile().equals(SimulationTool.getCDLIncludeFile()))
			SimulationTool.setCDLIncludeFile(SimulationTool.getFactoryCDLIncludeFile());
		if (!SimulationTool.getFactoryCDLLibName().equals(SimulationTool.getCDLLibName()))
			SimulationTool.setCDLLibName(SimulationTool.getFactoryCDLLibName());
		if (!SimulationTool.getFactoryCDLLibPath().equals(SimulationTool.getCDLLibPath()))
			SimulationTool.setCDLLibPath(SimulationTool.getFactoryCDLLibPath());
		if (SimulationTool.isFactoryCDLConvertBrackets() != SimulationTool.isCDLConvertBrackets())
			SimulationTool.setCDLConvertBrackets(SimulationTool.isFactoryCDLConvertBrackets());
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        cdl = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cdlLibraryName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        cdlLibraryPath = new javax.swing.JTextField();
        cdlConvertBrackets = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        cdlIncludeFile = new javax.swing.JTextField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("IO Options");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        cdl.setLayout(new java.awt.GridBagLayout());

        jLabel14.setText("Cadence Library Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        cdl.add(jLabel14, gridBagConstraints);

        cdlLibraryName.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        cdl.add(cdlLibraryName, gridBagConstraints);

        jLabel15.setText("Cadence Library Path:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        cdl.add(jLabel15, gridBagConstraints);

        cdlLibraryPath.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        cdl.add(cdlLibraryPath, gridBagConstraints);

        cdlConvertBrackets.setText("Convert brackets");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        cdl.add(cdlConvertBrackets, gridBagConstraints);

        jLabel1.setText("Include File");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        cdl.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        cdl.add(cdlIncludeFile, gridBagConstraints);

        getContentPane().add(cdl, new java.awt.GridBagConstraints());

        pack();
    }//GEN-END:initComponents

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cdl;
    private javax.swing.JCheckBox cdlConvertBrackets;
    private javax.swing.JTextField cdlIncludeFile;
    private javax.swing.JTextField cdlLibraryName;
    private javax.swing.JTextField cdlLibraryPath;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    // End of variables declaration//GEN-END:variables

}

/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: SUETab.java
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

import com.sun.electric.tool.io.IOTool;
import com.sun.electric.tool.user.dialogs.PreferencesFrame;

import javax.swing.JPanel;

/**
 * Class to handle the "SUE" tab of the Preferences dialog.
 */
public class SUETab extends PreferencePanel
{
	/** Creates new form SUETab */
	public SUETab(PreferencesFrame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
	}

	/** return the panel to use for user preferences. */
	public JPanel getUserPreferencesPanel() { return sue; }

	/** return the name of this preferences tab. */
	public String getName() { return "SUE"; }

	/**
	 * Method called at the start of the dialog.
	 * Caches current values and displays them in the SUE tab.
	 */
	public void init()
	{
		sueMake4PortTransistors.setSelected(IOTool.isSueUses4PortTransistors());
		sueConvertExpressions.setSelected(IOTool.isSueConvertsExpressions());
	}

	/**
	 * Method called when the "OK" panel is hit.
	 * Updates any changed fields in the SUE tab.
	 */
	public void term()
	{
		boolean currentUse4PortTransistors = sueMake4PortTransistors.isSelected();
		if (currentUse4PortTransistors != IOTool.isSueUses4PortTransistors())
			IOTool.setSueUses4PortTransistors(currentUse4PortTransistors);

		boolean currentConvertExpressions = sueConvertExpressions.isSelected();
		if (currentConvertExpressions != IOTool.isSueConvertsExpressions())
			IOTool.setSueConvertsExpressions(currentConvertExpressions);
	}

	/**
	 * Method called when the factory reset is requested.
	 */
	public void reset()
	{
		if (IOTool.isFactorySueUses4PortTransistors() != IOTool.isSueUses4PortTransistors())
			IOTool.setSueUses4PortTransistors(IOTool.isFactorySueUses4PortTransistors());
		if (IOTool.isFactorySueConvertsExpressions() != IOTool.isSueConvertsExpressions())
			IOTool.setSueConvertsExpressions(IOTool.isFactorySueConvertsExpressions());
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        sue = new javax.swing.JPanel();
        sueMake4PortTransistors = new javax.swing.JCheckBox();
        sueConvertExpressions = new javax.swing.JCheckBox();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("IO Options");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        sue.setLayout(new java.awt.GridBagLayout());

        sueMake4PortTransistors.setText("Make 4-port transistors");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sue.add(sueMake4PortTransistors, gridBagConstraints);

        sueConvertExpressions.setText("Convert SUE expressions to Electric");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        sue.add(sueConvertExpressions, gridBagConstraints);

        getContentPane().add(sue, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel sue;
    private javax.swing.JCheckBox sueConvertExpressions;
    private javax.swing.JCheckBox sueMake4PortTransistors;
    // End of variables declaration//GEN-END:variables

}

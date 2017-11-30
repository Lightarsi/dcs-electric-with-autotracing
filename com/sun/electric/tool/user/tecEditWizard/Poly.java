/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: Poly.java
 *
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.electric.tool.user.tecEditWizard;

import com.sun.electric.tool.user.Resources;
import com.sun.electric.util.TextUtils;

import java.awt.*;

/**
 * Class to handle the "Poly" tab of the Numeric Technology Editor dialog.
 */
public class Poly extends TechEditWizardPanel
{
	/** Creates new form Poly */
	public Poly(TechEditWizard parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		image.setIcon(Resources.getResource(getClass(), "Poly.png"));
		pack();
	}

	/** return the panel to use for this Numeric Technology Editor tab. */
	public Component getComponent() { return poly; }

	/** return the name of this Numeric Technology Editor tab. */
	public String getName() { return "Poly"; }

	/**
	 * Method called at the start of the dialog.
	 * Caches current values and displays them in the Poly tab.
	 */
	public void init()
	{
		TechEditWizardData data = wizard.getTechEditData();
		width.setText(TextUtils.formatDouble(data.getPolyWidth().value));
		widthRule.setText(data.getPolyWidth().rule);
		endcap.setText(TextUtils.formatDouble(data.getPolyEndcap().value));
		endcapRule.setText(data.getPolyEndcap().rule);
		activeSpacing.setText(TextUtils.formatDouble(data.getPolyDiffSpacing().value));
		activeSpacingRule.setText(data.getPolyDiffSpacing().rule);
		spacing.setText(TextUtils.formatDouble(data.getPolySpacing().value));
		spacingRule.setText(data.getPolySpacing().rule);
	}

	/**
	 * Method called when the "OK" panel is hit.
	 * Updates any changed fields in the Poly tab.
	 */
	public void term()
	{
		TechEditWizardData data = wizard.getTechEditData();
		data.setPolyWidth(new WizardField(TextUtils.atof(width.getText()), widthRule.getText()));
		data.setPolyEndcap(new WizardField(TextUtils.atof(endcap.getText()), endcapRule.getText()));
		data.setPolyDiffSpacing(new WizardField(TextUtils.atof(activeSpacing.getText()), activeSpacingRule.getText()));
		data.setPolySpacing(new WizardField(TextUtils.atof(spacing.getText()), spacingRule.getText()));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        poly = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        width = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        endcap = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        activeSpacing = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        spacing = new javax.swing.JTextField();
        image = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        widthRule = new javax.swing.JTextField();
        endcapRule = new javax.swing.JTextField();
        activeSpacingRule = new javax.swing.JTextField();
        spacingRule = new javax.swing.JTextField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Poly");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        poly.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Width (A):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 4, 1, 0);
        poly.add(jLabel1, gridBagConstraints);

        width.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 2);
        poly.add(width, gridBagConstraints);

        jLabel2.setText("Endcap (B):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 4, 1, 0);
        poly.add(jLabel2, gridBagConstraints);

        endcap.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 2);
        poly.add(endcap, gridBagConstraints);

        jLabel3.setText("Active spacing (C):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 4, 1, 0);
        poly.add(jLabel3, gridBagConstraints);

        activeSpacing.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 2);
        poly.add(activeSpacing, gridBagConstraints);

        jLabel4.setText("Spacing (D):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 4, 1, 0);
        poly.add(jLabel4, gridBagConstraints);

        spacing.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 2);
        poly.add(spacing, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        poly.add(image, gridBagConstraints);

        jLabel7.setText("Distances are in nanometers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 1, 0);
        poly.add(jLabel7, gridBagConstraints);

        jLabel5.setText("Polysilicon Parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        poly.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        poly.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Rule Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        poly.add(jLabel8, gridBagConstraints);

        widthRule.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        poly.add(widthRule, gridBagConstraints);

        endcapRule.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        poly.add(endcapRule, gridBagConstraints);

        activeSpacingRule.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        poly.add(activeSpacingRule, gridBagConstraints);

        spacingRule.setColumns(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        poly.add(spacingRule, gridBagConstraints);

        getContentPane().add(poly, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField activeSpacing;
    private javax.swing.JTextField activeSpacingRule;
    private javax.swing.JTextField endcap;
    private javax.swing.JTextField endcapRule;
    private javax.swing.JLabel image;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel poly;
    private javax.swing.JTextField spacing;
    private javax.swing.JTextField spacingRule;
    private javax.swing.JTextField width;
    private javax.swing.JTextField widthRule;
    // End of variables declaration//GEN-END:variables

}

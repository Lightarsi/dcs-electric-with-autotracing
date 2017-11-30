/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: NewExport.java
 *
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.electric.tool.user.dialogs;

import com.sun.electric.database.EditingPreferences;
import com.sun.electric.database.hierarchy.Cell;
import com.sun.electric.database.hierarchy.Export;
import com.sun.electric.database.prototype.PortCharacteristic;
import com.sun.electric.database.prototype.PortProto;
import com.sun.electric.database.topology.NodeInst;
import com.sun.electric.database.topology.PortInst;
import com.sun.electric.database.variable.ElectricObject;
import com.sun.electric.tool.Job;
import com.sun.electric.tool.JobException;
import com.sun.electric.tool.user.CircuitChangeJobs;
import com.sun.electric.tool.user.Highlight;
import com.sun.electric.tool.user.Highlighter;
import com.sun.electric.tool.user.User;
import com.sun.electric.tool.user.ui.EditWindow;
import com.sun.electric.tool.user.ui.TopLevel;

import java.awt.Frame;
import java.util.List;

import javax.swing.JOptionPane;


/**
 * Class to handle the "Create New Export" dialog.
 */
public class NewExport extends EDialog
{
	private static String latestCharacteristic;

	/** Creates new form NewExport */
	public NewExport(Frame parent)
	{
		super(parent, true);
		initComponents();
        getRootPane().setDefaultButton(ok);

		// setup the export characteristics popup
		String last = latestCharacteristic;
		for(PortCharacteristic ch : PortCharacteristic.getOrderedCharacteristics())
		{
			exportCharacteristics.addItem(ch.getName());
		}
		boolean setDefaultChar = false;
		EditWindow wnd = EditWindow.getCurrent();
		if (wnd != null)
		{
			Highlighter h = wnd.getHighlighter();
			List<Highlight> highs = h.getHighlights();
			if (highs.size() == 1)
			{
				Highlight high = highs.get(0);
				ElectricObject eObj = high.getElectricObject();
				if (eObj instanceof PortInst)
				{
					NodeInst ni = ((PortInst)eObj).getNodeInst();
					if (ni.isCellInstance())
					{
						Export e = (Export)((PortInst)eObj).getPortProto();
						exportCharacteristics.setSelectedItem(e.getCharacteristic().getName());
						setDefaultChar = true;
					}
				}
			}
		}
		if (!setDefaultChar && last != null)
			exportCharacteristics.setSelectedItem(last);
		referenceExport.setEditable(false);
		exportName.grabFocus();
		finishInitialization();
		setVisible(true);
	}

	protected void escapePressed() { cancelActionPerformed(null); }

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        exportName = new javax.swing.JTextField();
        exportCharacteristics = new javax.swing.JComboBox();
        alwaysDrawn = new javax.swing.JCheckBox();
        bodyOnly = new javax.swing.JCheckBox();
        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        referenceExport = new javax.swing.JTextField();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Create New Export");
        setName("");
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                closeDialog(evt);
            }
        });

        jLabel1.setText("Export name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("Export characteristics:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        jLabel3.setText("Reference export:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        getContentPane().add(jLabel3, gridBagConstraints);

        exportName.setPreferredSize(new java.awt.Dimension(250, 20));
        exportName.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exportNameActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(exportName, gridBagConstraints);

        exportCharacteristics.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exportCharacteristicsActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(exportCharacteristics, gridBagConstraints);

        alwaysDrawn.setText("Always drawn");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(alwaysDrawn, gridBagConstraints);

        bodyOnly.setText("Body only");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(bodyOnly, gridBagConstraints);

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 40, 4, 4);
        getContentPane().add(cancel, gridBagConstraints);

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 40);
        getContentPane().add(ok, gridBagConstraints);

        referenceExport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                referenceExportActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        getContentPane().add(referenceExport, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

	private void referenceExportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_referenceExportActionPerformed
	{//GEN-HEADEREND:event_referenceExportActionPerformed
		okActionPerformed(evt);
	}//GEN-LAST:event_referenceExportActionPerformed

	private void exportNameActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exportNameActionPerformed
	{//GEN-HEADEREND:event_exportNameActionPerformed
		okActionPerformed(evt);
	}//GEN-LAST:event_exportNameActionPerformed

	private void exportCharacteristicsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exportCharacteristicsActionPerformed
	{//GEN-HEADEREND:event_exportCharacteristicsActionPerformed
		latestCharacteristic = (String)exportCharacteristics.getSelectedItem();
		PortCharacteristic characteristic = PortCharacteristic.findCharacteristic(latestCharacteristic);
		referenceExport.setEditable(characteristic.isReference());
	}//GEN-LAST:event_exportCharacteristicsActionPerformed

	private void okActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okActionPerformed
	{//GEN-HEADEREND:event_okActionPerformed
		String name = exportName.getText();
		String referenceName = referenceExport.getText();
		String characteristics = (String)exportCharacteristics.getSelectedItem();
		boolean drawn = alwaysDrawn.isSelected();
		boolean body = bodyOnly.isSelected();
		if (name.length() <= 0)
		{
			JOptionPane.showMessageDialog(TopLevel.getCurrentJFrame(), "Must enter an export name");
			return;
		}

		// get the current node and selected port
        EditWindow wnd = EditWindow.getCurrent();
        if (wnd == null) return;
		Highlight h = wnd.getHighlighter().getOneHighlight();
		if (h == null) return;

		if (!h.isHighlightEOBJ())
		{
			System.out.println("Must select a node");
			return;
		}
		ElectricObject eobj = h.getElectricObject();
		PortProto pp = null;
		if (eobj instanceof PortInst)
		{
			pp = ((PortInst)eobj).getPortProto();
			eobj = ((PortInst)eobj).getNodeInst();
		}
		if (!(eobj instanceof NodeInst))
		{
			System.out.println("Must select a node");
			return;
		}
		NodeInst ni = (NodeInst)eobj;
		Cell parent = ni.getParent();

		if (pp == null)
		{
			if (ni.getProto().getNumPorts() == 0)
			{
				System.out.println("There is no port to export");
				return;
			}
			pp = ni.getProto().getPort(0);
		}

		// make the export
		new MakeExport(parent, ni, pp, name, body, drawn, characteristics, referenceName);
		closeDialog(null);
	}//GEN-LAST:event_okActionPerformed

	private static class MakeExport extends Job
	{
		private Cell cell;
		private NodeInst ni;
		private PortProto pp;
		private String name;
		private String referenceName;
		private boolean body;
		private boolean drawn;
		private String ch;

        protected MakeExport(Cell cell, NodeInst ni, PortProto pp, String name,
			boolean body, boolean drawn, String ch, String referenceName)
		{
			super("Make Export", User.getUserTool(), Job.Type.CHANGE, null, null, Job.Priority.USER);
			this.cell = cell;
			this.ni = ni;
			this.pp = pp;
			this.name = name;
			this.body = body;
			this.drawn = drawn;
			this.ch = ch;
			this.referenceName = referenceName;
            startJob();
		}

        @Override
		public boolean doIt() throws JobException
		{
            EditingPreferences ep = getEditingPreferences();
			// see if this export already exists
			Export e = cell.findExport(name);
			if (e != null)
			{
				// special case for exports in multipage schematics
				if (cell.isMultiPage())
				{
					int exportPage = e.getOriginalPort().getNodeInst().whichMultiPage();
					int currentPage = ni.whichMultiPage();
					if (currentPage != exportPage)
					{
						JOptionPane.showMessageDialog(TopLevel.getCurrentJFrame(),
							"That export name already exists on page " + (exportPage+1), "Duplicate Export", JOptionPane.WARNING_MESSAGE);
					}
				}
			}

			// make sure the export is possible
	        if (CircuitChangeJobs.cantEdit(cell, ni, true, true, true) != 0) return false;

			PortInst pi = ni.findPortInstFromEquivalentProto(pp);
			PortCharacteristic characteristic = PortCharacteristic.findCharacteristic(ch);
			e = Export.newInstance(cell, pi, name, ep, characteristic);
			if (e == null)
			{
				System.out.println("Failed to create export");
				return false;
			}
			e.setAlwaysDrawn(drawn);
			e.setBodyOnly(body);
			if (characteristic.isReference())
				e.newVar(Export.EXPORT_REFERENCE_NAME, referenceName, ep);

			return true;
		}
	}

	private void cancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelActionPerformed
	{//GEN-HEADEREND:event_cancelActionPerformed
		closeDialog(null);
	}//GEN-LAST:event_cancelActionPerformed

	/** Closes the dialog */
	private void closeDialog(java.awt.event.WindowEvent evt)//GEN-FIRST:event_closeDialog
	{
		setVisible(false);
		dispose();
	}//GEN-LAST:event_closeDialog

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox alwaysDrawn;
    private javax.swing.JCheckBox bodyOnly;
    private javax.swing.JButton cancel;
    private javax.swing.JComboBox exportCharacteristics;
    private javax.swing.JTextField exportName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton ok;
    private javax.swing.JTextField referenceExport;
    // End of variables declaration//GEN-END:variables

}

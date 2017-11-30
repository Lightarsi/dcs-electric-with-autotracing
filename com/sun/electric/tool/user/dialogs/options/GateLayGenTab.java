/* -*- tab-width: 4 -*-
 *
 * Electric(tm) VLSI Design System
 *
 * File: GateLayGenTab.java
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

import com.sun.electric.database.text.Setting;
import com.sun.electric.tool.generator.layout.GateLayGenSettings;
import com.sun.electric.tool.user.dialogs.PreferencesFrame;

import java.awt.Frame;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Class to handle the "Gate Layout Generator" tab of the Preferences dialog.
 */
public class GateLayGenTab extends PreferencePanel
{
	public static final long serialVersionUID=0;
    Frame parent;
    
    private Setting foundrySetting = GateLayGenSettings.getFoundrySetting();
    private Setting enableNCCSetting = GateLayGenSettings.getEnableNCCSetting();
    private Setting sizeQuantizationErrorSetting = GateLayGenSettings.getSizeQuantizationErrorSetting();
    private Setting maxMosWidthSetting = GateLayGenSettings.getMaxMosWidthSetting();
    private Setting vddYSetting = GateLayGenSettings.getVddYSetting();
    private Setting gndYSetting = GateLayGenSettings.getGndYSetting();
    private Setting nmosWellHeightSetting = GateLayGenSettings.getNmosWellHeightSetting();
    private Setting pmosWellHeightSetting = GateLayGenSettings.getPmosWellHeightSetting();
    private Setting simpleNameSetting = GateLayGenSettings.getSimpleNameSetting();
    
    //private JPanel paintinv;
    
    /** Creates new form Layout_Gen */
    public GateLayGenTab(PreferencesFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = (java.awt.Frame)parent.getOwner();
        initComponents();
        initializeFields();
    }
    
    void initializeFields() {
        techCombo.setSelectedItem(getString(foundrySetting));
        
//        libLabel.setEnabled(true);
        libCombo.setSelectedItem(getString(enableNCCSetting));
        
        quantText.setText(String.valueOf(getInt(sizeQuantizationErrorSetting)));
        mosWidthText.setText(String.valueOf(getInt(maxMosWidthSetting)));
        vddyText.setText(String.valueOf(getInt(vddYSetting)));
        gndyText.setText(String.valueOf(getInt(gndYSetting)));
        nmoswellText.setText(String.valueOf(getInt(nmosWellHeightSetting)));
        pmoswellText.setText(String.valueOf(getInt(pmosWellHeightSetting)));
        simpleNameCheck.setSelected(getBoolean(simpleNameSetting));
    }
    
    /** return the panel to use for user preferences. */
    @Override
    public JPanel getUserPreferencesPanel() {return (JPanel) getContentPane();}
    
    /** return the name of this preferences tab. */
    @Override
    public String getName() {return "Gate Layout Generator";}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        techLabel = new javax.swing.JLabel();
        techCombo = new javax.swing.JComboBox();
        nccCheck = new javax.swing.JCheckBox();
        nccCheck.setSelected(true);
        libLabel = new javax.swing.JLabel();
        libCombo = new javax.swing.JComboBox();
        help1 = new javax.swing.JButton();
        quantLabel = new javax.swing.JLabel();
        quantText = new javax.swing.JTextField();
        maxMosLabel = new javax.swing.JLabel();
        help2 = new javax.swing.JButton();
        mosWidthText = new javax.swing.JTextField();
        help3 = new javax.swing.JButton();
        vddyLabel = new javax.swing.JLabel();
        vddyText = new javax.swing.JTextField();
        help4 = new javax.swing.JButton();
        gndyLabel = new javax.swing.JLabel();
        gndyText = new javax.swing.JTextField();
        help5 = new javax.swing.JButton();
        nmoswellLabel = new javax.swing.JLabel();
        nmoswellText = new javax.swing.JTextField();
        help6 = new javax.swing.JButton();
        pmoswellLabel = new javax.swing.JLabel();
        pmoswellText = new javax.swing.JTextField();
        help7 = new javax.swing.JButton();
        simpleNameCheck = new javax.swing.JCheckBox();
        help8 = new javax.swing.JButton();
        reset = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setForeground(java.awt.Color.white);
        techLabel.setText("Technology");
        techLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(techLabel, gridBagConstraints);

        techCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MOCMOS", "CMOS90", "TSMC180" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(techCombo, gridBagConstraints);

        nccCheck.setText("Enable NCC");
        nccCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        nccCheck.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        nccCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        nccCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nccCheckActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(nccCheck, gridBagConstraints);

        libLabel.setText("Reference Library");
        libLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(libLabel, gridBagConstraints);

        libCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PurpleFour", "RedFour" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(libCombo, gridBagConstraints);

        help1.setText("?");
        help1.setToolTipText("Updates global Auto. Layout Gen. settings");
        help1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 10);
        getContentPane().add(help1, gridBagConstraints);

        quantLabel.setText("Quantization Error");
        quantLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(quantLabel, gridBagConstraints);

        quantText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        quantText.setMinimumSize(new java.awt.Dimension(5, 19));
        quantText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(quantText, gridBagConstraints);

        maxMosLabel.setText("Maximum MOS Width");
        maxMosLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(maxMosLabel, gridBagConstraints);

        help2.setText("?");
        help2.setToolTipText("Updates global Auto. Layout Gen. settings");
        help2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help2, gridBagConstraints);

        mosWidthText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mosWidthText.setMinimumSize(new java.awt.Dimension(5, 19));
        mosWidthText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(mosWidthText, gridBagConstraints);

        help3.setText("?");
        help3.setToolTipText("Updates global Auto. Layout Gen. settings");
        help3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help3ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help3, gridBagConstraints);

        vddyLabel.setText("Vdd Y Coordinate");
        vddyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(vddyLabel, gridBagConstraints);

        vddyText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        vddyText.setMinimumSize(new java.awt.Dimension(5, 5));
        vddyText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(vddyText, gridBagConstraints);

        help4.setText("?");
        help4.setToolTipText("Updates global Auto. Layout Gen. settings");
        help4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help4ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help4, gridBagConstraints);

        gndyLabel.setText("Gnd Y Corodinate");
        gndyLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(gndyLabel, gridBagConstraints);

        gndyText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gndyText.setMinimumSize(new java.awt.Dimension(5, 19));
        gndyText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(gndyText, gridBagConstraints);

        help5.setText("?");
        help5.setToolTipText("Updates global Auto. Layout Gen. settings");
        help5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help5ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help5, gridBagConstraints);

        nmoswellLabel.setText("NMOS Well Height");
        nmoswellLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(nmoswellLabel, gridBagConstraints);

        nmoswellText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nmoswellText.setMinimumSize(new java.awt.Dimension(5, 19));
        nmoswellText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(nmoswellText, gridBagConstraints);

        help6.setText("?");
        help6.setToolTipText("Updates global Auto. Layout Gen. settings");
        help6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help6ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help6, gridBagConstraints);

        pmoswellLabel.setText("PMOS Well Height");
        pmoswellLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(pmoswellLabel, gridBagConstraints);

        pmoswellText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pmoswellText.setMinimumSize(new java.awt.Dimension(5, 19));
        pmoswellText.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        getContentPane().add(pmoswellText, gridBagConstraints);

        help7.setText("?");
        help7.setToolTipText("Updates global Auto. Layout Gen. settings");
        help7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help7ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help7, gridBagConstraints);

        simpleNameCheck.setText("Simple Name");
        simpleNameCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        simpleNameCheck.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        simpleNameCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 50);
        getContentPane().add(simpleNameCheck, gridBagConstraints);

        help8.setText("?");
        help8.setToolTipText("Updates global Auto. Layout Gen. settings");
        help8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        help8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        help8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                help8ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 0);
        getContentPane().add(help8, gridBagConstraints);

        reset.setText("Reset");
        reset.setToolTipText("Updates global Auto. Layout Gen. settings");
        reset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        getContentPane().add(reset, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void help8ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help8ActionPerformed
    {//GEN-HEADEREND:event_help8ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help8ActionPerformed

    private void help7ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help7ActionPerformed
    {//GEN-HEADEREND:event_help7ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help7ActionPerformed

    private void help6ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help6ActionPerformed
    {//GEN-HEADEREND:event_help6ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help6ActionPerformed

    private void help5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help5ActionPerformed
    {//GEN-HEADEREND:event_help5ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help5ActionPerformed

    private void help1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help1ActionPerformed
    {//GEN-HEADEREND:event_help1ActionPerformed
       JOptionPane.showMessageDialog(parent, "check Enable NCC and select a library. After generating layout, NCC uses this library as a reference");
    }//GEN-LAST:event_help1ActionPerformed

    private void help2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help2ActionPerformed
    {//GEN-HEADEREND:event_help2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help2ActionPerformed

    private void help3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help3ActionPerformed
    {//GEN-HEADEREND:event_help3ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help3ActionPerformed

    private void help4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_help4ActionPerformed
    {//GEN-HEADEREND:event_help4ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_help4ActionPerformed
    
    private void resetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resetActionPerformed
    {//GEN-HEADEREND:event_resetActionPerformed
    	initializeFields();
//        techCombo.setSelectedIndex(0);
//        nccCheck.setSelected(false);
//        libLabel.setEnabled(false);
//        libCombo.setEnabled(false);
//        quantText.setText("0");
//        mosWidthText.setText("1000");
//        vddyText.setText("21");
//        gndyText.setText("-21");
//        nmoswellText.setText("84");
//        pmoswellText.setText("84");
//        simpleNameCheck.setSelected(false);
    }//GEN-LAST:event_resetActionPerformed
    
    
    private class EmptyFieldException extends Exception {
    	public static final long serialVersionUID = 0;
        String error = new String();
        
        EmptyFieldException(String error) {
            super();
            this.error = error;
        }
    }
    
    /** Method called when the "OK" panel is hit.
     * Updates any changed fields in the Layout Generation tab. */
    @Override
    public void term() {
        if(!fieldsAreValid()) return;

        setString(foundrySetting, (String)techCombo.getSelectedItem());

        String newNccCheck = nccCheck.isSelected() ? (String)libCombo.getSelectedItem() : "";
        setString(enableNCCSetting, newNccCheck);
        
        setInt(sizeQuantizationErrorSetting, Integer.parseInt(quantText.getText()));
        setInt(maxMosWidthSetting, Integer.parseInt(mosWidthText.getText()));
        setInt(vddYSetting, Integer.parseInt(vddyText.getText()));
        setInt(gndYSetting, Integer.parseInt(gndyText.getText()));
        setInt(nmosWellHeightSetting, Integer.parseInt(nmoswellText.getText()));
        setInt(pmosWellHeightSetting, Integer.parseInt(pmoswellText.getText()));
        setBoolean(simpleNameSetting, simpleNameCheck.isSelected());
    }
    
    private boolean fieldsAreValid() {
        String param = new String();
        //String errorField = new String();
        
        try {
            //REFERENCE LIBRARY
            if(nccCheck.isSelected()) {
                param = (String)libCombo.getSelectedItem();
                if(param.equals(""))
                    throw new EmptyFieldException("Library Reference");
            }
            
            //QUANTIZATION ERROR
            param = quantText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("Quantization Error");
            
            //MAX. MOS WIDTH
            param = mosWidthText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("Maximum MOS Width");
            
            //VDD Y COORDINATE
            param = vddyText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("VDD Y Coordinate");
            
            //Gnd Y Coordinate
            param = gndyText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("Gnd Y Coordinate");
            
            //NMOS Well Height
            param = nmoswellText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("NMOS Well Height");
            
            //PMOS Well Height
            param = pmoswellText.getText();
            if(param.equals(""))
                throw new EmptyFieldException("PMOS Well Height");
            
            //Set Simple Name
            //boolean paramBl = simpleNameCheck.isSelected();
        }
        
        catch(EmptyFieldException e) {
            JOptionPane.showMessageDialog(parent, "Cannot leave " + e.error + " blank!");
            return false;
        }
        
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Non-numeric value entered in a numeric field!");
            return false;
        }
        
        return true;
    }
        
    private void nccCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nccCheckActionPerformed
// TODO add your handling code here:
        boolean check = nccCheck.isSelected();
        
        libLabel.setEnabled(check);
        libCombo.setEnabled(check);
        
    }//GEN-LAST:event_nccCheckActionPerformed
    
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[])
//    {
//        java.awt.EventQueue.invokeLater(new Runnable()
//        {
//            public void run()
//            {
//                new GateLayGenTab(new javax.swing.JFrame(), true).setVisible(true);
//            }
//        });
//    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel gndyLabel;
    private javax.swing.JTextField gndyText;
    private javax.swing.JButton help1;
    private javax.swing.JButton help2;
    private javax.swing.JButton help3;
    private javax.swing.JButton help4;
    private javax.swing.JButton help5;
    private javax.swing.JButton help6;
    private javax.swing.JButton help7;
    private javax.swing.JButton help8;
    private javax.swing.JComboBox libCombo;
    private javax.swing.JLabel libLabel;
    private javax.swing.JLabel maxMosLabel;
    private javax.swing.JTextField mosWidthText;
    private javax.swing.JCheckBox nccCheck;
    private javax.swing.JLabel nmoswellLabel;
    private javax.swing.JTextField nmoswellText;
    private javax.swing.JLabel pmoswellLabel;
    private javax.swing.JTextField pmoswellText;
    private javax.swing.JLabel quantLabel;
    private javax.swing.JTextField quantText;
    private javax.swing.JButton reset;
    private javax.swing.JCheckBox simpleNameCheck;
    private javax.swing.JComboBox techCombo;
    private javax.swing.JLabel techLabel;
    private javax.swing.JLabel vddyLabel;
    private javax.swing.JTextField vddyText;
    // End of variables declaration//GEN-END:variables
    
    /** Closes the dialog */
//    private void closeDialog(java.awt.event.WindowEvent evt)
//    {
//        setVisible(false);
//        dispose();
//    }
    
}



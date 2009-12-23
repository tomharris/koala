package org.koala.ui;
/*
 * Created on Aug 14, 2005
 *
 */

/**
 * @author tom
 *
 * TODO
 */


import java.awt.GridLayout;

import javax.swing.*;

import org.koala.model.Report;

public class ReportGUI extends DriverGUI {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	private Report report = null;

	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JButton backButton = null;
	private JButton printButton = null;
	private JScrollPane reportScrollPane = null;
	private JTextArea reportTextArea = null;

	/**
	 * This is the default constructor
	 */
	public ReportGUI(Report report) {
		super();
		this.report = report;

		initialize();
		this.setTitle(APP_NAME + ": Report");

		//generate the report
		this.report.doReport();

		//put the report in the textarea
		reportTextArea.setText(this.report.toString());

		backButton.requestFocus();
	}

	protected void finalize() {
		this.jContentPane = null;
		this.buttonPanel = null;
		this.backButton = null;
		this.printButton = null;
		this.reportScrollPane = null;
		this.reportTextArea = null;

		this.report = null;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450,350);
		this.setContentPane(getJContentPane());
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getReportScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1,2));
			buttonPanel.add(getPrintButton(), null);
			buttonPanel.add(getBackButton(), null);
		}
		return buttonPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton();
			backButton.setText("Close");
			backButton.setFont(BUTTON_TEXT_FONT);
			backButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                DriverGUI.closeModelGui();
	            }
	        });
		}
		return backButton;
	}
	/**
	 * This method initializes printButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPrintButton() {
		if (printButton == null) {
			printButton = new JButton();
			printButton.setText("Print");
			printButton.setFont(BUTTON_TEXT_FONT);
			printButton.setEnabled(this.report.isPrintable());
			printButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                report.print();
	            }
	        });
		}
		return printButton;
	}

	/**
	 * This method initializes reportScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getReportScrollPane() {
		if (reportScrollPane == null) {
			reportScrollPane = new JScrollPane();
			reportScrollPane.setViewportView(getReportTextArea());
		}
		return reportScrollPane;
	}
	/**
	 * This method initializes reportTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getReportTextArea() {
		if (reportTextArea == null) {
			reportTextArea = new JTextArea();
			reportTextArea.setEditable(false);
		}
		return reportTextArea;
	}
   }

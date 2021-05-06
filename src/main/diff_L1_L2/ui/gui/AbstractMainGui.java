/*****************************************************************************************
 *
 *   This file is part of JNdiff project.
 *
 *   JNdiff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   JNdiff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.ui.gui;

import main.diff_L1_L2.ui.gui.menus.MainMenu;
import main.diff_L1_L2.ui.gui.panels.FileSelectorPanel;
import main.diff_L1_L2.ui.gui.panels.OperationsPanel;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractMainGui extends JFrame {

	private static final long serialVersionUID = 1L;
	protected JFrame mainFrame = null;
	protected JPanel mainPanel = null;
	protected JMenuBar mainMenuBar = null;

	protected OperationsPanel operationsPanel = null;
	protected FileSelectorPanel fileSelectorPanel2;
	protected FileSelectorPanel fileSelectorPanel1;
	protected FileSelectorPanel fileSelectorPanel3;
	protected JPanel selectionFilePanel = null;
	protected JPanel buttonsPanel = null;
	protected JButton okButton = null;
	protected JCheckBox useXSLcheck = null;
	protected FileSelectorPanel fileSelectorXSLpanel;

	/**
	 * This method initializes
	 * 
	 */
	public AbstractMainGui() {
		super();
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setJMenuBar(getMainMenuBar());
		this.setContentPane(getMainPanel());

	}

	abstract void changeOperationStatus();

	/**
	 * This method initializes buttonsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout());
			buttonsPanel.add(getOkButton(), null);

		}
		return buttonsPanel;
	}

	protected FileSelectorPanel getFileSelectorPanel1() {
		if (fileSelectorPanel1 == null) {
			fileSelectorPanel1 = new FileSelectorPanel();

		}
		return fileSelectorPanel1;
	}

	protected FileSelectorPanel getFileSelectorPanel2() {
		if (fileSelectorPanel2 == null) {
			fileSelectorPanel2 = new FileSelectorPanel();

		}
		return fileSelectorPanel2;
	}

	protected FileSelectorPanel getFileSelectorPanel3() {
		if (fileSelectorPanel3 == null) {
			fileSelectorPanel3 = new FileSelectorPanel();

		}
		return fileSelectorPanel3;
	}

	protected FileSelectorPanel getFileSelectorXSLPanel() {
		if (fileSelectorXSLpanel == null) {
			fileSelectorXSLpanel = new FileSelectorPanel();

			fileSelectorXSLpanel.add(getUseXSLcheck(), BorderLayout.NORTH);
			fileSelectorXSLpanel.setEnabled(false);
		}
		return fileSelectorXSLpanel;
	}

	/**
	 * This method initializes mainMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	protected JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new MainMenu();

		}
		return mainMenuBar;
	}

	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getSelectionFilePanel(), BorderLayout.CENTER);
			mainPanel.add(getOperationsPanel(), BorderLayout.EAST);
			mainPanel.add(getButtonsPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					startOperation();
				}
			});
		}
		return okButton;
	}

	protected OperationsPanel getOperationsPanel() {
		if (operationsPanel == null) {
			operationsPanel = new OperationsPanel() {

				private static final long serialVersionUID = 1L;

				@Override
				public void checkChangeStatus() {
					changeOperationStatus();
				}
			};

		}
		return operationsPanel;

	}

	/**
	 * This method initializes selectionFilePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getSelectionFilePanel() {
		if (selectionFilePanel == null) {
			selectionFilePanel = new JPanel();
			selectionFilePanel.setLayout(new BoxLayout(getSelectionFilePanel(),
					BoxLayout.Y_AXIS));
			selectionFilePanel.add(getFileSelectorPanel1(), null);
			selectionFilePanel.add(getFileSelectorPanel2(), null);
			selectionFilePanel.add(getFileSelectorPanel3(), null);
			selectionFilePanel.add(getFileSelectorXSLPanel(), null);
		}
		return selectionFilePanel;
	}

	/**
	 * This method initializes useXSLcheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	protected JCheckBox getUseXSLcheck() {
		if (useXSLcheck == null) {
			useXSLcheck = new JCheckBox();
			useXSLcheck.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					xslCheckChange();
				}
			});
		}
		return useXSLcheck;
	}

	abstract void startOperation();

	abstract void xslCheckChange();
}

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

import main.diff_L1_L2.ui.OperationsHandler;
import main.diff_L1_L2.ui.Parameters;
import main.diff_L1_L2.ui.i18n.MessageHandler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

public class MainGui extends AbstractMainGui {

	private static final long serialVersionUID = 1L;
	private static final String BUNDLE_PROPERTY = "mainGUI";
	private MessageHandler messages = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainGui application = new MainGui();
				application.setVisible(true);
			}
		});
	}

	public MainGui() {
		super();
		messages = new MessageHandler(BUNDLE_PROPERTY);
		initialize();
		pack();
	}

	private void initialize() {
		// Titolo della finestra
		this.setTitle(messages.getString("MainGui.MAIN_TITLE"));

		// Primo pannello di selezione file
		fileSelectorPanel1
				.setBorder(BorderFactory.createTitledBorder(
						null,
						messages.getString("MainGui.ORIGINAL_FILE"), TitledBorder.DEFAULT_JUSTIFICATION, //$NON-NLS-1$
						TitledBorder.DEFAULT_POSITION, new Font("Dialog",
								Font.BOLD, 12), new Color(51, 51, 51)));
		// Secondo pannello di selezione file

		fileSelectorPanel2
				.setBorder(BorderFactory.createTitledBorder(
						null,
						messages.getString("MainGui.MODIFIED_FILE"), TitledBorder.DEFAULT_JUSTIFICATION, //$NON-NLS-1$
						TitledBorder.DEFAULT_POSITION, new Font("Dialog",
								Font.BOLD, 12), new Color(51, 51, 51)));
		// Terzo pannello di selezione file (output)

		fileSelectorPanel3
				.setBorder(BorderFactory.createTitledBorder(
						null,
						messages.getString("MainGui.OUTPUT_FILE"), TitledBorder.DEFAULT_JUSTIFICATION, //$NON-NLS-1$
						TitledBorder.DEFAULT_POSITION, new Font("Dialog",
								Font.BOLD, 12), new Color(51, 51, 51)));
		// Pannello di selezione file XSL

		fileSelectorXSLpanel
				.setBorder(BorderFactory.createTitledBorder(
						null,
						messages.getString("MainGui.XSL_FILE"), TitledBorder.DEFAULT_JUSTIFICATION, //$NON-NLS-1$
						TitledBorder.DEFAULT_POSITION, new Font("Dialog",
								Font.BOLD, 12), new Color(51, 51, 51)));
		useXSLcheck.setText(messages.getString("MainGui.USE_XSL"));
		// Pulsante start
		okButton.setText(messages.getString("MainGui.START_OPERATION"));

	}

	@Override
	void changeOperationStatus() {

		switch (operationsPanel.getSelectedOperation()) {
		case 1:
		case 3:
			// Diff o Diff & Merge
			fileSelectorPanel1.setTitle("Original file:");
			fileSelectorPanel2.setTitle("Modified file:");
			break;
		case 2:
			// Merge
			fileSelectorPanel1.setTitle("Original file:");
			fileSelectorPanel2.setTitle("Delta file:");
			break;
		default:

		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fileSelectorPanel1.setEnabled(enabled);
		fileSelectorPanel2.setEnabled(enabled);
		fileSelectorPanel3.setEnabled(enabled);
		useXSLcheck.setEnabled(enabled);
		if (useXSLcheck.isSelected()) {
			fileSelectorXSLpanel.setEnabled(enabled);
		} else {
			fileSelectorXSLpanel.setEnabled(false);
		}
		operationsPanel.setEnabled(enabled);
		okButton.setEnabled(enabled);

	}

	@Override
	protected void startOperation() {
		setEnabled(false);
		doOperation();
		setEnabled(true);

	}

	private void doOperation() {
		Parameters params = new Parameters();
		params.setMerge(operationsPanel.isMerge());
		params.setDiff(operationsPanel.isDiff());
		// Nell'interfaccia grafica non uso lo standard output
		params.setStdout(false);
		// Per ora non supporta l'XSLT
		params.setXslt(false);
		File tempFile;
		tempFile = fileSelectorPanel1.getSelectedFile();
		if (tempFile.canRead() && tempFile.isFile()) {
			params.setOriginalPath(fileSelectorPanel1.getSelectedStringFile());
		} else {
			JOptionPane.showMessageDialog((this), "File: \""
					+ fileSelectorPanel1.getSelectedStringFile()
					+ "\" not exists or is not readable", "JNDiff Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		tempFile = fileSelectorPanel2.getSelectedFile();
		if (tempFile.canRead() && tempFile.isFile()) {
			if (operationsPanel.isDiff()) {
				params.setModifiedPath(fileSelectorPanel2
						.getSelectedStringFile());
			} else {

				params.setDeltaPath(fileSelectorPanel2.getSelectedStringFile());

			}

		} else {
			JOptionPane.showMessageDialog((this), "File: \""
					+ fileSelectorPanel2.getSelectedStringFile()
					+ "\" not exists or is not readable", "JNDiff Error",
					JOptionPane.ERROR_MESSAGE);

			return;
		}

		tempFile = fileSelectorPanel3.getSelectedFile();
		if (tempFile.getParentFile().isDirectory()
				&& tempFile.getParentFile().canWrite()) {
			if (tempFile.isFile() && tempFile.canWrite()) {

				int overWriteDecision = JOptionPane.showConfirmDialog(this,
						"File \"" + tempFile.getAbsolutePath() + "\"\n"
								+ "Already exists.\n" + "Do you overwrite?",
						"JNDiff warning:", JOptionPane.YES_NO_OPTION);

				switch (overWriteDecision) {
				case 0:
					// SI
					break;
				case 1:
					// NO
				default:
					// ANNULLA
					return;
				}
			}

			if (operationsPanel.isMerge()) {
				params.setMarkupPath(fileSelectorPanel3.getSelectedStringFile());
			} else {
				params.setDeltaPath(fileSelectorPanel3.getSelectedStringFile());
			}
		}
		try {
			OperationsHandler.doOperation(params);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void xslCheckChange() {
		fileSelectorXSLpanel.setEnabled(useXSLcheck.isSelected());
	}

}

package main.diff_L1_L2.ui.gui.panels;

import main.diff_L1_L2.ui.i18n.MessageHandler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class OperationsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JRadioButton diffOperation = null;
	private JRadioButton mergeOperation = null;
	private JRadioButton diffMergeOperation = null;
	private final static MessageHandler messages = new MessageHandler("mainGUI");

	/**
	 * This is the default constructor
	 */
	public OperationsPanel() {
		super();
		initialize();
	}

	protected void checkChangeStatus() {

	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		diffOperation.setEnabled(enabled);
		mergeOperation.setEnabled(enabled);
		diffMergeOperation.setEnabled(enabled);
	}

	/**
	 * This method initializes diffMergeOperation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDiffMergeOperation() {
		if (diffMergeOperation == null) {
			diffMergeOperation = new JRadioButton();
			diffMergeOperation.setText(messages.getString("OperationsPanel.DIFF_MERGE"));
			diffMergeOperation
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkChangeStatus();
						}
					});
		}
		return diffMergeOperation;
	}

	/**
	 * This method initializes diffOperation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDiffOperation() {
		if (diffOperation == null) {
			diffOperation = new JRadioButton();
			diffOperation.setText(messages.getString("OperationsPanel.DIFF"));
			diffOperation
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkChangeStatus();
						}
					});

		}
		return diffOperation;
	}

	/**
	 * This method initializes mergeOperation
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getMergeOperation() {
		if (mergeOperation == null) {
			mergeOperation = new JRadioButton();
			mergeOperation.setText(messages.getString("OperationsPanel.MERGE"));
			mergeOperation
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							checkChangeStatus();
						}
					});
		}
		return mergeOperation;
	}

	public int getSelectedOperation() {
		if (isMerge() && isDiff())
			return 3;
		if (isMerge())
			return 2;
		if (isDiff())
			return 1;
		return 0; // impossibile :D
	}

	/*
	 * public void disable() { diffOperation.setEnabled(false);
	 * mergeOperation.setEnabled(false); diffMergeOperation.setEnabled(false); }
	 * 
	 * public void enable() { diffOperation.setEnabled(true);
	 * mergeOperation.setEnabled(true); diffMergeOperation.setEnabled(true); }
	 */
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder(null,messages.getString("OperationsPanel.OPERATION"),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		ButtonGroup group = new ButtonGroup();
		this.add(getDiffOperation(), null);
		this.add(getMergeOperation(), null);
		this.add(getDiffMergeOperation(), null);
		group.add(getDiffOperation());
		getDiffOperation().setSelected(true);
		group.add(getMergeOperation());
		group.add(getDiffMergeOperation());
	}

	public boolean isDiff() {

		return (diffOperation.isSelected() || diffMergeOperation.isSelected());
	}

	public boolean isMerge() {

		return (mergeOperation.isSelected() || diffMergeOperation.isSelected());

	}

}

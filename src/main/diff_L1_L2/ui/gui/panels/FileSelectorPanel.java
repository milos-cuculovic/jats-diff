package main.diff_L1_L2.ui.gui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;

public class FileSelectorPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField sourceFileText = null;
	private JButton sourceFileButton = null;
	private JFileChooser fileChooser;

	/**
	 * This is the default constructor
	 */
	public FileSelectorPanel() {
		super();
		initialize();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		sourceFileButton.setEnabled(enabled);
		sourceFileText.setEnabled(enabled);
	}

	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();

		}
		return fileChooser;
	}

	private JTextField getFileText() {
		if (sourceFileText == null) {
			sourceFileText = new JTextField();
			sourceFileText.setColumns(50);
		}
		return sourceFileText;
	}

	public File getSelectedFile() {

		return new File(sourceFileText.getText());

	}

	public String getSelectedStringFile() {
		return sourceFileText.getText();

	}

	/**
	 * This method initializes sourceFileSelect1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSourceFileButton() {
		if (sourceFileButton == null) {
			sourceFileButton = new JButton();
			sourceFileButton.setText("...");
			sourceFileButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openFile();
						}
					});
		}
		return sourceFileButton;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getFileText(), BorderLayout.CENTER);
		this.add(getSourceFileButton(), BorderLayout.EAST);
		this.getFileChooser();
	}

	protected void openFile() {

		File sourceFile = new File(sourceFileText.getText());

		if (sourceFile.isDirectory()) {
			fileChooser.setCurrentDirectory(sourceFile);
		}

		if (sourceFile.isFile()) {
			fileChooser.setCurrentDirectory(sourceFile.getParentFile());

		}

		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			sourceFileText.setText(fileChooser.getSelectedFile().getPath());
		}
	}

	public void setTitle(String title) {
		this.setBorder(BorderFactory.createTitledBorder(null, title,
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION,
				new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));

	}

}

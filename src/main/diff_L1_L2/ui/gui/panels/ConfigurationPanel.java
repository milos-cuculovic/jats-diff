package main.diff_L1_L2.ui.gui.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JSlider findUpdate = null;
	private JLabel findUpdateLabel = null;
	private JPanel FindMovePanel = null;
	private JSlider findMoveRangeSlider = null;
	private JLabel minWeightLabel = null;
	private JLabel FindMoveRangeLabel = null;
	private JSlider findMoveMinWeightSlider = null;
	private JPanel PropagationPanel = null;
	private JLabel PropagationAttSimilarityLabel = null;
	private JSlider PropagationAttSimilaritySlider = null;
	private JCheckBox PropagationForceMatchOption = null;
	private JPanel OptionPanel = null;
	private JPanel GeneralOptionPanel = null;
	private JCheckBox ltrimCheck = null;
	private JCheckBox rtrimCheck = null;
	private JCheckBox emptyNodeCheck = null;
	private JCheckBox collapseCheck = null;
	private JCheckBox commentNodeCheck = null;
	private OperationsPanel operationsPanel;

	/**
	 * This is the default constructor
	 */
	public ConfigurationPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes collapseCheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCollapseCheck() {
		if (collapseCheck == null) {
			collapseCheck = new JCheckBox();
			collapseCheck.setSelected(true);
			collapseCheck.setText("Collapse");
		}
		return collapseCheck;
	}

	/**
	 * This method initializes commentNodeCheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCommentNodeCheck() {
		if (commentNodeCheck == null) {
			commentNodeCheck = new JCheckBox();
			commentNodeCheck.setSelected(true);
			commentNodeCheck.setText("Comment Node");
		}
		return commentNodeCheck;
	}

	/**
	 * This method initializes emptyNodeCheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getEmptyNodeCheck() {
		if (emptyNodeCheck == null) {
			emptyNodeCheck = new JCheckBox();
			emptyNodeCheck.setSelected(true);
			emptyNodeCheck.setText("Empty Node");
		}
		return emptyNodeCheck;
	}

	/**
	 * This method initializes findMoveMinWeightSlider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getFindMoveMinWeightSlider() {
		if (findMoveMinWeightSlider == null) {
			findMoveMinWeightSlider = new JSlider();
		}
		return findMoveMinWeightSlider;
	}

	/**
	 * This method initializes FindMovePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFindMovePanel() {
		if (FindMovePanel == null) {
			FindMoveRangeLabel = new JLabel();
			FindMoveRangeLabel.setText("Range:");
			minWeightLabel = new JLabel();
			minWeightLabel.setText("Min weight:");
			FindMovePanel = new JPanel();
			FindMovePanel.setLayout(new FlowLayout());
			FindMovePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Find Move options:", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			FindMovePanel.add(FindMoveRangeLabel, null);
			FindMovePanel.add(getFindMoveRangeSlider(), null);
			FindMovePanel.add(minWeightLabel, null);
			FindMovePanel.add(getFindMoveMinWeightSlider(), null);
		}
		return FindMovePanel;
	}

	/**
	 * This method initializes findMoveO
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getFindMoveRangeSlider() {
		if (findMoveRangeSlider == null) {
			findMoveRangeSlider = new JSlider();
			findMoveRangeSlider.setPaintLabels(true);
			findMoveRangeSlider.setPaintTicks(true);
		}
		return findMoveRangeSlider;
	}

	/**
	 * This method initializes findUpdate
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getFindUpdate() {
		if (findUpdate == null) {
			findUpdate = new JSlider();
			findUpdate.setMinimum(20);
			findUpdate.setPaintLabels(true);
			findUpdate.setPaintTicks(true);
			findUpdate.setSnapToTicks(true);
			findUpdate.setMinorTickSpacing(10);
		}
		return findUpdate;
	}

	/**
	 * This method initializes GeneralOptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeneralOptionPanel() {
		if (GeneralOptionPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(2);
			GeneralOptionPanel = new JPanel();
			GeneralOptionPanel.setBorder(BorderFactory.createTitledBorder(null,
					"General options:", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			GeneralOptionPanel.setLayout(gridLayout);
			GeneralOptionPanel.add(getLtrimCheck(), null);
			GeneralOptionPanel.add(getRtrimCheck(), null);
			GeneralOptionPanel.add(getEmptyNodeCheck(), null);
			GeneralOptionPanel.add(getCollapseCheck(), null);
			GeneralOptionPanel.add(getCommentNodeCheck(), null);
		}
		return GeneralOptionPanel;
	}

	/**
	 * This method initializes ltrimCheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getLtrimCheck() {
		if (ltrimCheck == null) {
			ltrimCheck = new JCheckBox();
			ltrimCheck.setSelected(true);
			ltrimCheck.setText("LTrim");
		}
		return ltrimCheck;
	}

	private OperationsPanel getOperationsPanel() {
		if (operationsPanel == null) {
			operationsPanel = new OperationsPanel();

		}
		return operationsPanel;
	}

	/**
	 * This method initializes OptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOptionPanel() {
		if (OptionPanel == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(2);
			gridLayout1.setColumns(2);
			OptionPanel = new JPanel();
			OptionPanel.setLayout(gridLayout1);
			OptionPanel.add(getGeneralOptionPanel(), null);
			OptionPanel.add(getOperationsPanel(), null);
			OptionPanel.add(getPropagationPanel(), null);
			OptionPanel.add(getFindMovePanel(), null);
		}
		return OptionPanel;
	}

	/**
	 * This method initializes PropagationAttSimilaritySlider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getPropagationAttSimilaritySlider() {
		if (PropagationAttSimilaritySlider == null) {
			PropagationAttSimilaritySlider = new JSlider();
			PropagationAttSimilaritySlider.setPaintLabels(true);
			PropagationAttSimilaritySlider.setPaintTicks(true);
		}
		return PropagationAttSimilaritySlider;
	}

	/**
	 * This method initializes PropagationForceMatchOption
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getPropagationForceMatchOption() {
		if (PropagationForceMatchOption == null) {
			PropagationForceMatchOption = new JCheckBox();
			PropagationForceMatchOption.setText("Force match");
		}
		return PropagationForceMatchOption;
	}

	/**
	 * This method initializes PropagationPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropagationPanel() {
		if (PropagationPanel == null) {
			PropagationAttSimilarityLabel = new JLabel();
			PropagationAttSimilarityLabel.setText("Attribute Similarity:");
			PropagationPanel = new JPanel();
			PropagationPanel.setLayout(new FlowLayout());
			PropagationPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Propagation options:", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			PropagationPanel.add(PropagationAttSimilarityLabel, null);
			PropagationPanel.add(getPropagationAttSimilaritySlider(), null);
			PropagationPanel.add(getPropagationForceMatchOption(), null);
		}
		return PropagationPanel;
	}

	/**
	 * This method initializes rtrimCheck
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getRtrimCheck() {
		if (rtrimCheck == null) {
			rtrimCheck = new JCheckBox();
			rtrimCheck.setSelected(true);
			rtrimCheck.setText("Rtrim");
		}
		return rtrimCheck;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		findUpdateLabel = new JLabel();
		findUpdateLabel.setText("FindUpdate percentage:");
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		this.setSize(650, 327);
		this.add(getOptionPanel(), null);
		this.add(findUpdateLabel, null);
		this.add(getFindUpdate(), null);
	}

}
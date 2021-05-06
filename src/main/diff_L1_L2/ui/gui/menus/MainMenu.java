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

package main.diff_L1_L2.ui.gui.menus;

import main.diff_L1_L2.ui.i18n.MessageHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * Classe che rappresenta il menù nell'interfaccia grafica MainGui
 * 
 * */
public class MainMenu extends JMenuBar {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenuItem exitMenuItem = null;
	private JCheckBoxMenuItem leftTrimMenuCheck = null;
	private JCheckBoxMenuItem rightTrimMenuCheck = null;
	private JCheckBoxMenuItem collapseMenuCheck = null;
	private JCheckBoxMenuItem emptyNodeMenuCheck = null;
	private JCheckBoxMenuItem commentNodeMenuCheck = null;
	private JMenu generalOptionsMenu = null;
	private JMenu advancedOptionsMenu = null;
	private JMenu findUpdateMenu = null;
	private JMenu findMoveMenu = null;
	//private JMenu propagationMenu = null;

	private static final MessageHandler messages = new MessageHandler("mainGUI");

	public MainMenu() {
		initialize();
	}

	/**
	 * Restituisce il sotto menù avanzato
	 * 
	 * @return javax.swing.JMenu
	 */
	@SuppressWarnings("unused")
	private JMenu getAdvancedOptionsMenu() {
		if (advancedOptionsMenu == null) {
			advancedOptionsMenu = new JMenu();
			advancedOptionsMenu.add(getFindUpdateMenu());
		}
		return advancedOptionsMenu;
	}

	/**
	 * Restituisce il sotto menù Find Move
	 * 
	 * @return javax.swing.JMenu
	 * 
	 * TODO 
	 */
	@SuppressWarnings("unused")
	private JMenu getFindMoveMenu() {
		if (findMoveMenu == null) {
			findMoveMenu = new JMenu();
			// findMoveMenu.add();
		}
		return findMoveMenu;
	}

	/**
	 * Restituisce l'elemento menù "Collapse node"
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getCollapseMenuCheck() {
		if (collapseMenuCheck == null) {
			collapseMenuCheck = new JCheckBoxMenuItem();
			collapseMenuCheck.setText(messages.getString("MainMenu.COLLAPSE"));
		}
		return collapseMenuCheck;
	}

	/**
	 * Restituisce l'elemento menù "Comment node"
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getCommentNodeMenuCheck() {
		if (commentNodeMenuCheck == null) {
			commentNodeMenuCheck = new JCheckBoxMenuItem();
			commentNodeMenuCheck.setText(messages
					.getString("MainMenu.COMMENTNODE"));
		}
		return commentNodeMenuCheck;
	}

	/**
	 * Restituisce il menù dell'applicazione MainGui
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText(messages.getString("MainMenu.OPTIONS"));
			editMenu.add(getGeneralOptionsMenu());

			// TODO Fare il menù con le altre opzioni tra cui findupdate
			// propagation...
			// editMenu.add(getAdvancedOptionsMenu());
		}
		return editMenu;
	}

	/**
	 * Restituisce l'elemento menù "Empty node"
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getEmptyNodeMenuCheck() {
		if (emptyNodeMenuCheck == null) {
			emptyNodeMenuCheck = new JCheckBoxMenuItem();
			emptyNodeMenuCheck
					.setText(messages.getString("MainMenu.EMPTYNODE"));
		}
		return emptyNodeMenuCheck;
	}

	/**
	 * Restituisce l'elemento menù "Exit"
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText(messages.getString("MainMenu.EXIT"));
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * Restituisce il sotto-menù "File"
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText(messages.getString("MainMenu.FILE"));
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * Restituisce l'elemento menù "Find Update"
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFindUpdateMenu() {
		if (findUpdateMenu == null) {
			findUpdateMenu = new JMenu();
			findUpdateMenu.setText(messages.getString("MainMenu.FIND_UPDATE"));
		}
		return findUpdateMenu;
	}

	/**
	 * Restituisce il sotto-menù "General options"
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getGeneralOptionsMenu() {
		if (generalOptionsMenu == null) {
			generalOptionsMenu = new JMenu();
			generalOptionsMenu.setText(messages
					.getString("MainMenu.GENERAL_OPTIONS"));
			generalOptionsMenu.add(getLeftTrimMenuCheck());
			generalOptionsMenu.add(getRightTrimMenuCheck());
			generalOptionsMenu.add(getCollapseMenuCheck());
			generalOptionsMenu.add(getEmptyNodeMenuCheck());
			generalOptionsMenu.add(getCommentNodeMenuCheck());
		}
		return generalOptionsMenu;
	}

	/**
	 * Restituisce l'elemento menù "Left trim"
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getLeftTrimMenuCheck() {
		if (leftTrimMenuCheck == null) {
			leftTrimMenuCheck = new JCheckBoxMenuItem();
			leftTrimMenuCheck.setText(messages.getString("MainMenu.LTRIM"));
		}
		return leftTrimMenuCheck;
	}

	/**
	 * Restituisce l'elemento menù "Right trim"
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getRightTrimMenuCheck() {
		if (rightTrimMenuCheck == null) {
			rightTrimMenuCheck = new JCheckBoxMenuItem();
			rightTrimMenuCheck.setText(messages.getString("MainMenu.RTRIM"));
		}
		return rightTrimMenuCheck;
	}

	/**
	 * Inizializza il menù rappresentato da questa classe
	 * 
	 */
	private void initialize() {
		this.add(getFileMenu());
		this.add(getEditMenu());
		setDefault();
	}

	/**
	 * Imposta il menù con i valori di default
	 * 
	 */

	public void setDefault() {
		leftTrimMenuCheck.setSelected(true);
		rightTrimMenuCheck.setSelected(true);
		collapseMenuCheck.setSelected(true);
		emptyNodeMenuCheck.setSelected(true);
		commentNodeMenuCheck.setSelected(true);
	}

}

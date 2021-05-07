/*****************************************************************************************
 *
 *   This file is part of jats-diff project.
 *
 *   jats-diff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   jats-diff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.exceptions;

/**
 * @author Mike Eccezione sollevata nel caso in cui ci sono problemi nel leggere
 *         l'input dell'algoritmo
 */
public class InputFileException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	String fileName;

	/**
	 * Costruttore
	 * 
	 * @param file
	 *            File sul quale si ha il problema
	 */
	public InputFileException(String file) {
		super("File di input " + file + " non valido!");
		fileName = file;
	}

	/**
	 * Ritorna il nome del file su cui si ha il problema
	 * 
	 * @return Nome del file su cui si ha il problema
	 */
	public String getFileName() {
		return fileName;
	}

}

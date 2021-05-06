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
package main.diff_L1_L2.ui;

import main.diff_L1_L2.debug.Debug;
import main.diff_L1_L2.exceptions.ParametersException;
import org.apache.log4j.xml.DOMConfigurator;
//import org.apache.log4j.Logger;

/**
 * 
 * Versione del main riscritta con il supporto getOpt e la gestione degli stream
 * 
 * @author Cesare Jacopo Corzani
 * 
 */
public class Main {

	//static Logger logger = Logger.getLogger(Main.class);

	/**
	 * @param args
	 *            Argomenti da riga di comando
	 * 
	 *            Main principale di jndiff, fa
	 *            il parsing dei parametri ed
	 *            esegue le operazioni richieste
	 */
	public static void main(String args[]) {

		DOMConfigurator.configure("log4j.xml");

		if (args.length == 0) {
			//System.err.print(ParametersHandler.getUsage());
			System.exit(1);
		}

		int exitCode = 0;

		Parameters params = null;
		try {
			params = ParametersHandler.getParameters(args);

			ParametersHandler.checkParameters(params);
		} catch (ParametersException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		// Initiate the debug
		Debug.start();
		//logger.info("Debug Status:" + Debug.flag);

		try {
			// TODO manage standard output

			OperationsHandler.doOperation(params);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			exitCode = 1;
		}

		Debug.close();

		//System.exit(exitCode);
	}

}
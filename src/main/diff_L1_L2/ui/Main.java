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
package main.diff_L1_L2.ui;

import main.diff_L1_L2.exceptions.ParametersException;
import java.net.URL;

/**
 * 
 * Versione del main riscritta con il supporto getOpt e la gestione degli stream
 * 
 * @author Cesare Jacopo Corzani
 * 
 */
public class Main {
	/**
	 * @param args
	 *            Argomenti da riga di comando
	 * 
	 *            Main principale di jats-diff, fa
	 *            il parsing dei parametri ed
	 *            esegue le operazioni richieste
	 */
	public void main(String args[]) {

		if (args.length == 0) {
			System.exit(1);
		}

		Parameters params = null;
		try {
			params = ParametersHandler.getParameters(args);

			ParametersHandler.checkParameters(params);
		} catch (ParametersException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		try {
			long start = System.nanoTime();
			OperationsHandler.doOperation(params);
			long elapsedTime = System.nanoTime() - start;
			System.out.println("Total time elapsed: " + elapsedTime);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}

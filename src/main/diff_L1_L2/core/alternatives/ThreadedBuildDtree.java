/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main.diff_L1_L2.core.alternatives;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadedBuildDtree {

	private class BuildDtreeThread extends Thread {

		private Dtree temp = null;

		private InputFileException e = null;

		private String fileXML;

		private boolean ltrim;

		private boolean rtrim;

		private boolean collapse;

		private boolean emptynode;

		private boolean commentNode;

		@SuppressWarnings("unused")
		private BuildDtreeThread() {
		}

		public BuildDtreeThread(String fileXML, boolean ltrim, boolean rtrim,
				boolean collapse, boolean emptynode, boolean commentNode) {
			this.fileXML = fileXML;
			this.ltrim = ltrim;
			this.rtrim = rtrim;
			this.collapse = collapse;
			this.emptynode = emptynode;
			this.commentNode = commentNode;

		}

		public InputFileException getE() {
			return e;
		}

		public Dtree getTree() {
			return temp;
		}

		@Override
		public void run() {

			try {

				// Costruzione delle struttura dati necessarie al confronto
				temp = new Dtree(fileXML, ltrim, rtrim, collapse, emptynode,
						commentNode);

			} catch (InputFileException ex) {
				Logger.getLogger(ThreadedBuildDtree.class.getName()).log(
						Level.SEVERE, null, ex);
				e = ex;
				temp = null;
			}

		}

	}

	public Dtree[] getDTree(String URIdocA, String URIdocB, boolean ltrim,
			boolean rtrim, boolean collapse, boolean emptynode,
			boolean commentNode) throws InputFileException {

		BuildDtreeThread dTree[] = new BuildDtreeThread[2];

		dTree[0] = new BuildDtreeThread(URIdocA, ltrim, rtrim, collapse,
				emptynode, commentNode);
		dTree[1] = new BuildDtreeThread(URIdocB, ltrim, rtrim, collapse,
				emptynode, commentNode);

		for (int i = 0; i < dTree.length; i++)
			dTree[i].run();

		for (int i = 0; i < dTree.length; i++) {
			try {
				dTree[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		for (int i = 0; i < dTree.length; i++) {

			if (dTree[i].getTree() == null) {
				throw dTree[i].getE();
			}

		}

		Dtree[] temp = { dTree[0].getTree(), dTree[1].getTree() };
		return temp;

	}
}

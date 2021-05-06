package main.diff_L1_L2.debug;

import main.diff_L1_L2.metadelta.METAdelta;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HtmlPrintMetaDelta {
	private PrintWriter out;

	public void print(METAdelta mDelta, String file) {

		//System.out.println("Debug: printNxN su " + file);

		try {
			FileWriter f = new FileWriter(file);
			out = new PrintWriter(f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		int k = 0;

		out.println("<ul>");
		for (int i = 0; i < mDelta.deleteOps.size(); i++) {
			out.println("<li>(" + k + ")" + mDelta.deleteOps.get(i).show());
			k++;
		}

		for (int i = 0; i < mDelta.insertOps.size(); i++) {
			out.println("<li>(" + k + ")" + mDelta.insertOps.get(i).show());
			k++;
		}

		for (int i = 0; i < mDelta.updateOps.size(); i++) {
			out.println("<li>(" + k + ")" + mDelta.updateOps.get(i).show());
			k++;
		}

		out.println("</ul>");

		out.close();

	}
}

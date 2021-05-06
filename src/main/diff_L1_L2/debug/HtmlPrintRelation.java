package main.diff_L1_L2.debug;

import main.diff_L1_L2.relation.Fragment;
import main.diff_L1_L2.relation.Relation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class HtmlPrintRelation {

	private PrintWriter out;
	public Relation R;

	public void print(Relation R, String file) {
		//System.out.println("Debug: printNxN su " + file);
		this.R = R;

		try {
			FileWriter f = new FileWriter(file);
			out = new PrintWriter(f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		out.println("<table border=\"0\" cellspacing=\"10\" cellpadding=\"12\">");
		out.println("<tr>");

		out.println("<td valign=\"top\">");
		printTable("EQUAL", R.getFragments(Relation.EQUAL));
		out.println("</td><td valign=\"top\">");
		printTable("MOVE", R.getFragments(Relation.MOVE));
		out.println("</td><td valign=\"top\">");
		printTable("UPDATE", R.getFragments(Relation.UPDATE));
		out.println("</td>");

		out.println("</tr></table>");

		out.close();

	}

	public void printTable(String relationType, Vector<Fragment> fragList) {
		out.println("<table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">");
		out.println("<tr bgcolor='blue'>");
		out.println("<td colspan=\"3\" align=\"center\"><h3>" + relationType
				+ "</h3></td></tr>");
		out.println("<tr><td>X</td><td>Y</td><td>Size</td></tr>");
		Fragment tmp;
		if (fragList != null)
			for (int i = 0; i < fragList.size(); i++) {
				tmp = fragList.get(i);
				out.println("<tr bgcolor='yellow'><td>" + tmp.getA().show()
						+ "</td>" + "<td>" + tmp.getB().show() + "</td>"
						+ "<td>(" + tmp.getWeight() + ")</td></tr>");
			}
		out.println("</table>");
	}

}

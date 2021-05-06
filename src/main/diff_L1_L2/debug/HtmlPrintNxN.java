package main.diff_L1_L2.debug;

import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class HtmlPrintNxN {
	private PrintWriter out;
	NxN plane;

	public void print(NxN plane, String file, boolean ShowStrutture,
			boolean ShowList, boolean ShowPlane) {

		//System.out.println("Debug: printNxN su " + file);
		this.plane = plane;

		try {
			FileWriter f = new FileWriter(file);
			out = new PrintWriter(f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		if (ShowStrutture)
			showStrutture();
		if (ShowPlane)
			showHowPlane();
		if (ShowList) {
			showList(Field.NO);
			showList(Field.LOCALITY);
		}

		out.close();

	}

	public void showHowPlane() {

		int cp[][] = new int[plane.dom + 2][plane.cod + 2];

		Vector<Interval> X = plane.getIntervalsOnX();
		Vector<Interval> Y = plane.getIntervalsOnY();

		for (int i = 1; i < X.size(); i++) {
			for (int j = 1; j < Y.size(); j++) {

				Field tmpREL = plane.getField(i, j);

				for (int x = tmpREL.xRef.inf; x <= tmpREL.xRef.sup; x++)
					for (int y = tmpREL.yRef.inf; y <= tmpREL.yRef.sup; y++)
						cp[x][y] = tmpREL.property + 1;
			}

		}

		out.println("<table cellspacing='0' border='1' cellpadding='0'>");

		for (int i = plane.cod; i >= 0; i--) {

			out.println("<tr><td bgcolor='#543fff'>" + i + "</td>");

			for (int j = 0; j <= plane.dom; j++) {

				out.println("<td width=14 align='center'  bgcolor='"
						+ (cp[j][i]) * 999999 + "'>");

				out.println(cp[j][i]);

				out.println("</td>");
			}
			out.println("</tr>");
		}

		out.println("<tr><td>*</td>");
		for (int i = 0; i <= plane.dom; i++)
			out.println("<td bgcolor='#543fff'>" + i + "</td>");
		out.println("</tr>");

		out.println("</table>");

	}

	public void showList(int property) {
		int puntRiga = 1;
		int contaElementi = 0;

		out.println("<h3> Show List property: " + property + "</h3>");

		out.println("<table><tr>");

		Field tmp;
		plane.StartFieldProcess(property);
		while ((tmp = plane.nextField()) != null) {
			out.println("<td align='center' bgcolor='yellow'>-" + contaElementi
					+ "- <br>" + tmp.xRef.show() + "<br>" + tmp.yRef.show()
					+ "</td><td>--></td>");
			contaElementi++;

			if (puntRiga == 19) {
				puntRiga = 0;
				out.println("</tr><tr>");
			} else
				puntRiga++;
		}
		out.println("</tr></table>");
	}

	public void showStrutture() {
		out.println("<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\"><tr>");

		// System.out.println(plane.X.size());
		Vector<Interval> X = plane.getIntervalsOnX();
		Vector<Interval> Y = plane.getIntervalsOnY();

		for (int j = Y.size() - 1; j >= 0; j--) {
			out.println("<tr><td>" + j + "</td><td bgcolor='yellow'>"
					+ (Y.get(j)).show() + "</td>");
			for (int i = 0; i < X.size(); i++) {
				out.println("<td align='center' bgcolor='"
						+ (plane.getField(i, j).property + 3) * 991394 + "'>"
						+ plane.getField(i, j).property + "</td>");
			}
			out.println("</tr>");
		}

		// stampa dell'ultima riga
		out.println("<tr><td colspan='2' bgcolor='black'>  </td>");
		for (int i = 0; i < X.size(); i++)
			out.println("<td bgcolor='yellow'>" + (X.get(i)).show() + "</td>");
		out.println("</tr>");

		out.println("<tr><td colspan='2' bgcolor='black'>  </td>");
		for (int i = 0; i < X.size(); i++)
			out.println("<td align='center'>" + i + "</td>");
		out.println("</tr>");

		out.println("</table>");

		// out.println(plane.getField(1, 9).show());
	}

}

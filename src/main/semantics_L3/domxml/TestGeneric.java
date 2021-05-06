package main.semantics_L3.domxml;

import main.diff_L1_L2.exceptions.InputFileException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TestGeneric {
	public TestGeneric() throws InputFileException, IOException {
	}

	public void generic(String path,String pathCompare) throws InputFileException, IOException {
		BufferedReader reader1 = new BufferedReader(new FileReader(path + "generic.txt"));

		BufferedReader reader2 = new BufferedReader(new FileReader(path + "semantics.txt"));

		String line1 = reader1.readLine();

		String line2 = reader2.readLine();

		boolean areEqual = true;

		while (line1 != null || line2 != null) {
			if (line1 == null || line2 == null) {
				areEqual = false;

				break;
			} else if (!line1.equalsIgnoreCase(line2)) {
				areEqual = false;
				break;
			}

			line1 = reader1.readLine();

			line2 = reader2.readLine();
		}

		if (!areEqual) {
			FileWriter myWriter = new FileWriter(pathCompare+"/compare.txt",true);
			myWriter.write("\n"+path);
			myWriter.close();
		}
		reader1.close();
		reader2.close();
	}
}

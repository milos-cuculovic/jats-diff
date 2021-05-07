package main.semantics_L3;

import info.debatty.java.stringsimilarity.Jaccard;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Similarity {

	public ArrayList<String> score(String orig, String modif, boolean jaccard, boolean simitext, boolean simtextW) {
		ArrayList<String> scores = new ArrayList<String>();
		if (jaccard) {
			Jaccard distance = new Jaccard();
			double jacNum = distance.distance(orig, modif);
			double pourcentage = (double) ((1 - jacNum) * 100);
			pourcentage = (double) Math.round(pourcentage * 10) / 10;
			scores.add(Double.toString(Math.abs(pourcentage)) + " %");
		} else {
			scores.add(null);
		}
		if (simitext) {
			double similar = Math.abs(similarText(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			scores.add(Double.toString(similar) + " %");
		} else {
			scores.add(null);
		}
		if (simtextW) {
			double similar = Math.abs(similarTextword(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			scores.add(Double.toString(similar) + " %");

		} else {
			scores.add(null);

		}
		return scores;
	}

	public int similarword(String first, String second) {
		int p, q, l, sum;
		int pos1 = 0;
		int pos2 = 0;
		int max = 0;
		ArrayList<String> arr1 = listWords(first);
		ArrayList<String> arr2 = listWords(second);
		int firstLength = arr1.size();
		int secondLength = arr2.size();
		for (p = 0; p < firstLength; p++) {
			for (q = 0; q < secondLength; q++) {
				for (l = 0; (p + l < firstLength) && (q + l < secondLength)
						&& ((arr1.get(p + l)).equals(arr2.get(q + l))); l++)
					;
				if (l > max) {
					max = l;
					pos1 = p;
					pos2 = q;
				}

			}
		}
		sum = max;
		if (sum > 0) {
			if (pos1 > 0 && pos2 > 0) {
				String first1 = new String();
				String second1 = new String();
				if (pos1 > firstLength) {
					for (int i = 0; i < firstLength; i++) {
						first1 += arr1.get(i) + " ";
					}
				} else {
					for (int i = 0; i < pos1; i++) {
						first1 += arr1.get(i) + " ";
					}
				}
				if (pos2 > secondLength) {
					for (int i = 0; i < secondLength; i++) {
						second1 += arr2.get(i) + " ";
					}
				} else {
					for (int i = 0; i < pos2; i++) {
						second1 += arr2.get(i) + " ";
					}
				}
				sum += this.similarword(first1, second1);
			}

			if ((pos1 + max < firstLength) && (pos2 + max < secondLength)) {
				String first1 = new String();
				String second1 = new String();
				for (int i = pos1 + max; i < firstLength; i++) {
					first1 += arr1.get(i) + " ";
				}
				for (int i = pos2 + max; i < secondLength; i++) {
					second1 += arr2.get(i) + " ";
				}
				sum += this.similarword(first1, second1);
			}
		}
		return sum;
	}

	public int similarword1(ArrayList<String> arr1, ArrayList<String> arr2) {
		int p, q, l, sum;
		int pos1 = 0;
		int pos2 = 0;
		int max = 0;
		int firstLength = arr1.size();
		int secondLength = arr2.size();
		for (p = 0; p < firstLength; p++) {
			for (q = 0; q < secondLength; q++) {
				for (l = 0; (p + l < firstLength) && (q + l < secondLength)
						&& ((arr1.get(p + l)).equals(arr2.get(q + l))); l++)
					;
				if (l > max) {
					max = l;
					pos1 = p;
					pos2 = q;
				}

			}
		}
		sum = max;
		if (sum > 0) {
			if (pos1 > 0 && pos2 > 0) {
				String first1 = new String();
				String second1 = new String();
				if (pos1 > firstLength) {
					for (int i = 0; i < firstLength; i++) {
						first1 += arr1.get(i) + " ";
					}
				} else {
					for (int i = 0; i < pos1; i++) {
						first1 += arr1.get(i) + " ";
					}
				}
				if (pos2 > secondLength) {
					for (int i = 0; i < secondLength; i++) {
						second1 += arr2.get(i) + " ";
					}
				} else {
					for (int i = 0; i < pos2; i++) {
						second1 += arr2.get(i) + " ";
					}
				}
				sum += this.similarword(first1, second1);
			}

			if ((pos1 + max < firstLength) && (pos2 + max < secondLength)) {
				String first1 = new String();
				String second1 = new String();
				for (int i = pos1 + max; i < firstLength; i++) {
					first1 += arr1.get(i) + " ";
				}
				for (int i = pos2 + max; i < secondLength; i++) {
					second1 += arr2.get(i) + " ";
				}
				sum += this.similarword(first1, second1);
			}
		}
		return sum;
	}

	public float similarTextword(String first, String second) {
		int lenght1 = countWords(first);
		int lenght2 = countWords(second);
		return (float) (this.similarword(first, second) * 200) / (lenght1 + lenght2);
	}

	public float similarTextword1(ArrayList<String> arr1, ArrayList<String> arr2) {
		int lenght1 = arr1.size();
		int lenght2 = arr2.size();
		return (float) (this.similarword1(arr1, arr2) * 200) / (lenght1 + lenght2);
	}

	public float similarText(String first, String second) {
		return (float) (this.similar(first, second) * 200) / (first.length() + second.length());
	}

	public int similar(String first, String second) {
		int p, q, l, sum;
		int pos1 = 0;
		int pos2 = 0;
		int max = 0;
		char[] arr1 = first.toCharArray();
		char[] arr2 = second.toCharArray();
		int firstLength = arr1.length;
		int secondLength = arr2.length;
		for (p = 0; p < firstLength; p++) {
			for (q = 0; q < secondLength; q++) {
				for (l = 0; (p + l < firstLength) && (q + l < secondLength) && (arr1[p + l] == arr2[q + l]); l++)
					;
				if (l > max) {
					max = l;
					pos1 = p;
					pos2 = q;
				}

			}
		}
		sum = max;
		if (sum > 0) {
			if (pos1 > 0 && pos2 > 0) {
				sum += this.similar(first.substring(0, pos1 > firstLength ? firstLength : pos1),
						second.substring(0, pos2 > secondLength ? secondLength : pos2));
			}

			if ((pos1 + max < firstLength) && (pos2 + max < secondLength)) {
				sum += this.similar(first.substring(pos1 + max, firstLength),
						second.substring(pos2 + max, secondLength));
			}
		}
		return sum;
	}

	public int countWords(String sentence) {
		if (sentence == null || sentence.isEmpty()) {
			return 0;
		}
		StringTokenizer tokens = new StringTokenizer(sentence);
		return tokens.countTokens();
	}

	public ArrayList<String> listWords(String sentence) {
		ArrayList<String> list = new ArrayList<String>();
		if (sentence == null || sentence.isEmpty()) {
			return list;
		}
		StringTokenizer tokens = new StringTokenizer(sentence);
		while (tokens.hasMoreTokens()) {
			list.add(tokens.nextToken());
		}

		return list;
	}

}

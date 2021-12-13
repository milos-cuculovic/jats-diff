package main.semantics_L3;

import com.topic.model.BTM;
import info.debatty.java.stringsimilarity.Jaccard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import com.topic.utils.FileUtil;
import com.topic.model.GibbsSamplingLDA;

public class Similarity {
	private boolean jaccard;
	private boolean simitext;
	private boolean simtextW;
	private boolean topicModel;
	private boolean tf;

	public Similarity(boolean jaccard, boolean simitext, boolean simtextW, boolean topicModel,boolean tf) {
		this.jaccard = jaccard;
		this.simitext = simitext;
		this.simtextW = simtextW;
		this.topicModel=topicModel;
		this.tf=tf;
	}

	public Object score(String orig, String modif,Object change) throws IOException {
		if (jaccard) {
			Jaccard distance = new Jaccard();
			double jacNum = distance.distance(orig, modif);
			double pourcentage = (double) ((1 - jacNum) * 100);
			pourcentage = (double) Math.round(pourcentage * 10) / 10;
			if (change instanceof NodeChanged){
				((NodeChanged) change).setJaccard(pourcentage);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setJaccard(pourcentage);
			}
		} else {
			if (change instanceof NodeChanged){
				((NodeChanged) change).setJaccard(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setJaccard(null);
			}
		}
		if (simitext) {
			double similar = Math.abs(similarText(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			if (change instanceof NodeChanged){
				((NodeChanged) change).setSimilartext(similar);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setSimilartext(similar);
			}
		} else {
			if (change instanceof NodeChanged){
				((NodeChanged) change).setSimilartext(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setSimilartext(null);
			}
		}
		if(topicModel){
			if (change instanceof NodeChanged){
				((NodeChanged) change).setTopicModel(topicmodel(orig,modif));
			}
			else if (change instanceof TableChange){
				((TableChange) change).setTopicModel(topicmodel(orig,modif));
			}
		}
		else{
			if (change instanceof NodeChanged){
				((NodeChanged) change).setTopicModel(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setTopicModel(null);
			}
		}
		if (tf) {
			TFIDFCalculator tfidf = new TFIDFCalculator();
			List<String> doc1 = Arrays.asList(orig.split(" "));
			List<String> doc2 = Arrays.asList(modif.split(" "));
			Integer total_positive_tf = 0;

			for (String term : doc1) {
				double tf_value = tfidf.tf(doc2, term);
				if (tf_value > 0) {
					total_positive_tf++;
				}
				else {
					total_positive_tf--;
				}
			}

			if (total_positive_tf < 0) {
				if (change instanceof NodeChanged){
					((NodeChanged) change).setTf((double) 0);
				}
				else if (change instanceof TableChange){
					((TableChange) change).setTf((double) 0);
				}
			}
			else {
				double tf_score = (float)(((double)(total_positive_tf) / (double)(doc2.size())) * 100);
				if (change instanceof NodeChanged){
					((NodeChanged) change).setTf((double) Math.round(tf_score));
				}
				else if (change instanceof TableChange){
					((TableChange) change).setTf((double) Math.round(tf_score));
				}
			}
		}

		else{
			if (change instanceof NodeChanged){
				((NodeChanged) change).setTf(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setTf(null);
			}
		}

		if (simtextW) {
			double similar = Math.abs(similarTextword(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			if (change instanceof NodeChanged){
				((NodeChanged) change).setSimitextword(similar);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setSimitextword(similar);
			}

		} else {
			if (change instanceof NodeChanged){
				((NodeChanged) change).setSimitextword(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setSimitextword(null);
			}

		}
		if(topicModel){
			double topicM=Math.abs(topicmodel(orig,modif));
			topicM = (double) Math.round(topicM * 10) / 10;
			if (change instanceof NodeChanged){
				((NodeChanged) change).setTopicModel(topicM);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setTopicModel(topicM);
			}
		}
		else{
			if (change instanceof NodeChanged){
				((NodeChanged) change).setTopicModel(null);
			}
			else if (change instanceof TableChange){
				((TableChange) change).setTopicModel(null);
			}
		}
		return change;
	}

	private double topicmodel(String orig, String modif) throws IOException {
		//token etc
		ArrayList<String> wordsorig = new ArrayList<String>();
		FileUtil.getlema(orig, wordsorig);
		String textorig = FileUtil.RemoveNoiseWord(wordsorig);
		ArrayList<String> wordsmodif = new ArrayList<String>();
		FileUtil.getlema(modif, wordsmodif);
		String textmodif = FileUtil.RemoveNoiseWord(wordsmodif);
		ArrayList<String> keyModif=topicModelList(textmodif);
		ArrayList<String> keyOrig=topicModelList(textorig);

//		GibbsSamplingLDA lda = new GibbsSamplingLDA("examples\\rawdata_process_lda.txt", "gbk",  50, 0.1,
//				0.01, 500, 50, "examples\\");
//		lda.MCMCSampling();
		TFIDFCalculator tfidf = new TFIDFCalculator();
		Integer total_positive_tf = 0;
		for (String term : keyModif) {
			double tf_value = tfidf.tf(keyOrig, term);
			if (tf_value > 0) {
				total_positive_tf++;
			}
			else {
				total_positive_tf--;
			}
		}
		if (total_positive_tf < 0) {
			return 0;

		}
		else {
			double tf_score = (float)(((double)(total_positive_tf) / (double)(keyOrig.size())) * 100);
			return (double) Math.round(tf_score);

		}
	}
	public ArrayList<String> topicModelList(String text) throws IOException {
		FileWriter writer = new FileWriter("examples\\rawdata_process_lda.txt");
		writer.write(text + System.lineSeparator());
		writer.close();
		BTM btm = new BTM("examples\\rawdata_process_lda.txt", "gbk", 15, 0.1,
				0.01, 1000, 30, 50, "examples\\");
		btm.MCMCSampling();
		BufferedReader reader;
		ArrayList<String> keyWord=new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(
					"examples\\topic_word_BTM_15.txt"));
			String line = reader.readLine();
			boolean topic15=false;
			while (line != null) {
				if(topic15){
					keyWord.add(line.split(" ")[0]);
				}
				if(line=="Topic:15"){
					topic15=true;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Files.delete(Path.of("examples\\doc_topic_BTM_15.txt"));
		Files.delete(Path.of("examples\\rawdata_process_lda.txt"));
		Files.delete(Path.of("examples\\topic_theta_BTM15.txt"));
		Files.delete(Path.of("examples\\topic_word_BTM_15.txt"));
		Files.delete(Path.of("examples\\topic_wordnop_BTM_15.txt"));
		return keyWord;
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

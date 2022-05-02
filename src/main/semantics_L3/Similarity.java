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
		ObjectChange<Object> change1=new ObjectChange<>();
		change1.add(change);
		change1.add(change);
		if (jaccard) {
			Jaccard distance = new Jaccard();
			double jacNum = distance.distance(orig, modif);
			double pourcentage = (double) ((1 - jacNum) * 100);
			pourcentage = (double) Math.round(pourcentage * 10) / 10;
			change1.setJaccard(pourcentage);
		} else {
			change1.setJaccard(null);
		}
		if (simitext) {
			double similar = Math.abs(similarText(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			change1.setSimtext(similar);

		} else {
			change1.setSimtext(null);
		}
		if(topicModel){
			change1.setTopicModel(topicmodel(orig,modif));
		}
		else{
			change1.setTopicModel(null);

		}
		if (tf) {
			change1.setTF(tf(orig,modif));
		}

		else{
			change1.setTF(null);

		}

		if (simtextW) {
			double similar = Math.abs(similarTextword(orig, modif));
			similar = (double) Math.round(similar * 10) / 10;
			change1.setSimtextWord(similar);

		} else {
			change1.setSimtextWord(null);

		}
		return change;
	}
	public double tf(String orig,String modif)
	{
		{
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
				return (double)0;

			}
			else {
				double tf_score = (float)(((double)(total_positive_tf) / (double)(doc2.size())) * 100);
				return(double) Math.round(tf_score);

			}
		}
	}
	public double topicmodel(String orig, String modif) throws IOException {
		//token etc
		ArrayList<String> wordsorig = new ArrayList<String>();
		orig=sepWord(orig);
		modif=sepWord(modif);
		FileUtil.getlema(orig, wordsorig);
		String textorig = FileUtil.RemoveNoiseWord(wordsorig);
		ArrayList<String> wordsmodif = new ArrayList<String>();
		FileUtil.getlema(modif, wordsmodif);
		String textmodif = FileUtil.RemoveNoiseWord(wordsmodif);
		ArrayList<String> keyModif=topicModelList(textmodif);
		ArrayList<String> keyOrig=topicModelList(textorig);

		TFIDFCalculator tfidf = new TFIDFCalculator();
		Integer total_positive_tf = 0;
		double score=0;
		double pourcentage = 0;
		for (String term : keyModif) {
			for(String oriTerm : keyOrig){
				if(term.equals(oriTerm)){
					score++;
					break;
				}
			}}
			pourcentage=score/(double)Math.max(keyModif.size(),keyOrig.size())*100;
//			double tf_value = tfidf.tf(keyOrig, term);
//			if (tf_value > 0) {
//				total_positive_tf++;
//			}
//			else {
//				total_positive_tf--;
//			}

		return pourcentage;
	/*	if (total_positive_tf < 0) {
			System.out.println(total_positive_tf);
			return 0;

		}
		else {
			double tf_score = (float)(((double)(total_positive_tf) / (double)(keyOrig.size())) * 100);
			System.out.println(total_positive_tf);
			System.out.println(keyOrig.size());
			System.out.println(tf_score);
			return (double) Math.round(tf_score);

		}*/
	}
	public String sepWord(String origormodif){
		String sentence="";
		for (String element : origormodif.split(" ")) {
			if(!isStringUpperCase(element)){
				String[] listword = element.split("");
				element=listword[0];
				for (int i=1;i<listword.length;i++){
					if(isStringUpperCase(listword[i])){
						element+=" ";
					}
					element+=listword[i];
				}
			}
			sentence+=element+" ";
	}
		return sentence;
	}
	public ArrayList<String> topicModelList(String text) throws IOException {
		FileWriter writer = new FileWriter("examples\\rawdata_process_lda.txt");
		String[] xmlword={"article","xref", "type", "ref", "rid","aff", "italy"};
		for (String rep:xmlword){
			text=text.replaceAll(rep,"");
		}
		writer.write(text + System.lineSeparator());
		writer.close();
		BTM btm = new BTM("examples\\rawdata_process_lda.txt", "gbk", 15, 0.1,
				0.01, 1000, 50, 50, "examples\\");
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
					if (!line.equals("")){
					keyWord.add(line.split(" ")[0]);}
					else{
						break;
					}
				}
				if(line.contains("Topic:15")){
					topic15=true;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*Files.delete(Path.of("examples\\doc_topic_BTM_15.txt"));
		Files.delete(Path.of("examples\\rawdata_process_lda.txt"));
		Files.delete(Path.of("examples\\topic_theta_BTM15.txt"));
		Files.delete(Path.of("examples\\topic_word_BTM_15.txt"));
		Files.delete(Path.of("examples\\topic_wordnop_BTM_15.txt"));*/
		ArrayList<String> key=new ArrayList<String>();
		for (String element : keyWord) {

			element=element.replaceAll("-","");
			element=element.replaceAll("article","");
			element=element.replaceAll("[0-9]", "");
			element = element.replaceAll("[^a-zA-Z0-9]", "");

			// If this element is not present in newList
			// then add it
			for (String word : element.split("\\s+")) {
				if (!key.contains(element)) {
					key.add(element);
				}
			}
		}
		return key;
	}
	private static boolean isStringUpperCase(String str){

		//convert String to char array
		char[] charArray = str.toCharArray();

		for(int i=0; i < charArray.length; i++){

			//if any character is not in upper case, return false
			if( !Character.isUpperCase( charArray[i] ))
				return false;
		}

		return true;
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

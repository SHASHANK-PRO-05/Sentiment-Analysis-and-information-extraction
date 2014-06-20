package classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.tree.ExpandVetoException;

public class LogisticRegression {
	public Map<String, Double> myMap = new HashMap<String, Double>();

	public LogisticRegression() {
		try {
			BufferedReader br1 = new BufferedReader(
					new FileReader(
							"E:/projects/sentiment_twitter/classifiers/classifiers/imdb/lr/Vocab.txt"));
			BufferedReader br2 = new BufferedReader(
					new FileReader(
							"E:/projects/sentiment_twitter/classifiers/classifiers/imdb/lr/Theata.txt"));
			String s = br1.readLine();
			while (s != null) {
				String k = br2.readLine();
				Double val = Double.parseDouble(k);
				myMap.put(s, val);
				s = br1.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int classify(String args) throws Exception {
		Set set = myMap.keySet();
		Iterator it;

		String scan = args;
		String[] tempo = scan.replace("!", " ! ").replace("?", " ? ")
				.replace(".", " ").replace(" '", " ").replace("' ", " ")
				.replace("<br />", " ").replace(")", " ").replace("(", " ")
				.replace("@", " ").replace("#", " ").replace("$", " ")
				.replace("^", " ").replace("%", " ").replace("&", " ")
				.replace("*", " ").replace("\"", " ").replace(",", " ")
				.replace(":", " ").replace(" - ", " ").replace("- ", " ")
				.replace("/", " ").replace("", " ").replace(" -", " ")
				.replace(":", " ").replace(";", " ").replace("}", " ")
				.replace("-", " ").replace("`", " ").replace("~", " ")
				.replace("_", " ").replace("+", " ").replace("=", " ")
				.replace("[", " ").replace("]", " ").replace("{", " ")
				.replace("}", " ").replace("\\", " ").replace("|", " ")
				.replace("<", " ").replace(">", " ").replaceAll("\\s{2,}", " ")
				.replaceAll(" '", " ").replaceAll("' ", " ").toLowerCase()
				.split(" ");
		int length = tempo.length;
		Double funcz = 0D;
		for (int i = 0; i < length; i++) {
			if (myMap.containsKey(tempo[i]))
				funcz += myMap.get(tempo[i]);
		}
		// sigmaoid function
		Double htheta = 1 / (1 + Math.exp(-1 * funcz));
		if (htheta > 0.5D) {
			System.out.println("lr positive " + args);
			return (1);
		} else {
			System.out.println("lr negative " + args);
			return (-1);
		}

	}

}
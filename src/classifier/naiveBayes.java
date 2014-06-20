package classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class naiveBayes {
	public Map<String, Double[]> myMap = new HashMap<String, Double[]>();

	public naiveBayes() {
		try {

			BufferedReader br = new BufferedReader(
					new FileReader(
							"E:/projects/sentiment_twitter/classifiers/classifiers/imdb/naive/naivebayesclassifier.txt"));
			String scan = br.readLine();
			while (scan != null) {
				String[] temp = scan.split(" ");
				Double l[] = new Double[2];
				l[0] = Double.parseDouble(temp[1]);
				l[1] = Double.parseDouble(temp[2]);
				myMap.put(temp[0], l);
				scan = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int classify(String args) throws Exception {
		Double total = 42927D;
		Double pos = 21466D / total;
		Double neg = 21461D / total;
		Double ratio = pos / neg;

		String scan = args;
		String[] temp = scan.replace("!", " ! ").replace("?", " ? ")
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
		int length = temp.length;
		Double prob1 = pos;
		Double prob2 = neg;
		Double prob = 0D;
		for (int i = 0; i < length; i++) {
			if (myMap.containsKey(temp[i])) {
				Double d = myMap.get(temp[i])[0] / myMap.get(temp[i])[1];
				prob1 += myMap.get(temp[i])[0];
				prob2 += myMap.get(temp[i])[1];
			}
		}
		prob = prob1 / prob2;
		if (prob > 1) {
			// pos
			return (1);
		} else if (prob < 1) {
			// neg
			return (-1);
		} else {
			// words are not found in our dictionary
			return (0);
		}

	}

}

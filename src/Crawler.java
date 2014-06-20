import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import twitter4j.TwitterException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Crawler {
	public String name = null;
	public String id = null;
	public String img = null;
	public String url = null;
	public List<String> cast = new ArrayList<String>();
	public List<String> collect = new ArrayList<String>();
	public List<String> subcollect = new ArrayList<String>();
	public List<String> results = new ArrayList<String>();
	public List<String> movies = new ArrayList<String>();
	public List<String> actors = new ArrayList<String>();
	public List<String> actresses = new ArrayList<String>();
	public List<String> directors = new ArrayList<String>();
	public Crawler crawl;
	public boolean isCastPresent = false;
	public boolean isPresent = false;


	public void main(String args[]) {
		// getCast();
		// sentenceSplitter sp = new sentenceSplitter(id, url);
		/* movies */
		// sp.movieAnalyser();

		/* review */
		// sp.reviewAnalyser();

		/* twitter */
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(System.in));
		// twitterMovieAnalyser tw = new twitterMovieAnalyser();
		// try {
		// System.out.println(tw.get(br.readLine()) + "");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public String getId() {
		return crawl.id;
	}

	public String getUrl() {
		return crawl.url;
	}

	public String getImg() {
		return crawl.img;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public JSONObject getCast() {
		JSONObject castObject = new JSONObject();

		try {

			Crawler crawl = new Crawler();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			name = br.readLine();

			List<String> myMap = new ArrayList<String>();
			String url = crawl.generateURL(name);

			crawl.retreiveReviewPage(url);

			crawl.process(name);

			myMap = crawl.partOfSpeechTag();

			FileOutputStream fout;
			PrintWriter pr = null;
			if (!isCastPresent) {
				myMap.add(name.replace(" ", "_"));
				fout = new FileOutputStream("D:/project/imdbdata/reviews/cast/"
						+ id + ".txt");
				pr = new PrintWriter(fout);
				for (String it : myMap) {
					String nouns = "," + (String) it.replace(" ", "_") + ",";
					String bracket = "(";
					bracket = crawl.tagActor(nouns, bracket);
					bracket = crawl.tagMovie(nouns, bracket);
					bracket = crawl.tagActress(nouns, bracket);
					bracket = crawl.tagDirector(nouns, bracket);
					bracket += ")";
					if (!bracket.equals("()")) {
						if (!results.contains(nouns + " " + bracket))
							results.add(nouns + " " + bracket);
						if (bracket.contains("Movie"))
							movies.add(nouns.replaceAll(",", ""));
						else {
							if (bracket.contains("Actor"))
								actors.add(nouns.replaceAll(",", ""));
							if (bracket.contains("Actress"))
								actresses.add(nouns.replaceAll(",", ""));
							if (bracket.contains("Director"))
								directors.add(nouns.replaceAll(",", ""));
						}
					}
				}
				int i;
				for (i = 0; i < cast.size(); i++) {
					if (!movies.contains(cast.get(i)))
						pr.print(cast.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < movies.size(); i++) {
					pr.print(movies.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < actors.size(); i++) {
					pr.print(actors.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < actresses.size(); i++) {
					pr.print(actresses.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < directors.size(); i++) {
					pr.print(directors.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < results.size(); i++) {
					System.out.println(results.get(i));
					pr.println(results.get(i));
				}

				pr.close();

			} else {
				br = new BufferedReader(new FileReader(
						"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
				String str;
				String arr[];
				arr = br.readLine().split(" ");
				for (String names : arr) {
					cast.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					movies.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					actors.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					actresses.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					directors.add(names);
				}

				while ((str = br.readLine()) != null) {
					if (!results.contains(str)) {
						System.out.println(str);
						results.add(str);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		castObject.put("movies", movies);
		castObject.put("actors", actors);
		castObject.put("actresses", actresses);
		castObject.put("directors", directors);
		castObject.put("cast", cast);
		castObject.put("result", results);
		return castObject;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public JSONObject getCast(String n) {
		JSONObject castObject = new JSONObject();
		crawl = new Crawler();

		try {

			BufferedReader br;
			name = n;
			List<String> myMap = new ArrayList<String>();
			String url = crawl.generateURL(name);

			crawl.retreiveReviewPage(url);

			crawl.process(name);

			myMap = crawl.partOfSpeechTag();

			FileOutputStream fout;
			PrintWriter pr = null;
			if (!crawl.isCastPresent) {
				myMap.add(name.replace(" ", "_"));
				fout = new FileOutputStream("D:/project/imdbdata/reviews/cast/"
						+ crawl.id + ".txt");
				pr = new PrintWriter(fout);
				for (String it : myMap) {
					String nouns = "," + (String) it.replace(" ", "_") + ",";
					String bracket = "(";
					bracket = crawl.tagActor(nouns, bracket);
					bracket = crawl.tagMovie(nouns, bracket);
					bracket = crawl.tagActress(nouns, bracket);
					bracket = crawl.tagDirector(nouns, bracket);
					bracket += ")";
					if (!bracket.equals("()")) {
						if (!results.contains(nouns + " " + bracket)) {
							results.add(nouns + " " + bracket);
							if (bracket.contains("Actor")) {
								if (!cast.contains(nouns.replaceAll(",", "")))
									cast.add(nouns.replaceAll(",", ""));
								actors.add(nouns.replaceAll(",", ""));
							}
							if (bracket.contains("Actress")) {
								if (!cast.contains(nouns.replaceAll(",", "")))
									cast.add(nouns.replaceAll(",", ""));
								actresses.add(nouns.replaceAll(",", ""));
							}
							if (bracket.contains("Director")) {
								if (!cast.contains(nouns.replaceAll(",", "")))
									cast.add(nouns.replaceAll(",", ""));
								directors.add(nouns.replaceAll(",", ""));
							}
							if (bracket.contains("Movie"))
								movies.add(nouns.replaceAll(",", ""));

						}
					}

				}
				int i;
				for (i = 0; i < cast.size(); i++) {
					pr.print(cast.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < movies.size(); i++) {
					pr.print(movies.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < actors.size(); i++) {
					pr.print(actors.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < actresses.size(); i++) {
					pr.print(actresses.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < directors.size(); i++) {
					pr.print(directors.get(i) + " ");
				}
				pr.println();

				for (i = 0; i < results.size(); i++) {
					pr.println(results.get(i));
				}

				pr.close();

			} else {
				br = new BufferedReader(
						new FileReader("D:/project/imdbdata/reviews/cast/"
								+ crawl.id + ".txt"));
				String str;
				String arr[];
				arr = br.readLine().split(" ");
				for (String names : arr) {
					cast.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					movies.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					actors.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					actresses.add(names);
				}
				arr = br.readLine().split(" ");
				for (String names : arr) {
					directors.add(names);
				}

				while ((str = br.readLine()) != null) {
					if (!results.contains(str))
						results.add(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		castObject.put("movies", movies);
		castObject.put("actors", actors);
		castObject.put("actresses", actresses);
		castObject.put("directors", directors);
		castObject.put("cast", cast);
		castObject.put("result", results);

		return castObject;

	}

	public String tagActor(String Input, String Tag) throws Exception {
		BufferedReader tagEntityActor = new BufferedReader(new FileReader(
				"D:/project/imdbdata/extras/IMDBActors.txt"));
		String actor = tagEntityActor.readLine();
		if (actor.contains(Input)) {
			Tag += "/Actor/";
			if (!cast.contains(Input.replaceAll(",", "")))
				cast.add(Input.replaceAll(",", ""));
			if (!actors.contains(Input.replaceAll(",", "")))
				actors.add(Input.replaceAll(",", ""));
		}

		return Tag;
	}

	public String tagActress(String Input, String Tag) throws Exception {
		BufferedReader tagEntityActress = new BufferedReader(new FileReader(
				"D:/project/imdbdata/extras/IMDBActresses.txt"));
		String actress = tagEntityActress.readLine();
		if (actress.contains(Input)) {
			Tag += "/Actress/";
			if (!cast.contains(Input.replaceAll(",", "")))
				cast.add(Input.replaceAll(",", ""));
			if (!actresses.contains(Input.replaceAll(",", "")))
				actresses.add(Input.replaceAll(",", ""));
		}

		return Tag;
	}

	public String tagDirector(String Input, String Tag) throws Exception {
		BufferedReader tagEntityDirector = new BufferedReader(new FileReader(
				"D:/project/imdbdata/extras/IMDBDirectors.txt"));
		String director = tagEntityDirector.readLine();
		if (director.contains(Input)) {
			Tag += "/Director/";
			if (!cast.contains(Input.replaceAll(",", "")))
				cast.add(Input.replaceAll(",", ""));
			if (!directors.contains(Input.replaceAll(",", "")))
				directors.add(Input.replaceAll(",", ""));
		}
		return Tag;
	}

	public String tagMovie(String Input, String Tag) throws Exception {
		BufferedReader tagEntityMovie = new BufferedReader(new FileReader(
				"D:/project/imdbdata/extras/IMDBMovies.txt"));
		String movie = tagEntityMovie.readLine();
		if (movie.contains(Input)) {
			Tag += "/Movie/";
			if (!movies.contains(Input.replaceAll(",", "")))
				movies.add(Input.replaceAll(",", ""));
		}

		return Tag;
	}

	public List<String> partOfSpeechTag() throws Exception {
		File f = new File("D:/project/imdbdata/reviews/cast/" + id + ".txt");
		int count = 0;
		if (f.exists()) {
			isCastPresent = true;
			return null;
		} else {
			System.out.println("NF");
		}
		System.out
				.println(".....................Tagging Will Take Time....................");
		List<String> myMap = new ArrayList<String>();
		MaxentTagger tagger = new MaxentTagger(
				"D:/project/imdbdata/extras/bidirectional-distsim-wsj-0-18.tagger");
		for (String input : subcollect) {
			input = input.replace(". ", ".").replace("? ", "?");
			String temp[] = input.split("[.?]");
			int len = temp.length;
			for (int i = 0; i < len; i++) {
				String tagged = tagger.tagString(temp[i]);
				collect.add(tagged);
				if (tagged.contains("/NNP")) {
					String taggedbreaking[] = tagged.split(" ");
					int lentag = taggedbreaking.length;
					for (int j = 0; j < lentag; j++) {

						if (taggedbreaking[j].contains("/NNP")) {
							int b = 0;
							String Store = taggedbreaking[j].substring(0,
									taggedbreaking[j].length() - 4);
							while (j + 1 < lentag
									&& taggedbreaking[j + 1].contains("/NNP")) {
								Store += " "
										+ taggedbreaking[++j].substring(0,
												taggedbreaking[j].length() - 4);
								b++;
							}
							if (b > 0)
								myMap.add(Store.toLowerCase());
						}
					}

				}
			}
		}
		System.out.println("Tagging done");
		return myMap;
	}

	public void process(String MovieName) throws Exception {
		String replace = MovieName.replace(" ", "_");
		for (String input : collect) {
			input = input.replace(MovieName, replace);
			input = input.replace(MovieName.toUpperCase(), replace);
			subcollect.add(input);
		}
	}

	public String generateURL(String name) {
		try {
			System.out.println("Generating URL");
			name = name.replaceAll(" ", "%20");
			url = "http://www.omdbapi.com/?t=" + name;

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			int c = 0;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONParser jsonParser = new JSONParser();

			Object obj = jsonParser.parse(result.toString());

			JSONObject imdbMovieInfoJsonObject = (JSONObject) obj;
			id = (String) imdbMovieInfoJsonObject.get("imdbID");
			img = (String) imdbMovieInfoJsonObject.get("Poster");

			String imdbUrl = "http://www.imdb.com/title/" + id + "/reviews";
			System.out.println("Generated URL: " + imdbUrl);
			url = imdbUrl;
			return imdbUrl;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void retreiveReviewPage(String url) {
		File f = new File("D:/project/imdbdata/reviews/" + id + ".txt");
		int count = 0;
		if (f.exists()) {
			try {
				isPresent = true;
				BufferedReader br = new BufferedReader(new FileReader(f));
				String text;
				while ((text = br.readLine()) != null) {
					if (count > 20)
						return;
					if (!text.isEmpty()) {
						collect.add(text);
						count++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			Document webpageDocument = null;

			for (int start = 0; start < 1; start++) {
				// extracting reviews
				try {
					System.out.println("Getting page: " + (start + 1));
					int counter = 0;
					List<String> reviewList = new ArrayList<>();
					List<String> headingList = new ArrayList<>();

					String modifiedUrl = url + "?start=" + (start * 10);
					webpageDocument = Jsoup
							.connect(modifiedUrl)
							.timeout(100 * 1000)
							.userAgent(
									"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
							.get();

					org.jsoup.nodes.Element subDocument = webpageDocument
							.getElementById("tn15content");

					Elements review = subDocument.getElementsByTag("p");
					for (org.jsoup.nodes.Element para1 : review) {
						if (!(para1.text().equals("Add another review"))
								&& !(para1.text()
										.equals("*** This review may contain spoilers ***")))
							reviewList.add(counter++, para1.text());
					}
					counter = 0;

					// extracting headings of these reveiws
					Elements heading = subDocument.getElementsByTag("h2");
					for (org.jsoup.nodes.Element para1 : heading) {
						headingList.add(counter++, para1.text());
					}

					for (int i = 0; i < counter; i++) {
						collect.add(headingList.get(i));
						collect.add(reviewList.get(i));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Done");

		}
	}

}

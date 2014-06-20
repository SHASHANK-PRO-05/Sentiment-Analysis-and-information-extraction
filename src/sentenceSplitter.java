import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import classifier.naiveBayes;

public class sentenceSplitter {
	public List<String> cast = new ArrayList<String>();
	public List<String> review = new ArrayList<String>();
	public List<String> movies = new ArrayList<String>();
	public List<String> actors = new ArrayList<String>();
	public List<String> actresses = new ArrayList<String>();
	public List<String> directors = new ArrayList<String>();

	public ArrayList<Integer> finalLines = null;
	public String id, url;
	public boolean isPresent = false;
	public naiveBayes naive = new naiveBayes();

	public sentenceSplitter(String i, String u) {
		id = i;
		url = u;
		review = getReviews();
		cast = getCast();
		movies = getM();
		actors = getA();
		actresses = getV();
		directors = getD();
	}

	public String getImage(String name) {
		System.out.println("Searching images for: " + name);
		try {
			File f = new File("D:/project/imdbdata/reviews/images/"
					+ name.replaceAll(" ", "_") + ".txt");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				return br.readLine();
			}
			name = name.replaceAll(" ", "+");
			String url = "http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q="
					+ name;
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
			name = name.replaceAll("\\+", "_");
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(result.toString());
			JSONObject imdbMovieInfoJsonObject = (JSONObject) obj;
			String temp;
			try {
				temp = imdbMovieInfoJsonObject.get("name_popular").toString();
			} catch (Exception n) {
				temp = imdbMovieInfoJsonObject.get("name_exact").toString();
			}
			obj = jsonParser.parse(temp.substring(1, temp.length() - 1));
			imdbMovieInfoJsonObject = (JSONObject) obj;

			String modifiedUrl = "http://www.imdb.com/name/"
					+ imdbMovieInfoJsonObject.get("id") + "/?ref_=fn_al_nm_1";
			Document webpageDocument = null;
			webpageDocument = Jsoup
					.connect(modifiedUrl)
					.timeout(200 * 1000)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
					.get();
			org.jsoup.nodes.Element img = webpageDocument
					.getElementById("name-poster");

			Response resultImageResponse = (Response) Jsoup
					.connect(img.attr("src")).ignoreContentType(true).execute();
			FileOutputStream fout = new FileOutputStream(
					"D:/project/imdbdata/reviews/images/" + name + ".txt");
			PrintWriter pr = new PrintWriter(fout);
			pr.println(img.attr("src"));
			pr.flush();
			pr.close();
			return img.attr("src");
		} catch (Exception e) {
			String uk = "";
			try {
				uk = "http://forums.abs-cbn.com/Themes/Vertex-Theme2-0-2-v1-2/images/vertex_image/unknown.jpg";
				FileOutputStream fout = new FileOutputStream(
						"D:/project/imdbdata/reviews/images/"
								+ name.replaceAll("+", "_")
										.replaceAll(" ", "_") + ".txt");
				PrintWriter pr = new PrintWriter(fout);
				pr.println(uk);
				pr.flush();
				pr.close();
				return uk;
			} catch (Exception ee) {
				System.out.println("Image not found 404");
				return uk;
			}
		}

	}

	public JSONObject movieAnalyser(List<String> review) {

		int pos = 0, neg = 0, neu = 0;
		for (String rev : review) {
			try {
				switch (naive.classify(rev)) {
				case 1:
					pos++;
					break;
				case -1:
					neg++;
					break;
				case 0:
					neu++;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		float tp = (float) pos * 100 / ((float) pos + neg + neu);
		float tn = (float) neg * 100 / ((float) pos + neg + neu);
		System.out.print("Pos%=" + tp + "  Neg%=" + tn);
		JSONObject movieOpinion = new JSONObject();
		movieOpinion.put("positive", pos);
		movieOpinion.put("negative", neg);
		movieOpinion.put("neutral", neu);
		System.out.println("\t" + movieOpinion);

		return movieOpinion;

	}

	@SuppressWarnings("unchecked")
	public JSONObject reviewAnalyser(List<String> review, List<String> cast,
			List<String> actors, List<String> actresses, List<String> directors) {
		JSONObject revJsonObject = new JSONObject();

		try {

			int pos[] = new int[cast.size()], neg[] = new int[cast.size()], neu[] = new int[cast
					.size()];
			finalLines = new ArrayList<Integer>();
			for (String rev : review) {
				if (!rev.isEmpty()) {
					rev = rev.replace(". ", ".").replace("? ", "?");
					String lines[] = rev.split("[\n.?]");
					for (int i = 0; i < lines.length; i++) {
						for (int j = 0; j < cast.size(); j++) {

							if (lines[i].contains(cast.get(j))) {

								switch (naive.classify(lines[i])) {
								case 1:
									pos[j] = pos[j] + 1;
									break;
								case -1:
									neg[j] = neg[j] + 1;
									break;
								case 0:
									neu[j] = neu[j] + 1;
									break;
								}
							}
						}

					}
				}
			}
			for (int i = 0; i < cast.size(); i++) {
				int t = 0;
				JSONObject p = new JSONObject();
				p.put("positive", pos[i] + " ");
				JSONObject n = new JSONObject();
				n.put("negative", neg[i] + " ");
				JSONObject u = new JSONObject();
				u.put("neutral", neu[i] + " ");
				JSONObject r = new JSONObject();
				List<String> role = new ArrayList<String>();
				if (actors.contains(cast.get(i)))
					role.add("A");
				if (actresses.contains(cast.get(i)))
					role.add("V");
				if (directors.contains(cast.get(i)))
					role.add("D");
				r.put("Role", role);
				JSONObject js = new JSONObject();
				js.put("image", getImage(cast.get(i)).replaceAll(" ", "_"));
				JSONArray ja = new JSONArray();
				ja.add(p);
				ja.add(n);
				ja.add(u);
				ja.add(r);
				ja.add(js);
				float tp = (float) pos[i] * 100
						/ (float) (pos[i] + neg[i] + neu[i]);
				float tn = (float) neg[i] * 100
						/ (float) (pos[i] + neg[i] + neu[i]);
				revJsonObject.put(cast.get(i).replaceAll(" ", "_"), ja);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return revJsonObject;

	}

	public List<String> getCast() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
			String st = br.readLine();
			String[] words = st.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (!cast.contains(words[i]))
					cast.add(words[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < cast.size(); i++) {
			cast.set(i, cast.get(i).replaceAll("_", " "));
		}
		return cast;
	}

	public List<String> getM() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
			br.readLine();
			String st = br.readLine();
			String[] words = st.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (!movies.contains(words[i]))
					movies.add(words[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < movies.size(); i++) {
			movies.set(i, movies.get(i).replaceAll("_", " "));
		}
		return movies;
	}

	public List<String> getA() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
			br.readLine();
			br.readLine();
			String st = br.readLine();
			String[] words = st.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (!actors.contains(words[i]))
					actors.add(words[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < actors.size(); i++) {
			actors.set(i, actors.get(i).replaceAll("_", " "));
		}
		return actors;
	}

	public List<String> getV() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
			br.readLine();
			br.readLine();
			br.readLine();
			String st = br.readLine();
			String[] words = st.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (!actresses.contains(words[i]))
					actresses.add(words[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < actresses.size(); i++) {
			actresses.set(i, actresses.get(i).replaceAll("_", " "));
		}
		return actresses;
	}

	public List<String> getD() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"D:/project/imdbdata/reviews/cast/" + id + ".txt"));
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			String st = br.readLine();
			String[] words = st.split(" ");
			for (int i = 0; i < words.length; i++) {
				if (!directors.contains(words[i]))
					directors.add(words[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < directors.size(); i++) {
			directors.set(i, directors.get(i).replaceAll("_", " "));
		}
		return directors;
	}

	public List<String> getReviews() {
		try {
			File f = new File("D:/project/imdbdata/reviews/" + id + ".txt");
			int count = 0;
			if (f.exists()) {
				try {
					isPresent = true;
					BufferedReader br = new BufferedReader(new FileReader(f));
					String text;
					while ((text = br.readLine()) != null) {
						if (!text.isEmpty())
							review.add(text.toLowerCase());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Getting total number of review pages");
				FileOutputStream fout;
				PrintWriter pr = null;
				fout = new FileOutputStream("D:/project/imdbdata/reviews/" + id
						+ ".txt");
				pr = new PrintWriter(fout);
				Document webpageDocument = null;
				try {
					webpageDocument = Jsoup
							.connect(url)
							.timeout(10 * 1000)
							.userAgent(
									"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
							.get();

				} catch (Exception e) {
					e.printStackTrace();
				}

				// to get total pages...each page has 10 reviews
				org.jsoup.nodes.Element table = webpageDocument
						.getElementsByTag("table").get(1);
				Iterator<org.jsoup.nodes.Element> itr = table.select("td")
						.iterator();
				int totalPages = Integer
						.parseInt(itr.next().text().split(" ")[3].replace(":",
								""));
				System.out.println("Total pages are " + totalPages
						+ " ,how many do you want to analyse?");
				Scanner sc = new Scanner(new BufferedReader(
						new InputStreamReader(System.in)));
				int end = sc.nextInt();
				for (int start = 0; start < totalPages && start < end; start++) {
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

						Elements reviews = subDocument.getElementsByTag("p");
						for (org.jsoup.nodes.Element para1 : reviews) {
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
							review.add(headingList.get(i).toLowerCase());
							review.add(reviewList.get(i).toLowerCase());
							pr.println(headingList.get(i));
							pr.println(reviewList.get(i));
							pr.println();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				pr.close();
				System.out.println("Done :D");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return review;
	}
}
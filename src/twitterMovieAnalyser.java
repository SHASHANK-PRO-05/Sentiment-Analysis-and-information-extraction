import java.util.ArrayList;

import org.json.simple.JSONObject;

import classifier.naiveBayes;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class twitterMovieAnalyser {
	ArrayList<String> arr = new ArrayList<String>();
	naiveBayes naive = new naiveBayes();

	public void twitterMovieAnalyser() {
		String stopword = "a an and are as at be by for if in into is it of on or such that the their then there these they this to was will with";
		String[] words = stopword.split(" ");
		for (int ii = 0; ii < words.length; ii++) {
			arr.add(words[ii]);
		}

	}

	public JSONObject get(String name) {
		int neg = 0, pos = 0, neu = 0;
		String consumerKey = "O10yxCmM1dvK07xih1GFSBmiN";
		String consumerSecret = "qIP7j5y0ayLVg7XJZ82NDmpP9LCxuJbaO8G8yZjQq0fLKLOxyS";
		String accessToken = "168741775-XKx5RjFqDsNuhjlyGg9pL2rMdR4jGpMXM9NEm7ma";
		String accessTokenSecret = "i8sufS5kbfj3Ue2rvSLTe8uU1TPtnhp9Mi7Wk06X23MXR";
		try {
			TwitterFactory twitterFactory = new TwitterFactory();
			Twitter twitter = twitterFactory.getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			twitter.setOAuthAccessToken(new AccessToken(accessToken,
					accessTokenSecret));

			Query query = new Query(name);
			int numberOfTweets = 250;
			long lastID = Long.MAX_VALUE;
			ArrayList<Status> tweets = new ArrayList<Status>();
			while (tweets.size() < numberOfTweets) {
				if (numberOfTweets - tweets.size() > 100)
					query.setCount(1000);
				else
					query.setCount(numberOfTweets - tweets.size());
				try {
					QueryResult result = twitter.search(query);
					tweets.addAll(result.getTweets());
					for (Status t : tweets)
						if (t.getId() < lastID)
							lastID = t.getId();
				}

				catch (TwitterException te) {
					te.printStackTrace();
				}
				query.setMaxId(lastID - 1);
			}

			for (int i = 0; i < tweets.size(); i++) {
				Status t = (Status) tweets.get(i);
				String text = t.getText();

				if ((!t.getUser().getScreenName().contains(query.toString()))
						&& (!text.startsWith("RT"))) {
					text = text.replaceAll("&quot", " ")
							.replaceAll("http[://a-zA-Z0-9./]+", " ")
							.replaceAll("@[a-zA-Z0-9!@#$%^&*()+/*+_~`]+", " ")
							.replaceAll("[^a-z?!']+", " ")
							.replaceAll("[?]+", " ? ")
							.replaceAll("[!]+", " ! ").replaceAll("(' )", " ")
							.replaceAll("( ')", " ").replace("\n", "")
							.replaceAll("[\\s]+", " ").replaceAll("z{3,}", "z")
							.replaceAll("(omg){2,}", "omg")
							.replaceAll("(ha){3,}", "haha");
					for (String str : arr) {
						text.replaceAll(str, "");
					}
					switch (naive.classify(text)) {
					case 1:
						pos++;
						break;
					case -1:
						neg++;
						break;
					case 0:
						neu++;
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject resultObject = new JSONObject();
		System.out.println(pos);
		resultObject.put("positive", pos);
		resultObject.put("negative", neg);
		System.out.println(neg);
		resultObject.put("neutral", neu);
		System.out.println(neu);

		System.err.println(resultObject);
		return resultObject;
	}
}
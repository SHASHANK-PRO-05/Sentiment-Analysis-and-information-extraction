package classifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class svm {
	public int classify(String query) {
		String res = null, score = null;
		int ret = 0;
		String t = query;
		try {

			query = query.replaceAll(" ", "%20");
			String url = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=9da29edcda29a81b549d0719eabc5f4da1847b40&sentiment=1&showSourceText=0&text="
					+ query;

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			// add request header
			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			int c = 0;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			Pattern pt1 = Pattern.compile("<type>(.*)</type>");
			Matcher m = pt1.matcher(result);
			while (m.find()) {
				res = m.group(1);
			}

			if (res.equals("neutral")) {
				/*
				 * Pattern pt2 = Pattern.compile("<score>(.*)</score>"); m =
				 * pt2.matcher(result); while (m.find()) { score = m.group(1); }
				 * ret = res + ":" + (Double.parseDouble(score) * 5D);
				 */
				ret = 0;
			} else if (res.equals("positive")) {
				ret = 1;
			} else
				ret = -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ret);
		return ret;
	}
}

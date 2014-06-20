import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * Servlet implementation class MainServlrt
 */
@WebServlet("/Servlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public JSONObject castObject;
	public Crawler cl = new Crawler();
	public sentenceSplitter sp = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.err.println("Getting cast reviews");
		sp = new sentenceSplitter(cl.getId(), cl.getUrl());
		String choice = request.getParameter("choice");
		if (choice.equals("L")) {
			System.out.println("Movies " + sp.movies);
			JSONObject jss = new JSONObject();
			jss.put("movies", sp.movies);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jss.toJSONString());
			response.getWriter().close();
			return;
		}
		JSONObject js = sp.reviewAnalyser(sp.review, sp.cast, sp.actors,
				sp.actresses, sp.directors);
		System.out.println(js);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(js.toJSONString());
		response.getWriter().close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String query = request.getParameter("query").toString();
		String choice = request.getParameter("choice");

		if (choice.equals("T")) {
			twitterMovieAnalyser tw = new twitterMovieAnalyser();
			JSONObject js = tw.get(query);
			System.err.println("Twitter search:");
			System.out.println(js);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(js.toJSONString());
			response.getWriter().close();

		} else {
			if (choice.equals("M")) {
				cl = new Crawler();
				System.err.println("Searching movie");
				castObject = cl.getCast(query);
				sp = new sentenceSplitter(cl.getId(), cl.getUrl());
				JSONObject js = sp.movieAnalyser(sp.review);
				System.out.println(js);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(js.toJSONString());
				response.getWriter().close();

			}
		}
	}
}

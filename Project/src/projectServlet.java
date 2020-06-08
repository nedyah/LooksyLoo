import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TreeSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * class used for Context Handlers within 
 * web server class 
 * 
 * @author hayde
 *
 */
public class projectServlet extends HttpServlet{
	/**
	 * private serialVersionUID var
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * private index variable used for this class 
	 */
	public static InvertedIndex index;
	
	/**
	 * returns a string of the current day of week
	 * 
	 * @return string of Day of week
	 */
	public static String dayOfWeek()
	{
		return Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
	}
	
	
	/**
	 * doGet method to write our http upon 
	 * server start up
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@Override 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		
		PrintWriter out = response.getWriter();
		
		out.printf("<html>%n");
		out.printf("<head>%n<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.6.2/css/bulma.min.css\">%n<title>%s</title></head>%n", "212 Search Engine");
		out.printf("<body>%n");
		//out.printf("<section class=\"hero is-sucess\">");
		out.printf("<section class=\"hero is-primary\">");
		out.printf("<div class=\"hero-body\">");
		out.printf("<div class=\"container\">");
		out.printf("<h1 class=\"title\">Hello, %s!</h1>%n", "Hayden");
		out.printf("<h2 class=\"subtitle\">Thank you for visiting on this fine %s.</h2>%n", dayOfWeek());
		out.printf("</div>");
		out.printf("</div>");
		out.printf("</section>");
		
		out.printf("<div class=\"content has-text-centered\">");
		printRequest(request, response);
		out.printf("</div>");
		
		out.printf("</body>%n");
		out.printf("</html>%n");
	}

	/**
	 * method to respond to form activation that 
	 * will display the query results
	 * 
	 * @param request
	 * @param response
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		
		String query = request.getParameter("query");
		
		String type = request.getParameter("type");

		query = query == null ? "" : query;
		// avoid XSS attacks
		query = StringEscapeUtils.escapeHtml4(query);
		
		TreeSet<String> clean = TextFileStemmer.stems(query);
		
		ArrayList<InvertedIndex.Result> results = (type.equals("partial")) ? index.partialSearch(clean) 
				: index.exactSearch(clean);
		
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		//added CSS 
		out.printf("<head>%n<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.6.2/css/bulma.min.css\"><title>%s</title></head>%n", "Results");
		
		out.printf("<body>%n");
		out.printf("<section class=\"hero is-primary\">");
		out.printf("<div class=\"hero-body\">");
		out.printf("<div class=\"container\">");
		out.printf("<h1 class=\"title\">Results for %s</h1>%n%n%n", query);
		out.printf("</div>");
		out.printf("</div>");
		out.printf("</section>");
		
		int count = 1;
		for (InvertedIndex.Result result : results)
		{
			out.printf("<p class=\"content has-text-centered\">%d. <a href=\"%s\">%s</a></p>%n", count++, result.getWhere(), result.getWhere());
		}
		out.printf("<div class=\"content has-text-centered\">");
		printRequest(request, response);
		out.printf("</div>");
		out.printf("%n</body>%n");
		out.printf("</html>%n");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * method to print form for user to input search query
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private static void printRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		// partial or exact search - extra features 
		out.printf("<input type=\"radio\" name=\"type\" value=\"partial\" checked> Partial Search<br>");
		out.printf("<input type=\"radio\" name=\"type\" value=\"exact\" checked> Exact Search<br>");
		out.printf("<p>Query:</p>%n");
		out.printf("<input type=\"text\" name=\"query\">%n");
		out.printf("<input class= \"button **is-large is-success is-rounded**\" type=\"submit\" value=\"Search\">%n");
		out.printf("</form>");
		
	}
}

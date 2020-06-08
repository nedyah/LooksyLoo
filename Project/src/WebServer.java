import java.awt.Desktop;
import java.net.URI;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * web server class that launches our server
 * 
 * @author hayde
 */
public class WebServer {
	/**
	 * Port used for server
	 */
	private final int PORT;
	
	/**
	 * index used for server 
	 */
	private final InvertedIndex index;
	
	/**
	 * Constructor for class 
	 * 
	 * @param index			used in servlet
	 * @param port
	 */
	public WebServer(InvertedIndex index, int port)
	{
		this.PORT = port;
		this.index = index; 
	}
	
	/**
	 * method to start server 
	 * @throws Exception 
	 */
	public void startServer() throws Exception
	{
		Server server = new Server(PORT);
		projectServlet.index = index;
		ServletContextHandler context1 = new ServletContextHandler();
		context1.setContextPath("/");

		context1.addServlet(projectServlet.class, "/");
		
		DefaultHandler defaultHandler = new DefaultHandler();
		ContextHandler defaultContext = new ContextHandler("logo.svg");
		
		defaultContext.setHandler(defaultHandler);
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {defaultContext, context1 });
		server.setHandler(handlers);

		openBrowser();
		server.start();
		server.join();
	}
	
	/**
	 * method to open browser without having to do so 
	 * manually... THANK GOD
	 * 
	 * @throws Exception
	 */
	public void openBrowser() throws Exception
	{
		String url = "http://localhost:8080/";
		
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			
			desktop.browse(new URI(url));
		}
		else
		{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("xdg-open " + url);
		}
	}
}

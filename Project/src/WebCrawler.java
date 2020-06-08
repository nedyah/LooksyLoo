import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class that will use multithreading to crawl 
 * a URL and clean/parse html into inverted Index. 
 * 
 * @author hayde
 *
 */
public class WebCrawler {
	/**
	 * private final que object 
	 */
	private final WorkQueue que;
	
	/**
	 * private final maximum redirects
	 */
	private final int redirects = 3;
	
	/**
	 * private Set for unique urls. 
	 */
	private final Set<String> URLs;
	
	/**
	 * private index used by this class and others
	 */
	private final ThreadSafeInvertedIndex index; 
	
	/**
	 * Constructor for WebCrawler class
	 * 
	 * @param index
	 * @param work 
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, WorkQueue work)
	{
		this.que = work;
		this.URLs = new HashSet<>();
		this.index = index;
	}
	
	/**
	 * method to crawl given a url and length of crawl
	 * 
	 * @param url
	 * @param limit
	 * @throws Exception
	 */
	public void crawl(String url, int limit) throws Exception 
	{		
		try 
		{
			URL seed = new URL(url);
			seed = LinkParser.clean(seed);
			URLs.add(seed.toString());
			que.execute(new Task(seed, limit));
		}
		finally 
		{
			que.finish();
		}
	}
	
	/**
	 * Task class that will execute the crawling with one or 
	 * more threads 
	 * 
	 * @author hayde
	 */
	private class Task implements Runnable {
		/**
		 * URL to crawl
		 */
		private final URL crawlURL;
		
		/**
		 * local index to be added to
		 */
		private InvertedIndex local;
		
		/**
		 * private number of pages to crawl 
		 */
		private final int limit;

		/**
		 * Constructor method to set members
		 * 
		 * @param url
		 * @param limit
		 */
		public Task(URL url, int limit)
		{
			this.crawlURL = url;
			this.limit = limit;
			local = new ThreadSafeInvertedIndex();
		}
		/**
		 * method will fetch html and add to local index 
		 * then merge with master, then will begin to crawl 
		 * further and keep going 
		 */
		@Override
		public void run() 
		{
			try
			{
				String html = HtmlFetcher.fetch(crawlURL, redirects);
				
				if (html == null)
				{
					System.out.println("No Html was found");
				}
				else
				{
					Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
					int count = 1;
					
					for (String word : TextParser.parse(HtmlCleaner.stripHtml(html)))
					{
						local.addIndex(stemmer.stem(word).toString().toLowerCase(), crawlURL.toString(), count++);
					}
					
					index.addAll(local);
					
					for (URL link : LinkParser.listLinks(crawlURL, html))
					{
						String urlPath = link.toString();
						
						synchronized (URLs)
						{
							if (URLs.contains(urlPath))
							{
								continue;
							}
							
							if (URLs.size() >= limit)
							{
								break;
							}
							
							URLs.add(urlPath);
							que.execute(new Task(link, limit));
						}
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Unable to run inside of Web Crawler Task");
			}
		}
		
		
	}
}

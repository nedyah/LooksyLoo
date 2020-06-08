import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco and Hayden Miller 
 * @version Fall 2019
 */
public class Driver {
	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args)  
	{
		// store initial start time
		Instant start = Instant.now();
		InvertedIndex index = null;
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndexBuilder builder = null;
		QueryInterface queryClass = null;
		WorkQueue que = null;
		WebCrawler crawler = null;

		try
		{
			if (parser.hasFlag("-threads"))
			{
				int threads = 5;
				if (parser.hasValue("-threads"))
				{		
					threads = Integer.parseInt(parser.getString("-threads", "5"));
					if (threads < 1)
					{
						threads = 5;
					}
				}
				ThreadSafeInvertedIndex threadIndex = new ThreadSafeInvertedIndex();
				index = threadIndex;
				que = new WorkQueue(threads);
				builder = new MultiThreadedBuilder(que, threadIndex);
				queryClass = new ThreadSafeQuery(threadIndex, que);
				crawler = new WebCrawler(threadIndex, que);
			}
			else
			{
				index = new InvertedIndex();
				builder = new InvertedIndexBuilder(index);
				queryClass = new QueryParser(index);
				crawler = new WebCrawler((ThreadSafeInvertedIndex) index, new WorkQueue());
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to use threading");
		}
		
		try 
		{
			if (parser.hasFlag("-url"))
			{
				String seed = parser.getString("-url");
				if (seed == null)
				{
					System.out.println("No seed url associated with URL flag");
				}
				else 
				{	
					crawler.crawl(seed, Integer.parseInt(parser.getString("-limit", "50")));
				}
				
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to crawl");
		}
		try 
		{
			Path inputPath = null;
			if ((inputPath = parser.getPath("-path")) != null) 
			{
				builder.build(inputPath);
			}
		}
		catch (Exception e)
		{
			System.out.println("We were unable to write to the json map with " + parser.getString("-path"));
		}
		
		try
		{
			// Check to see if we output to file  
			if (parser.hasFlag("-index"))
			{
				index.toJson(parser.getPath("-index", Path.of("index.json")));
			}
		}
		catch (IOException e)
		{
			System.out.println("We were unable to write to file for the Inverted Index" + parser.getString("-index"));
		}
		
		
		// Test to see if we need to make count file
		if (parser.hasFlag("-counts")) 
		{
			Path countsPath = parser.getPath("-counts", Path.of("counts.json"));
			
			try 
			{
				JsonWriter.asObject(index.getCount(), countsPath.normalize());
			}
			catch (Exception e) 
			{
				System.out.println("Unable to write to counts file " + parser.getString("-counts", "counts.json"));
			}
		}
		
		try 
		{
			if (parser.hasFlag("-query"))
			{
				queryClass.query(parser.getPath("-query"), parser.hasFlag("-exact"));
			}
			
		}
		catch (Exception e)
		{
			System.out.println("Cannot query using path of " + parser.getString("-query"));
		}
		
		try 
		{
			if (parser.hasFlag("-results"))
			{
				queryClass.writeQueryJson(parser.getPath("-results", Path.of("results.json")));
			}
		}
		catch (Exception e)
		{
			System.out.println("Cannot write Json query to path of " + parser.getString("-results", "results.json"));
		}
		
		try 
		{
			if (parser.hasFlag("-port"))
			{
				WebServer server = new WebServer(index, Integer.parseInt(parser.getString("-port", "8080")));
				server.startServer();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (que != null)
		{
			que.shutdown();
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}

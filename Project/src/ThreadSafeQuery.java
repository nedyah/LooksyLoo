import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A thread safe QueryParser
 * 
 * @author hayden
 */
public class ThreadSafeQuery implements QueryInterface
{
	/**
	 * private final WorkQue passed in by constructor 
	 */
	private final WorkQueue que;
	
	/**
	 * private final ThreadSafe Index 
	 */
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * private query Object that stores our query results. 
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> query;
	
	/**
	 * Constructor for this class that sets index and workQue
	 * 
	 * @param index 			index object 
	 * @param que				que 
	 */
	public ThreadSafeQuery(ThreadSafeInvertedIndex index, WorkQueue que) 
	{
		this.que = que;
		this.index = index;
		query = new TreeMap<>();
		
	}
	
	/**
	 * method to write to disk
	 * 
	 * @param path			path to write to
	 */
	@Override
	public void writeQueryJson(Path path) throws Exception
	{
		JsonWriter.queryJson(path, query);
	}
	
	/**
	 * method to query path
	 * 
	 * @param path				path to search
	 * @param exact 			know whether we exact
	 */
	@Override 
	public void query(Path path, boolean exact) throws Exception
	{
		QueryInterface.super.query(path, exact);
		que.finish();
	}
	
	/**
	 * Does the work of the queryparser with multithreading!!!
	 * @author hayde
	 *
	 */
	private class Task implements Runnable 
	{
		/**
		 * raw line of the query variable 
		 */
		private final String local;
		
		/**
		 * boolean to know whether this task will be doing 
		 * an exact search or not
		 */
		private final boolean exact; 
		
		/**
		 * Constructor for this class
		 * 
		 * @param query				query to set local
		 * @param exact				boolean to know whether we do exact search
		 */
		public Task(String query, boolean exact)
		{
			this.local = query;
			this.exact = exact;
		}
		
		/**
		 * run method that will run when the que executes a thread
		 */
		@Override
		public void run()
		{
			TreeSet<String> cleanQuery = TextFileStemmer.stems(local);
			
			if(!cleanQuery.isEmpty())
			{
				String joined = String.join(" ", cleanQuery);
				
				synchronized (query)
				{
					if (query.containsKey(local))
					{
						return;
					}	
				}
				
				ArrayList<InvertedIndex.Result> results = index.search(cleanQuery, exact);
				
				synchronized (query)
				{
					query.put(joined, results);
				}
			}
		}
	}

	/**
	 * thread safe way to execute lines.
	 * 
	 * @param line				line to parse
	 * @param exact				bool to know whtether we search with exact
	 */
	@Override
	public void parseLine(String line, boolean exact) {
		que.execute(new Task(line, exact));
	}

	/**
	 * method to return keyset of query
	 * @return Set<String> 		returns keyset of query object
	 */
	@Override
	public Set<String> getResultsKeys() {
		return Collections.unmodifiableSet(query.keySet());
	}

}

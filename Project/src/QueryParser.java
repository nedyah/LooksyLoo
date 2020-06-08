import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class that handles and stores the search queryJson object.
 * queryJson is a TreeMap with a string as its key and an arrayList of 
 * ResultComparable objects as its value
 * 
 * @author hayden
 */
public class QueryParser implements QueryInterface{
	/**
	 * queryJson that stores the results of search
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.Result>> results;

	/**
	 * private index object
	 */
	private final InvertedIndex index;

	/**
	 * Default constructor for the class that initializes queryJson object
	 * @param index 			index to be used for this class
	 */
	public QueryParser(InvertedIndex index)
	{
		results = new TreeMap<>();
		this.index = index;
	}

	/**
	 * writes QueryJson object to disk safely without sending to main 
	 * @param path 			Path past in to 
	 * @throws Exception 
	 */
	public void writeQueryJson(Path path) throws Exception
	{
		JsonWriter.queryJson(path, results);
	}

	/**
	 * Called by query function to stem lines and inject the code in to 
	 * the results 
	 * 
	 * @param line 			line to be paresed
	 * @param exact			whether we are doing exact or not
	 */
	public void parseLine(String line, boolean exact)
	{
			TreeSet<String> stems = TextFileStemmer.stems(line);
			String joined = String.join(" ", stems);

			if (!stems.isEmpty() && !results.containsKey(joined))
			{
				results.put(joined, index.search(stems, exact));
			}
	}

	/**
	 * getter method to return an unmodifiable keyset of 
	 * queryJson.
	 * 
	 * @return keyset of queries
	 */
	public Set<String> getResultsKeys() {
		return Collections.unmodifiableSet(results.keySet());
	}
}

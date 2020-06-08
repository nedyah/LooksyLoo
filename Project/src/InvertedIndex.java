import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Hayden Miller git: nedyah
 * Data structure class to encapsulate the index TreeMap 
 * and the count TreeMap. It safely injects the data structures 
 * and calls JsonWriter for the index in a safe manner. Class
 * Also contains several getters and setters to allow access
 * to both data structures in a safe way. 
 */
public class InvertedIndex 
{
	/**
	 *  private final variable that stores our Inverted Index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Private variable that stores our Counts data 
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Constructor for the class 
	 * instantiates index and jsonCount
	 */
	public InvertedIndex()
	{
		// Declare our Data Structures when InvertedIndex class is created 
		index = new TreeMap<>();
		counts = new TreeMap<String, Integer>();
	}
	
	/**
	 * Method to merge another index in to our main 
	 * index
	 * 
	 * @param local			foreign index to be added
	 */
	public void addAll(InvertedIndex local)
	{
		for (String word : local.index.keySet())
		{
			if (this.index.containsKey(word))
			{
				for (String pathway : local.index.get(word).keySet())
				{
					if (this.index.get(word).containsKey(pathway))
					{
						this.index.get(word).get(pathway).addAll(local.index.get(word).get(pathway));
					}
					else
					{
						this.index.get(word).put(pathway, local.index.get(word).get(pathway));
					}
				}
			}
			else
			{
				this.index.put(word, local.index.get(word));
			}
		}
		
		for (String pathway : local.counts.keySet())
		{
			this.counts.put(pathway, this.counts.getOrDefault(pathway, 0) + local.counts.get(pathway));
		}
		
	}
	
	/**
	 * Getter method to return the jsonCount object 
	 * in a safe way where main cant access our data 
	 * @return jsonCount
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * returns an unmodifiable key set of jsonCount
	 * @return unmodifiable keyset 
	 */
	public Set<String> getLocations()
	{
		return Collections.unmodifiableSet(counts.keySet());
	}

	/**
	 * Returns key set of index object.
	 * @return Set<string> 
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * Returns an unmodifiable set of hits for a word at a particular
	 * location. 
	 * 
	 * @param word		word to be searched
	 * @param location	location of word
	 * @return positions of word in particular location 
	 *
	 */
	public Set<Integer> getPositions(String word, String location)
	{
		if (index.containsKey(word))
		{
			if (index.get(word).containsKey(location))
			{
				return Collections.unmodifiableSet(index.get(word).get(location));
			}
			else
			{
				return Collections.emptySet();
			}
		}
		else
		{
			return Collections.emptySet();
		}
	}

	/**
	 * 
	 * returns number of hits for a word in a particular file
	 * 
	 * @param word		word that is searched	
	 * @param file		file to be searched
	 * @return int		number of hits 
	 * 
	 */
	public int numLocation(String word, String file)
	{
		if (index.containsKey(word))
		{
			if (index.get(word).containsKey(file))
			{
				return index.get(word).get(file).size();
			}
			else 
			{
				return 0;
			}
		}
		return 0;
	}

	/**
	 * public search function to determine whether we do partial
	 * or exact and will return an ArrayList of Results. 
	 * 
	 * @param queries					Collection of queries 
	 * @param exact						boolean to know whether we are doing exact
	 * @return List 					List of Results	objects
	 */
	public ArrayList<Result> search(Collection<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}

	/**
	 * Peforms a partial search of the collection of queries and 
	 * returns an Array List of Results. 
	 * 
	 * @param queries			Collection of queries to be searched
	 * @return ArrayList		ArrayList of Results 
	 */
	public ArrayList<Result> partialSearch(Collection<String> queries) {
		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String query : queries)
		{
			for (String stem : index.tailMap(query).keySet())
			{
				if (stem.startsWith(query))
				{
					searchHelper(stem, lookup, results);
				}
				else
				{
				 		break;
				}
			}
			
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Helper function that exact and partial search utilizes to
	 * insert in to our ArrayList of Results 
	 * 
	 * @param word 				word to be searched 
	 * @param lookup			lookup map to check if word is contained.
	 * @param results			Our ArrayList of results we update
	 */
	private void searchHelper(String word, Map<String, Result> lookup, ArrayList<Result> results)
	{
		Set<String> locations = index.get(word).keySet();

		for (String location : locations)
		{
			if (lookup.containsKey(location))
			{
				lookup.get(location).update(word);
			}
			else
			{
				Result result = new Result(location);
				result.update(word);
				lookup.put(location, result);
				results.add(result);
			}
		}
		
	}

	/**
	 * Performs an exact search given the collection of queries. 
	 * 
	 * @param queries			Queries to be searched 
	 * @return results			ArrayList of results class
	 */
	public ArrayList<Result> exactSearch(Collection<String> queries) 
	{
		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String query : queries)
		{
			if (index.containsKey(query))
			{
				searchHelper(query, lookup, results);
			}
			
		}

		Collections.sort(results);
		return results;
		
	}

	/**
	 * Stores information on matches for searches
	 * @author hayde
	 */
	public class Result implements Comparable<Result> {

		/**
		 * variable to store file path
		 */
		private final String where;

		/**
		 * variable to store count 
		 */
		private int count;

		/**
		 * variable to store score of search match 
		 */
		private double score;


		/**
		 * constructor with arguments
		 * to set private variables 
		 * 
		 * @param path
		 */
		public Result(String path)
		{
			this.where = path;
			this.count = 0;
			this.score = 0;
		}
		
		/**
		 * Overrides compareTo in order to use Collections.sort.
		 * First compares score, then count, and finally path. 
		 * 
		 * @return int 		either -1 or 1 
		 */
		@Override
		public int compareTo(Result o) {
			int compareScore = Double.compare(o.score, this.score);
			int compareCount = Integer.compare(o.count, this.count);
			int comparePath = this.where.compareTo(o.where);
			
			if ((compareScore) != 0)
			{
				return compareScore;
			}
			else 
			{
				if (compareCount != 0)
				{
					return compareCount;
				}
				else
				{
					return comparePath;
				}
			}
		}

		/**
		 * returns where variable 
		 * @return where
		 */
		public String getWhere() {
			return where;
		}

		/**
		 * returns count 
		 * @return count		count of result
		 */
		public int getCount() {
			return count;
		}

		/**
		 * return score variable
		 * @return score		score of result
		 */
		public double getScore() {
			return score;
		}
		

		/**
		 * Updates the count and score of the Result class
		 * 
		 * @param word		used to update our count and score	
		 */
		private void update(String word)
		{
			this.count += index.get(word).get(where).size();
			this.score = (double) count / (double) counts.get(where);
		}
	}

	
	/**
	 * Gets locations of given word or empty set 
	 * if word is not in index. Returns an unmodifiable set of strings with a particular
	 * word. 
	 * @param word			word we will be using to return locations
	 * @return unmodifiable set of inner key set of index 
	 */
	public Set<String> getLocations(String word)
	{
		if (index.containsKey(word)) 
		{
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		else
		{
			return Collections.emptySet();
		}
	}
	
	/**
	 * Safe way to write to file without passing main access to 
	 * our index data structure. 
	 * 
	 * @param path		File to be written to
	 * @throws IOException
	 * 
	 */
	public void toJson(Path path) throws IOException {
		JsonWriter.asNestedObjects(index, path);
	}

	/**
	 * public class to addIndex one at a time. 
	 * Called by addAll, inserts word in to index,
	 * inserts path in to index and locations of word 
	 * in the index. Lastly it injects our Count 
	 * data structure with the file name and word 
	 * count of file.
	 * 
	 * @param word		word to be added 
	 * @param pretty	file to be added
	 * @param num 		number to be added for position 
	 */
	public void addIndex(String word, String pretty, int num)
	{
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(pretty, new TreeSet<Integer>());
		index.get(word).get(pretty).add(num);

		if (counts.get(pretty) == null)
		{
			counts.put(pretty, num);
		}
		else
		{
			counts.put(pretty, Math.max(counts.get(pretty), num));
		}
	}
	
}

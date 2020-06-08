import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe version of {@link InvertedIndex} using a read/write lock.
 *
 * @see InvertedIndex
 * @see ReadWriteLock
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;

	/**
	 * Initializes an unsorted thread-safe indexed set.
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	/**
	 * Getter method to return the jsonCount object 
	 * in a safe way where main cant access our data 
	 * @return jsonCount		returns jsonCount 
	 */
	@Override
	public Map<String, Integer> getCount() { 
		lock.readLock().lock();
		try 
		{
			return super.getCount();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * returns an unmodifiable key set of jsonCount
	 * @return unmodifiable keyset 
	 */
	@Override
	public Set<String> getLocations()
	{
		lock.readLock().lock();
		try 
		{
			return super.getLocations();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Returns key set of index object.
	 * @return Set<string> 		returns keyset of index object 
	 */
	@Override
	public Set<String> getWords() { 
		lock.readLock().lock();
		try
		{
			return super.getWords();
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Returns an unmodifiable set of hits for a word at a particular
	 * location. 
	 * 
	 * @param word					word to be searched
	 * @param location				location of word
	 * @return positions 			positions of word in particular location 
	 *
	 */
	public Set<Integer> getPositions(String word, String location)
	{
		lock.readLock().lock();
		try
		{
			return super.getPositions(word, location);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * returns number of hits for a word in a particular file
	 * 
	 * @param word					word that is searched	
	 * @param file					file to be searched
	 * @return int					number of hits 
	 * 
	 */
	@Override
	public int numLocation(String word, String file)
	{
		lock.readLock().lock();
		try
		{
			return super.numLocation(word, file);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Returns number of partial hits given query word and file. 
	 * 
	 * @param queries				Words to be searched
	 * @return ArrayList			ArrayList of words to be searched 
	 */
	@Override
	public ArrayList<Result> partialSearch(Collection<String> queries)
	{
		lock.readLock().lock();
		try
		{
			return super.partialSearch(queries);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Performs an exact search given the collection of queries. 
	 * 
	 * @param queries				queries used to search
	 * @return ArrayList<Result>	Returns arraylist of results
	 */
	@Override
	public ArrayList<Result> exactSearch(Collection<String> queries) 
	{
		lock.readLock().lock();
		try
		{
			return super.exactSearch(queries);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Safe way to write to file without passing main access to 
	 * our index data structure. 
	 * 
	 * @param path		File to be written to
	 * @throws IOException
	 */
	@Override
	public void toJson(Path path) throws IOException 
	{
		lock.readLock().lock();
		try
		{
			super.toJson(path);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}
	
	/**
	 * public class to addIndex one at a time. 
	 * Called by addAll, inserts word in to index,
	 * inserts path in to index and locations of word 
	 * in the index. Lastly it injects our Count 
	 * data structure with the file name and word 
	 * count of file.
	 * 
	 * @param word				word to be added 
	 * @param pretty			file to be added
	 * @param num 				number to be added for position 
	 */
	@Override
	public void addIndex(String word, String pretty, int num)
	{
		lock.writeLock().lock();
		try
		{
			super.addIndex(word, pretty, num);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	/**
	 * Method to merge local index to main index in a threadsafe manner
	 * 
	 * @param index				index to be merged in to main
	 */
	@Override
	public void addAll(InvertedIndex index)
	{
		lock.writeLock().lock();
		try
		{
			super.addAll(index);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
}

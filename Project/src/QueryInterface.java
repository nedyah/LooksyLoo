import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Interface that will be implemented by 
 * QueryParser and ThreadSafeQuery
 * @author hayde
 *
 */
public interface QueryInterface {

	/**
	 * method to write to file 
	 * 
	 * @param path				path to write to
	 * @throws Exception
	 */
	public void writeQueryJson(Path path) throws Exception;

	/**
	 * method to parseLine
	 * 
	 * @param line				line to parse
	 * @param exact				exact boolean
	 */
	public void parseLine(String line, boolean exact);

	/**
	 * Method that returns Results keys
	 * @return	Set<String> 		Set of strings from results
	 */
	public Set<String> getResultsKeys();
	
	/**
	 * default method to query 
	 * 
	 * @param path				path to query
	 * @param exact				exact boolean
	 * @throws Exception
	 */
	public default void query(Path path, boolean exact) throws Exception {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String raw;

			while ((raw = reader.readLine()) != null)
			{
				parseLine(raw, exact);
			}
		}
}

}

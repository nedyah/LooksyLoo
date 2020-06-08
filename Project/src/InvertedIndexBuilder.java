import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author hayden
 * Class to inject the file or directory in to our 
 * data structure class. 
 */
public class InvertedIndexBuilder {
	
	/**
	 * The default stemmer algorithm used by this class
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt or .text extension
	 * (case-insensitive).
	 */
	public static final Predicate<Path> IS_TEXT = path -> (path.toString().toLowerCase().endsWith(".txt")
			|| path.toString().toLowerCase().endsWith(".text")) && Files.isRegularFile(path);

	/**
	 * private index object used further protect data from main. 
	 */
	private final InvertedIndex index;

	/**
	 * Constructor for the class to set up
	 * index for our class 
	 * 
	 * @param index		index data structure to set our own index
	 */
	public InvertedIndexBuilder(InvertedIndex index)
	{
		this.index = index;
	}

	/**
	 * public method that calls build method
	 * 
	 * @param path				Path using to build index
	 * @throws Exception 
	 */
	public void addFile(Path path) throws Exception { addFile(path, this.index); }

	/**
	 * Class will build our inverted index whether it is a file or 
	 * directory. Calls addFile which parses file and injects our 
	 * inverted index data structure by calling methods in Inverted
	 * index class 
	 * 
	 * @param inputPath 		Path we will use to build our index
	 * @throws Exception 		
	 */
	public void build(Path inputPath) throws Exception 
	{
		List<Path> subPathList = getTextFiles(inputPath);
		
		for (Path path : subPathList)
		{	
			addFile(path);
		}
	}
	
	/**
	 * Class will walk and read an inputPath and return an ArrayList of Paths
	 * 
	 * @param inputPath 			InputPath to be walked/filtered and collected in to a list
	 * @return subPathList			List of Paths given and inputPath
	 * @throws IOException 
	 */
	public static List<Path> getTextFiles(Path inputPath) throws IOException 
	{
		try(Stream<Path> subPaths = Files.walk(inputPath, FileVisitOption.FOLLOW_LINKS))
		{	
			// Filters only files, maps the Paths to string and collects them to the list SubPathList
			List<Path> subPathList = subPaths.filter(IS_TEXT)
					.collect(Collectors.toList());
			
			return subPathList;
		}
	}

	/**
	 * method to add only one path to our inverted Index, accepts a path to an object
	 * and index object in order to call addIndex to actually inject data structure. 
	 * Reads file line by line and parses file for 
	 * 
	 * @param inputPath 		File we are reading 
	 * @param index 			index passed in
	 * @throws Exception 
	 */
	public static void addFile(Path inputPath, InvertedIndex index) throws Exception
	{
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
			String raw;
			int count = 0;
			String pretty = inputPath.toString();
			
			while ((raw = reader.readLine())!= null)
			{
				String tokens[] = TextParser.parse(raw);
				
				for (String token : tokens)
				{
					count++;
					
					index.addIndex(stemmer.stem(token).toString(), pretty, count);
				}
			}
		}
	}
}

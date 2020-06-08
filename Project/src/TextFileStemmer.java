import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class to parse and stem words. 
 * @author hayde
 * @author CS 212 Software Development
 * 
 *
 */
public class TextFileStemmer {
	
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @return array of parsed strings 
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #stems(String, Stemmer)
	 */
	public static TreeSet<String> stems(String line) { 
		return stems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer 
	 * @return array of Strings 
	 * 
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> stems(String line, Stemmer stemmer) {

		String []temp = TextParser.parse(line);
		TreeSet<String> list = new TreeSet<String>();
		for (int i = 0; i < temp.length; i++)
		{

			temp[i] = stemmer.stem(temp[i]).toString();

			String t = (String) stemmer.stem(temp[i]);
			if (!list.contains(t))
			{
				list.add(t);
			}

		}

		return list;
		

	}
}

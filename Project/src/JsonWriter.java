import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class JsonWriter {
	
	/**
	 * Default object to achieve desired output with score 
	 */
	static DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
	
	/**
	 * Writes the elements as a pretty JSON array.
	 * Calls indent. 
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException  {
		Iterator<Integer> iter = elements.iterator();
		writer.write('[');
		writer.write('\n');

		if (iter.hasNext())
		{
			indent(iter.next(), writer, level + 1);	
		}
		while(iter.hasNext())
		{
			writer.write(",\n");
			indent(iter.next(), writer, level + 1);	
		}
		writer.write('\n');
		indent(writer, level);
		writer.write(']');
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 * @throws IOException 
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) throws IOException {
		StringWriter writer = new StringWriter();
		asArray(elements, writer, 0);
		return writer.toString();
	}

	/**
	 * Writes the Object as a pretty JSON object.
	 * Calls quote and indent 
	 * 
	 * @param map the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> map, Writer writer, int level) throws IOException {

		Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator();
		writer.write('{');
		writer.write('\n');
		if (iter.hasNext())
		{
			Map.Entry<String, Integer> entry = iter.next();
			quote(entry.getKey(), writer, level + 1);
			writer.write(": " + entry.getValue().toString());
		}
		while(iter.hasNext())
		{
			writer.write(",\n");
			Map.Entry<String, Integer> entry = iter.next();
			quote(entry.getKey(), writer, level + 1);
			writer.write(": " + entry.getValue().toString());
		}
		writer.write('\n');
		writer.write('}');
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param map the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Integer> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(map, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the Nested Object as a nested pretty JSON object.
	 * Calls asArray, quote, and indent. 
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level) throws IOException {
		writer.write("{\n");
		Iterator<String> iter = elements.keySet().iterator();
		indent(writer, 1);
		
		if (iter.hasNext())
		{
			String temp = iter.next();
			quote(temp, writer, 1);
			writer.write(": ");
			asArray(elements.get(temp), writer, level + 1);
		}
		while (iter.hasNext())
		{
			writer.write(",\n");
			String temp = iter.next();
			quote(temp, writer, 1);
			writer.write(": ");
			asArray(elements.get(temp), writer, level + 1);
		}
		writer.write('\n');
		indent(writer, 1);
		writer.write("}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	
	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObjects(Map, Writer, int)
	 */
	public static String asNestedObjects(Map<String,TreeMap<String,TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObjects(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param map the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObjects(Map, Writer, int)
	 */
	public static void asNestedObjects(Map<String, TreeMap<String, TreeSet<Integer>>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObjects(map, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param map the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObjects(Map<String, TreeMap<String, TreeSet<Integer>>> map, Writer writer, int level) throws IOException {
		writer.write("{\n");

		Iterator<String> iter = map.keySet().iterator();

		if (iter.hasNext())
		{
			String temp = iter.next();
			quote(temp, writer, 1);
			writer.write(": ");
			asNestedObject(map.get(temp), writer, level + 1);
		} 
		while (iter.hasNext())
		{
			writer.write(",\n");
			String temp = iter.next();
			quote(temp, writer, 1);
			writer.write(": ");
			asNestedObject(map.get(temp), writer, level + 1);
		}
		writer.write('\n');
		writer.write("}\n");
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * public method that will call method to write to file, tries to use a bufferedreader to pass
	 * additional writer object to method. 
	 * 
	 * @param path					path to write to
	 * @param queryJson		queryJsonTreeMap object
	 * @throws Exception 
	 */
	public static void queryJson(Path path, TreeMap<String, ArrayList<InvertedIndex.Result>> queryJson) throws Exception
	{
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			queryJson(writer, queryJson, 0);
		}
	}

	/**
	 * method that writes brackets and calls asSearch to down 
	 * further in nested data structure json. 
	 * 
	 * @param writer 		writer object to write to file
	 * @param queryJson			object to write as json 
	 * @param level			current level of indentation
	 * @throws Exception 
	 * 
	 */
	public static void queryJson(Writer writer, TreeMap<String, ArrayList<InvertedIndex.Result>> queryJson, int level) throws Exception
	{
		writer.write('{');
		writer.write('\n');
		Iterator<Entry<String, ArrayList<InvertedIndex.Result>>> iter = queryJson.entrySet().iterator();
		
		if (iter.hasNext())
		{
			JsonWriter.asSearch(iter.next(), writer, level + 1);
		}
		while(iter.hasNext())
		{
			writer.write(",\n");
			JsonWriter.asSearch(iter.next(), writer, level + 1);
		}
		writer.write('\n');
		writer.write('}');
	}

	/**
	 * method that will write the key value in pretty json format 
	 * and uses an iterator to pass the resultcomparable object to 
	 * the writeResult class. 
	 * 
	 * @param entry				object to write as json 
	 * @param writer			writer object to write to file
	 * @param level				current level of indentation
	 * @throws Exception 
	 * 
	 */
	public static void asSearch(Entry<String, ArrayList<InvertedIndex.Result>> entry, Writer writer, int level) throws Exception
	{
		JsonWriter.indent(writer, level);
		JsonWriter.quote(entry.getKey(), writer);
		writer.write(": [");
		
		Iterator<InvertedIndex.Result> iter = entry.getValue().iterator();
		
		if (iter.hasNext())
		{
			writer.write('\n');
			writeResult(iter.next(), writer, level);
			
		}
		while (iter.hasNext())
		{
			writer.write(",\n");
			writeResult(iter.next(), writer, level);
		}
		writer.write('\n');
		JsonWriter.indent(writer, level);
		writer.write(']');
	}
	
	/**
	 * method that will write the contents of the resultcomparable class 
	 * to a file in a pretty json like format. 
	 * 
	 * @param result		Object with info we will be using to write 
	 * @param writer		writer to write to file.
	 * @param level			level of indentation 
	 * @throws Exception 
	 */
	public static void writeResult(InvertedIndex.Result result, Writer writer, int level) throws Exception
	{
		JsonWriter.indent(writer, level + 1);
		writer.write('{');
		writer.write('\n');

		JsonWriter.indent(writer, level + 2);
		JsonWriter.quote("where", writer);
		writer.write(": ");
		JsonWriter.quote(result.getWhere(), writer);
		writer.write(",\n");

		JsonWriter.indent(writer, level + 2);
		JsonWriter.quote("count", writer);
		writer.write(": ");
		writer.write(Integer.toString(result.getCount()));
		writer.write(",\n");

		JsonWriter.indent(writer, level + 2);
		JsonWriter.quote("score", writer);
		writer.write(": ");
		writer.write(FORMATTER.format(result.getScore()));
		writer.write("\n");
		JsonWriter.indent(writer, level + 1);
		writer.write("}");
	}
}

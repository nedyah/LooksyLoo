import java.nio.file.Path;

/**
 * thread safe builder class 
 */
public class MultiThreadedBuilder extends InvertedIndexBuilder {
	
	/**
	 * Worker que object 
	 */
	private final WorkQueue que;
	
	/**
	 * index object used for the this class 
	 */
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * Constructor class for the builder 
	 * 
	 * @param work			worker object to sync with class 
	 * @param index			index used locally by builder class. 
	 */
	public MultiThreadedBuilder(WorkQueue work, ThreadSafeInvertedIndex index)
	{
		super(index);
		this.que = work;
		this.index = index;
	}
	
	/**
	 * executes a new task and passes it a path
	 * 
	 * @param path			path to add 
	 */
	public void addFile(Path path) {
		que.execute(new Task(path));
	}
	
	/**
	 * Call that will send path to super build method and 
	 * will finish the que once done. 
	 * 
	 * @param path			path to add 
	 */
	public void build(Path path) throws Exception {
		super.build(path);
		que.finish();
	}	

	/**
	 *
	 * task class that will be ran by threads
	 *
	 */
	private class Task implements Runnable {
		
		/**
		 * number adding 
		 */
		private final Path path;
		
		/**
		 * Constructor for BuildTask class 
		 * 
		 * @param path		Path object used for this class
		 */
		public Task(Path path)
		{
			this.path = path;
		}
		
		/**
		 * Method that will run our worker will run which 
		 * creates a local index and adds it to our main. 
		 */
		@Override 
		public void run()
		{
			try
			{
				InvertedIndex local = new InvertedIndex();
				addFile(path, local);
				index.addAll(local);
			}
			catch (Exception e)
			{
				System.out.println("Unable to add " + path + " to index.");
			}
		}
		
	}

}

package Database_Catalog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import UnionFind.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import java.io.Serializable;

/**
 * This class is a data catalog to store the input path and output path and the database 
 * schema. We use singleton pattern to create this class.
 * 
 * @author Lini Tan, lt398
 */
public class Catalog implements Cloneable, Serializable {

	private static Catalog instance;
	
	private String inputLocation;
	private String outputLocation;
	private String tempLocation;
	private static HashMap<String, ArrayList> map;
	private HashMap<String, String> pairAlias;
	private String joinConfig;
	private String sortConfig;
	private String indexConfig;
	private ArrayList<String> indexList;
	private int queryNumber;
	private HashMap<String, ArrayList> indexInfo;
	private UnionFind unionFind;
	private List<Expression> joinResidual;
	private List<Expression> selectResidual;
	private StatsInfo statsInfo;
	private HashMap<String, List<IndexInfo>> indexMap;

	
	/* Private Constructor prevents any other class from instantiating */
	private Catalog() {
	}

	public synchronized static Catalog getInstance() {

		/* Lazy initialization, creating object on first use */
		if (instance == null) {
			synchronized (Catalog.class) {
				if (instance == null) {
					instance = new Catalog();
				}
			}
		}

		return instance;
	}

	/** Restrict cloning of object */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**test the implementation of singleton pattern */
	public void display() {
		System.out.println("Hurray! I am display from Singleton!");
	}
	
	/**Get the input directory path
	 * 
	 * @return the input path
	 * */
	public String getInputLocation() {
		return inputLocation;
	}
	
	/**Get the output directory path
	 * 
	 * @return the output path
	 * */
	public String getOutputLocation() {
		return outputLocation;
	}
	
	/**Get the temp directory path
	 * 
	 * @return the temp path
	 * */
	public String getTempLocation() {
		return tempLocation;
	}
	
	/**Get the join configuration
	 * 
	 * @return the string represents join configuration
	 * */
	public String getJoinConfig() {
		return joinConfig;
	}
	
	/**Get the sort configuration
	 * 
	 * @return the string represents sort configuration
	 * */
	public String getSortConfig() {
		return sortConfig;
	}
	
	/**Get the index configuration
	 * 
	 * @return the string represents sort configuration
	 * */
	public String getIndexConfig() {
		return indexConfig;
	}
	
	/**Get the index list
	 * 
	 * @return the string represents index configuration
	 * */
	public ArrayList<String> getIndexList() {
		return indexList;
	}
	
	/**Get the index list info
	 * 
	 * @return the string represents index configuration
	 * */
	public HashMap<String, ArrayList> getIndexInfo() {
		return indexInfo;
	}
	
	/**Get database schema
	 * 
	 * @return a hash map of the schema
	 * */
	public HashMap getSchema() {
		return map;
	}
	
	/**Get the number key of which query is running now
	 * 
	 * @return the number key of which query is running now
	 * */
	public int getQueryNumber() {
		return queryNumber;
	}
	
	/**Get the unionfind
	 * 
	 * @return the unionfind
	 * */
	public UnionFind getUnionFind(){
		return unionFind;
	}
	
	/**Get the SelectResidual list
	 * 
	 * @return the SelectResidual list
	 * */
	public List<Expression> getSelectResidual(){
		return selectResidual;
	}
	
	/**Get the JoinResidual list
	 * 
	 * @return the JoinResidual list
	 * */
	public List<Expression> getJoinResidual(){
		return joinResidual;
	}
	
	/**Get the stats info txt
	 * 
	 * @return the stats info txt
	 * */
	public StatsInfo getStatsInfo(){
		return statsInfo;
	}
	
	/**Get the indexmap with store the relationship of table and its index info
	 * 
	 * @return the index map
	 * */
	public HashMap<String, List<IndexInfo>> getIndexMap(){
		return indexMap;
	}
	
	public List<List<IndexInfo>> getIndexInfoList(){
		return new LinkedList<>(indexMap.values());
	}
	
	public int getLeaveNum(String tableName, String columnName){
		for(IndexInfo indexinfo: indexMap.get(tableName)){
			if(indexinfo.getColumn().toString().equals(columnName)){
				return indexinfo.getNumLeaves();
			}
		}
		return 0;
	}
	
	/**set the  indexmap with store the relationship of table and its index info
	 * 
	 * @param the index map
	 * */
	public void setIndexMap(HashMap<String, List<IndexInfo>> map){
		indexMap = map;
	}
	
	/**set the stats info
	 * 
	 * @param the stats info
	 * */
	public void setStatsInfo(StatsInfo stat){
		statsInfo = stat;
	}
	
	/**set the unionfind
	 * 
	 * @param the unionfind
	 * */
	public void setUnionFind(UnionFind uf){
		unionFind = uf;
	}
	
	/**set the SelectResidual list
	 * 
	 * @param the SelectResidual list
	 * */
	public void setSelectResidual(List<Expression> selectList){
		selectResidual = selectList;
	}
	
	/**set the JoinResidual list
	 * 
	 * @param the JoinResidual list
	 * */
	public void setJoinResidual(List<Expression> joinList){
		joinResidual = joinList;
	}
	
	/**set the input directory location
	 * 
	 * @param the input path
	 * */
	public void setinputLocation(String input) {
		this.inputLocation = input;
	}
	
	/**set the output directory location
	 * 
	 * @param the output path
	 * */
	public void setoutputLocation(String output) {
		this.outputLocation = output;
	}
	
	/**Set the join configuration
	 * 
	 * @param the string represents join configuration
	 * */
	public void setJoinConfig(String jConfig) {
		this.joinConfig =  jConfig;
	}
	
	/**Set the sort configuration
	 * 
	 * @param the string represents sort configuration
	 * */
	public void setSortConfig(String sConfig) {
		this.sortConfig =  sConfig;
	}
	
	/**Set the index configuration
	 * 
	 * @param the string represents sort configuration
	 * */
	public void setIndexConfig(String iConfig) {
		this.indexConfig =  iConfig;
	}
	
	/**Set the index list
	 * 
	 * @param the string represents index configuration
	 * */
	public void setIndexList(ArrayList<String> iList) {
		this.indexList = iList;
	}
	
	/**Set the index list info
	 * 
	 * @return the string represents index configuration
	 * */
	public void setIndexInfo(HashMap<String, ArrayList> iInfo) {
		this.indexInfo = iInfo;
	}
	
	/**set the table-field schema
	 * 
	 * @param a hash map corresponding to the schema
	 * */
	public void setSchema(HashMap schema) {
		this.map = schema;
	}
	
	/**set up the pairAlias hash map
	 * 
	 * @param a hash map corresponding to alias-tableName pair
	 * */
	public void setPairAlias(HashMap<String, String> Alias) {
		this.pairAlias = Alias;
	}
	
	/**Set the number key of which query is running now
	 * 
	 * @param the number key of which query is running now
	 * */
	public void setQueryNumber(int number) {
		this.queryNumber = number;
	}
	
	/**get the pairAlias hash map
	 * 
	 * @return the pairAlias hash map
	 * */
	public HashMap<String, String> getPairAlias() {
		return pairAlias;
	}
	
	public void copy1Schema(String tableName) {
		this.map.put(tableName+"*", (ArrayList) map.get(tableName).clone());
	}

	public void settempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}
	
	
}
//		String command = System.console().readLine();
//		int start = command.indexOf(".jar")+5;
//		String sub = command.substring(start);
//		int end = sub.indexOf(" ");
//		inputLocation = sub.substring(0, end);
//		outputLocation = sub.substring(end+1);
//	}
//		//read file 
//
//	//java -jar cs4321 p2.jar inputdir outputdir
//	  public static void main(String[] args) throws IOException {
//		    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		    String command = reader.readLine();
//		    int start = command.indexOf(".jar")+5;
//			String sub = command.substring(start);
//			int end = sub.indexOf(" ");
//			String inputLocation = sub.substring(0, end);
//			String outputLocation = sub.substring(end+1);
//		    System.out.println("input: " + inputLocation);
//		    System.out.println("output: " + outputLocation);
//		  
//		//read file 
//		  
//		  BufferedReader br = new BufferedReader(new FileReader("schema.txt"));
//		  HashMap<String, ArrayList> map = new HashMap<>();
//		  try {
//		      StringBuilder sb = new StringBuilder();
//		      String line = br.readLine();
//
//		      while (line != null) {
//		    	  System.out.println("new line " + line);
//		    	  String[] oneLine = line.split(" ");
//		    	  ArrayList fields = new ArrayList();
//		    	  for(int i=1; i<oneLine.length; i++){
//		    		  fields.add(oneLine[i]);
//		    	  }
//		    	  map.put(oneLine[0], fields);
//		    	  
//		          sb.append(line);
//		          sb.append(System.lineSeparator());
//		          line = br.readLine();
//		      }
//		      String everything = sb.toString();
//		      System.out.println(everything);
//		  } finally {
//		      br.close();
//		      
//		  }
//			
//		  
//}

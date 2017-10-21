package physicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Collections;


import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * This class is used when sql query contains order by condition.
 * 
 * @author Lini Tan, lt398
 */
public class SortOperator extends Operator {
	
	Operator childOp;
	LinkedList<Tuple> sorted;
	List order;
	HashMap<String,String> PairAlias;
	
	//public SortOperator(Operator op, List orderList) {
	public SortOperator(Operator op, PlainSelect selectBody) {
		// TODO Auto-generated constructor stub
		//System.out.println(orderList.toString());
		childOp = op;
		sorted = new LinkedList<Tuple>();
		order = selectBody.getOrderByElements();
		Catalog catalog = Catalog.getInstance();
		PairAlias = catalog.getPairAlias();
		BuildList();
		
	}
	
	/**This method grab all tuple from the child operator and add in the list*/
	public void BuildList(){
		Tuple a = childOp.getNextTuple();
		while(a!=null){
			//add all tuples into list
			sorted.add(a);
			if(order==null){
				
				
				Object[] mapKeySet = a.getTupleMap().keySet().toArray();
				order = new ArrayList();
				for(int i=mapKeySet.length-1; i>=0; i--){
					//System.out.println(key.toString());
					order.add(mapKeySet[i].toString());
					
				}
				
				//System.out.println(order);
			}
			a=childOp.getNextTuple();
			//System.out.println(a.getTuple().toString());
			
		}
		
		System.out.println("hh"+order.toString());
		Collections.sort(sorted, new TupleComparator(order));
	}
	
	/** This method return the satisfied tuple and get next tuples.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		//System.out.println("yy");
		if(sorted.size()!=0) return (Tuple) sorted.pop();
		return null;
	}

	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		childOp.reset();
	}

	/**To print your result. Use for debug */
	@Override
	public void dump() {
		// TODO Auto-generated method stub
		
		//BuildList();
		Tuple a =getNextTuple();
		while(a != null){
			System.out.println(a.getTuple());
			a =getNextTuple();
		}
		
	}
	
	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		//BuildList();
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a!= null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}
}

/**
 * This class set up the tuple comparator
 * 
 * @author Lini Tan, lt398
 */
class TupleComparator implements Comparator<Tuple> {
	List condition;
	HashMap<String,String> PairAlias;
	
	public TupleComparator(List order){
		condition = order;
		Catalog catalog = Catalog.getInstance();
		PairAlias = catalog.getPairAlias();
	}
	
	/**compare two tuples.
	 * 
	 * @param two tuples to be compared
	 * */
    @Override
    public int compare(Tuple a, Tuple b) {
    	HashMap amap = a.getTupleMap();
    	ArrayList alist = a.getTuple();
    	ArrayList blist = b.getTuple();
    	HashSet s = new HashSet<String>();
    	
    	
    	//compare by condition
    	
    	//System.out.println(condition.toString());
    	for(int i=0; i<condition.size(); i++){
    		//System.out.println(condition.get(i).toString());
    		//System.out.println(amap.get(condition.get(i).toString()));
    		
    		String aliasCondition = condition.get(i).toString();
    		int dot = aliasCondition.indexOf(".");
    		String aliasT = aliasCondition.substring(0, dot);
    		aliasCondition = PairAlias.get(aliasT) + "." + aliasCondition.substring(dot+1, aliasCondition.length());
    		
    		
    		//System.out.println(aliasCondition);
    		int index = (int) amap.get(aliasCondition);
    		int anum = Integer.parseInt((String) alist.get(index));
    		//System.out.println("a "+anum);
    		
    		int bnum = Integer.parseInt((String) blist.get(index));
    		//System.out.println("b "+bnum);
    		if(anum < bnum) return -1;
    		if(anum > bnum) return 1;
    		s.add(aliasCondition);
    	}
    	
    	//if not condition or condition are the same, compare tuple from left to right.
    	for(int i=0; i<alist.size(); i++){
    		if(Integer.parseInt((String)alist.get(i))<Integer.parseInt((String)blist.get(i))){
    			return -1;
    		}else if(Integer.parseInt((String)alist.get(i))>Integer.parseInt((String)blist.get(i))){
    			return 1;
    		}
    	}
    	return 0;
    	
  //  	ArrayList field = new ArrayList();
//    	List table = a.getNameList();
//    	//System.out.println("a.getNameList()" + table);
//    	
//    	
//    	Catalog c = Catalog.getInstance();
//    	for(int j=0; j<table.size(); j++){
//    		ArrayList tfield = (ArrayList) c.getSchema().get(table.get(j));
//    		for(int i=0; i<tfield.size(); i++){
//    			
//    			field.add(table.get(j).toString()+"."+tfield.get(i));
//    		}
//    	}
    	
//    	HashMap tuplemap = a.getTupleMap();
//    	Object[] keySet = tuplemap.keySet().toArray();
//    	for(int i = keySet.length-1; i>=0; i-- ){
//    		field.add(keySet[i]);
//    	}
//    	//System.out.println(x);
//    	
//    	//System.out.println("amap " + amap);
//    	//System.out.println(field.toString());
//    	for(int i=0; i<field.size()-1; i++){
//    		if(s.contains(field.get(i).toString())) continue;
//    		if(amap.get(field.get(i))==null) continue;
//    		//System.out.println(field.get(i));
//    		int index = (int) amap.get(field.get(i));
//    		//System.out.println(index);
//    		int anum = Integer.parseInt((String) alist.get(index));
//    		//System.out.println("a "+anum);
//    		int bnum = Integer.parseInt((String) blist.get(index));
//    		//System.out.println("b "+bnum);
//    		if(anum < bnum) {
//    			//System.out.println("haha");
//    			return -1;}
//    		if(anum > bnum) return 1;
//    		//s.add(field.get(i));
//    	}
//        return 0;
    }
}
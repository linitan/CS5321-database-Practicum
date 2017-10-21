package logicalOperator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import visitor.PhysicalPlanBuilder;

/**
 * This class is used when sql query contains select condition.
 * 
 * @author Lini Tan, lt398
 */
public class LogicalProjectOperator extends LogicalOperator {
//	HashMap map;
	List project;
	LogicalOperator childOp;
	PlainSelect plainSelectBody;
//	String table;
//	HashMap<String, String> pairAlias;
	
	public LogicalProjectOperator(PlainSelect selectBody, LogicalOperator op) {
		// TODO Auto-generated constructor stub
		childOp = op;
		project = selectBody.getSelectItems();
		plainSelectBody = selectBody;
//		Catalog c = Catalog.getInstance();
//		map=c.getSchema();
//		table ="";
//		
//		pairAlias = c.getPairAlias();
	}

	public List getSelectitem(){
		return this.project;
	}
	
	public PlainSelect getPlainSelect(){
		return plainSelectBody;
	}
	
	public LogicalOperator getchildOperator(){
		return this.childOp;
	}

	@Override
	public void accept(PhysicalPlanBuilder physicalPlanBuilder) throws IOException {
		physicalPlanBuilder.visit(this);
	}

}
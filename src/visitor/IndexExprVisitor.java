package visitor;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * @author benzhangtang
 *
 * [IndexExprVisitor] visits the selection expression, and separates 
 * index selection from selection without index
 */
public class IndexExprVisitor implements ExpressionVisitor {

	private String ColumnName;
	private Long lowkey;
	private Long highkey;
	private Expression NoIndexExpr;
	private Boolean lowopen;
	private Boolean highopen; 

	/*
	 * constructor of the class
	 */
	public IndexExprVisitor (String columnName) {
		this.ColumnName = columnName;
	}

	/*
	 * Get methods for this class 
	 */
	public Long getLowkey() {
		return this.lowkey;
	}

	public Long getHighkey() {
		return this.highkey;
	}

	public Expression getNoIndexExpr() {
		return this.NoIndexExpr;
	}
	
	public boolean isLowopen() {
		if(this.lowopen==null){
			return false;
		}
		return this.lowopen;
	}
	
	public boolean isHighopen() {
		if(this.highopen==null){
			return false;
		}
		return this.highopen;
	}

	/*
	 * [addNoIndexExpr] joins expressions with no index together with AND expression.
	 */
	public void addNoIndexExpr (Expression expr) {
		if (this.NoIndexExpr == null) {
			this.NoIndexExpr = expr;
		}else {
			this.NoIndexExpr = new AndExpression(this.NoIndexExpr, expr);
		} 
	}

	/*
	 * [hasNoIndexColunm] returns true if one of the binary expressions has no index on it. 
	 */
	public boolean hasNoIndexColunm(Expression left, Expression right) {
		if (left instanceof Column) {
			if (!((Column) left).getColumnName().equals(this.ColumnName)) {
				return true;
			}
		}

		if (right instanceof Column) {
			if (!((Column) right).getColumnName().equals(this.ColumnName)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void visit(NullValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Function arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(InverseExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(JdbcParameter arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DoubleValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LongValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DateValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TimeValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(TimestampValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Parenthesis arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(StringValue arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Addition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Division arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Multiplication arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Subtraction arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AndExpression arg0) {
		// TODO Auto-generated method stub
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
	}

	@Override
	public void visit(OrExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Between arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * [Select] "=" visit method. (eg. R.A =9; 8=B.c) 
	 */
	@Override
	public void visit(EqualsTo arg0) {
		// TODO Auto-generated method stub
		Expression leftExpr = arg0.getLeftExpression();
		Expression rightExpr = arg0.getRightExpression();

		//check if those sub-expressions contain indexed attribute (column) or not.
		if (this.hasNoIndexColunm(leftExpr,rightExpr)) {
			this.addNoIndexExpr(arg0);;
			return;
		}else { // if one of (both) the tables is indexed
			if (leftExpr instanceof LongValue) {
				this.highkey = ((LongValue) leftExpr).getValue();
				this.lowkey = ((LongValue) leftExpr).getValue();
				this.lowopen = false;
				this.highopen = false; 
			}
			if (rightExpr instanceof LongValue) {
				this.highkey = ((LongValue) rightExpr).getValue();
				this.lowkey = ((LongValue) rightExpr).getValue();
				this.lowopen = false;
				this.highopen = false; 
			}
		}
	}

	@Override
	public void visit(GreaterThan arg0) {
		// TODO Auto-generated method stub
		Expression leftExpr = arg0.getLeftExpression();
		Expression rightExpr = arg0.getRightExpression();

		//check if those sub-expressions contain indexed attribute (column) or not.
		if (this.hasNoIndexColunm(leftExpr,rightExpr)) {
			this.addNoIndexExpr(arg0);;
			return;
		}else { // if one of (both) the tables is indexed
			if (leftExpr instanceof LongValue) { // 9 > T.b
				long highK = ((LongValue) leftExpr).getValue();
				if (this.highkey == null || this.highkey > highK) {
					this.highkey = highK;
					this.highopen = true;
				}
			}
			if (rightExpr instanceof LongValue) { //T.b >9
				long lowK = ((LongValue) rightExpr).getValue();
				if (this.lowkey == null || this.lowkey < lowK) {
					this.lowkey = lowK;
					this.lowopen = true;
				}
			}
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		// TODO Auto-generated method stub
		Expression leftExpr = arg0.getLeftExpression();
		Expression rightExpr = arg0.getRightExpression();

		//check if those sub-expressions contain indexed attribute (column) or not.
		if (this.hasNoIndexColunm(leftExpr,rightExpr)) {
			this.addNoIndexExpr(arg0);;
			return;
		}else { // if one of (both) the tables is indexed
			if (leftExpr instanceof LongValue) { // 9 >= T.b
				long highK = ((LongValue) leftExpr).getValue();
				if (this.highkey == null || this.highkey > highK) {
					this.highkey = highK;
					this.highopen = false;
				}
			}
			if (rightExpr instanceof LongValue) { //T.b >= 9
				long lowK = ((LongValue) rightExpr).getValue();
				if (this.lowkey == null || this.lowkey < lowK) {
					this.lowkey = lowK;
					this.lowopen = false;
				}
			}
		}
	}

	@Override
	public void visit(InExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(IsNullExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LikeExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(MinorThan arg0) {
		// TODO Auto-generated method stub
		Expression leftExpr = arg0.getLeftExpression();
		Expression rightExpr = arg0.getRightExpression();

		//check if those sub-expressions contain indexed attribute (column) or not.
		if (this.hasNoIndexColunm(leftExpr,rightExpr)) {
			this.addNoIndexExpr(arg0);;
			return;
		}else { // if one of (both) the tables is indexed
			if (leftExpr instanceof LongValue) { // 9 < T.b
				long lowK = ((LongValue) leftExpr).getValue();
				if (this.lowkey == null || this.lowkey > lowK) {
					this.lowkey = lowK;
					this.lowopen = true;
				}
			}
			if (rightExpr instanceof LongValue) { //T.b <9
				long highK = ((LongValue) rightExpr).getValue();
				if (this.highkey == null || this.highkey < highK) {
					this.highkey = highK;
					this.highopen = true;
				}
			}
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		// TODO Auto-generated method stub
		Expression leftExpr = arg0.getLeftExpression();
		Expression rightExpr = arg0.getRightExpression();

		//check if those sub-expressions contain indexed attribute (column) or not.
		if (this.hasNoIndexColunm(leftExpr,rightExpr)) {
			this.addNoIndexExpr(arg0);;
			return;
		}else { // if one of (both) the tables is indexed
			if (leftExpr instanceof LongValue) { // 9 < T.b
				long lowK = ((LongValue) leftExpr).getValue();
				if (this.lowkey == null || this.lowkey > lowK) {
					this.lowkey = lowK;
					this.lowopen = false;
				}
			}
			if (rightExpr instanceof LongValue) { //T.b <9
				long highK = ((LongValue) rightExpr).getValue();
				if (this.highkey == null || this.highkey < highK) {
					this.highkey = highK;
					this.highopen = false;
				}
			}
		}
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		// TODO Auto-generated method stub
		this.addNoIndexExpr(arg0);
	}

	@Override
	public void visit(Column arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SubSelect arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(CaseExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhenClause arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExistsExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AllComparisonExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AnyComparisonExpression arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Concat arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Matches arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseAnd arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseOr arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BitwiseXor arg0) {
		// TODO Auto-generated method stub

	}

}

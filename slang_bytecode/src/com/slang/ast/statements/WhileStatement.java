package com.slang.ast.statements;

import java.util.ArrayList;

import com.slang.ast.Visitor;
import com.slang.ast.expression.Expression;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing while statement
public class WhileStatement extends Statement {
	private Expression conditionExpression;
	private ArrayList<Statement> statements;

	public WhileStatement(Expression condExpr, ArrayList<Statement> statements) {
		this.conditionExpression = condExpr;
		this.statements = statements;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Expression getConditionExpression() {
		return conditionExpression;
	}

	public ArrayList<Statement> getStatements() {
		return statements;
	}

}

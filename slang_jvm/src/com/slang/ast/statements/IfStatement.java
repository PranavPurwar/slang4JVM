package com.slang.ast.statements;

import java.util.ArrayList;

import com.slang.ast.Visitor;
import com.slang.ast.expression.Expression;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing if statement
public class IfStatement extends Statement {
	private Expression conditionExpression;
	private ArrayList<Statement> truePartStatements;
	private ArrayList<Statement> falsePartStatements;

	public IfStatement(Expression expression,
			ArrayList<Statement> truePartStatements,
			ArrayList<Statement> falsePartStatements) {
		this.conditionExpression = expression;
		this.truePartStatements = truePartStatements;
		this.falsePartStatements = falsePartStatements;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Expression getConditionExpression() {
		return conditionExpression;
	}

	public ArrayList<Statement> getTruePartStatements() {
		return truePartStatements;
	}

	public ArrayList<Statement> getFalsePartStatements() {
		return falsePartStatements;
	}
}

package com.slang.ast.statements;

import com.slang.ast.Visitor;
import com.slang.ast.expression.Expression;
import com.slang.ast.expression.VariableExpression;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing assignment statement
public class AssignmentStatement extends Statement {
	private VariableExpression variableExpression;
	private Expression expression;
	private Symbol symbol;

	public AssignmentStatement(VariableExpression variable,
			Expression expression, Symbol symbol) {
		this.variableExpression = variable;
		this.expression = expression;
		this.symbol = symbol;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public VariableExpression getVariableExpression() {
		return variableExpression;
	}

	public Expression getExpression() {
		return expression;
	}

	public Symbol getSymbol() {
		return symbol;
	}
}

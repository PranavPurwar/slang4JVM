package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;
import com.slang.front_end.Token;

// Node representing a logical expression
public class LogicalExpression extends Expression {
	private Expression lExpression, rExpression;
	private Token operator;

	public LogicalExpression(Expression a, Expression b, Token o) {
		this.operator = o;
		this.lExpression = a;
		this.rExpression = b;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Expression getLExpression() {
		return lExpression;
	}

	public Expression getRExpression() {
		return rExpression;
	}

	public Token getOperator() {
		return operator;
	}
}

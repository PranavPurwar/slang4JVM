package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.Operator;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing an unary expression
public class UnaryExpression extends Expression {
	private Expression expression;
	private Operator operator;

	public UnaryExpression(Expression a, Operator o) {
		this.expression = a;
		this.operator = o;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Expression getExpression() {
		return expression;
	}

	public Operator getOperator() {
		return operator;
	}
}
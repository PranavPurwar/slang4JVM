package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a negation expression
public class NegationExpression extends Expression {
	private Expression expression;

	public NegationExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Expression getExpression() {
		return expression;
	}
}

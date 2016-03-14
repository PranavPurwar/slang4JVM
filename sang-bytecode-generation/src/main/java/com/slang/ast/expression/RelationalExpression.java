package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.Operator;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a relational expression
public class RelationalExpression extends Expression {
	private Operator operator;
	private Expression lExpression, rExpression;

	public RelationalExpression(Expression a, Expression b, Operator o) {
		this.lExpression = a;
		this.rExpression = b;
		this.operator = o;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Operator getOperator() {
		return operator;
	}

	public Expression getLExpression() {
		return lExpression;
	}

	public Expression getRExpression() {
		return rExpression;
	}
}

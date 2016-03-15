package com.slang.ast.statements;

import com.slang.ast.Visitor;
import com.slang.ast.expression.Expression;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing print statement
public class PrintStatement extends Statement {
	private Expression expression;

	public PrintStatement(Expression expression) {
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
package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.DataType;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a variable
public class VariableExpression extends Expression {
	private String name;

	public VariableExpression(String name, DataType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public String getName() {
		return name;
	}
}

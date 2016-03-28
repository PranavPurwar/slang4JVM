package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.DataType;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a boolean constant
public class BooleanConstantExpression extends Expression {
	private Symbol symbol;

	public BooleanConstantExpression(boolean value) {
		this.symbol = new Symbol();
		this.symbol.setName(null);
		this.symbol.setBooleanValue(value);
		this.symbol.setType(DataType.BOOLEAN);
		this.type = DataType.BOOLEAN;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Symbol getSymbol() {
		return symbol;
	}
}

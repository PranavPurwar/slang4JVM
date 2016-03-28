package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.DataType;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a string literal
public class StringLiteralExpression extends Expression {
	private Symbol symbol;

	public StringLiteralExpression(String value) {
		this.symbol = new Symbol();
		this.symbol.setName(null);
		this.symbol.setStringValue(value);
		this.symbol.setType(DataType.STRING);
		this.type = DataType.STRING;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Symbol getSymbol() {
		return symbol;
	}
}

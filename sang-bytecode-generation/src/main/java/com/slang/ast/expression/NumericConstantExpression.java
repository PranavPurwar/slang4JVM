package com.slang.ast.expression;

import com.slang.ast.Visitor;
import com.slang.ast.meta.DataType;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a numeric constant
public class NumericConstantExpression extends Expression {
	private Symbol symbol;

	public NumericConstantExpression(double value) {
		this.symbol = new Symbol();
		this.symbol.setName(null);
		this.symbol.setDoubleValue(value);
		this.symbol.setType(DataType.NUMERIC);
		this.type = DataType.NUMERIC;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Symbol getSymbol() {
		return symbol;
	}
}

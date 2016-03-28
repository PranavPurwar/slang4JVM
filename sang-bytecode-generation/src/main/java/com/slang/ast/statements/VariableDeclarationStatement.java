package com.slang.ast.statements;

import com.slang.ast.Visitor;
import com.slang.ast.meta.DataType;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing variable declaration statement
public class VariableDeclarationStatement extends Statement {
	private String name;
	private DataType type;

	public VariableDeclarationStatement(String name, DataType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public String getVariableName() {
		return name;
	}

	public DataType getVariableType() {
		return type;
	}
}

package com.slang.ast.statements;

import com.slang.ast.Visitor;
import com.slang.ast.meta.Type;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing variable declaration statement
public class VariableDeclarationStatement extends Statement {
	private String name;
	private Type type;

	public VariableDeclarationStatement(String name, Type type) {
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

	public Type getVariableType() {
		return type;
	}
}

package com.slang.ast;

import java.util.ArrayList;

import com.slang.ast.expression.Expression;
import com.slang.ast.meta.DataType;
import com.slang.ast.statements.Statement;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;
import com.slang.contexts.SymbolTable;

// Node representing closure: base class of function
public class Closure {
	private String name;
	private ArrayList<Statement> statements;
	private ArrayList<Symbol> formalParameters;
	private DataType type;
	private int index;

	public Closure(String name, DataType returnType, SymbolTable symbolTable,
			ArrayList<Statement> statements, ArrayList<Symbol> fParameters,
			int index) {
		this.name = name;
		this.type = returnType;
		this.statements = statements;
		this.formalParameters = fParameters;
		this.index = index;
	}

	public Symbol accept(Context context, Visitor visitor,
			ArrayList<Expression> actualParameters) throws Exception {
		return visitor.visit(context, this, actualParameters);
	}

	public String getName() {
		return name;
	}

	public DataType getType() {
		return type;
	}

	public ArrayList<Symbol> getFormalParameters() {
		return formalParameters;
	}

	public ArrayList<Statement> getStatements() {
		return statements;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}

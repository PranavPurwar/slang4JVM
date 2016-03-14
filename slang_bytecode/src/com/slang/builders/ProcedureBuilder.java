package com.slang.builders;

import java.util.ArrayList;

import com.slang.ast.Procedure;
import com.slang.ast.meta.Type;
import com.slang.ast.statements.Statement;
import com.slang.contexts.ParserContext;
import com.slang.contexts.Symbol;

// Builder for procedures
public class ProcedureBuilder {
	private String procedureName;
	private Type returnType;
	private ParserContext parserContext;
	private ArrayList<Statement> statements;
	private ArrayList<Symbol> formalParameters;
	// Index points the source code location of function definition
	private int index;

	public ProcedureBuilder(ParserContext context) {
		this.parserContext = context;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public ParserContext getContext() {
		return parserContext;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public void setStatements(ArrayList<Statement> statements) {
		this.statements = statements;
	}

	public void setFormalParameters(ArrayList<Symbol> fParameters) {
		for (Symbol symbol : fParameters) {
			this.parserContext.getSymbolTable().addSymbol(symbol);
		}
		this.formalParameters = fParameters;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Procedure build() {
		return new Procedure(procedureName, returnType,
				parserContext.getSymbolTable(), statements,
				formalParameters, index);
	}
}

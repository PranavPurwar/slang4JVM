package com.slang.contexts;


public class Context {
	protected SymbolTable symbolTable;

	public Context() {
		this.symbolTable = new SymbolTable();
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
}

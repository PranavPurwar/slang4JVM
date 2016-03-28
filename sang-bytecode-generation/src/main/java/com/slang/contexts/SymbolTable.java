package com.slang.contexts;

import java.util.HashMap;
import java.util.Map;

import com.slang.ast.expression.VariableExpression;

// Represents a symbol table attached to any context
public class SymbolTable {
	private Map<String, Symbol> symbolTable = new HashMap<String, Symbol>();

	public void addSymbol(Symbol symbol) {
		symbolTable.put(symbol.getName(), symbol);
	}

	public void assign(VariableExpression variable, Symbol symbol) {
		symbolTable.put(variable.getName(), symbol);
	}

	public Symbol getSymbol(String name) {
		return symbolTable.get(name);
	}
}

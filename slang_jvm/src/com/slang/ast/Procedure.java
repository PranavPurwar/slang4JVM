package com.slang.ast;

import java.util.ArrayList;

import com.slang.ast.meta.Type;
import com.slang.ast.statements.Statement;
import com.slang.contexts.Symbol;
import com.slang.contexts.SymbolTable;

// Node representing a function
public class Procedure extends Closure {
	public Procedure(String procedureName, Type returnType,
			SymbolTable symbolTable, ArrayList<Statement> statements,
			ArrayList<Symbol> fParameters, int index) {
		super(procedureName, returnType, symbolTable, statements, fParameters,
				index);
	}
}

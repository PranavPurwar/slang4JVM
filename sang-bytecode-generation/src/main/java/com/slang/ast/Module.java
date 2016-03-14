package com.slang.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.slang.ast.expression.Expression;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a module
public class Module {
	private String name;
	private LinkedHashMap<String, Procedure> procedures;

	public Module(String moduleName, LinkedHashMap<String, Procedure> procedures) {
		this.name = moduleName;
		this.procedures = procedures;
	}

	public Symbol accept(Context context, Visitor visitor,
			ArrayList<Expression> cmdLineArguments) throws Exception {
		return visitor.visit(context, this, cmdLineArguments);
	}

	public Procedure findProcedure(String procedureName) {
		String key = procedureName.toUpperCase();
		return procedures.get(key);
	}

	public String getName() {
		return name;
	}

	public LinkedHashMap<String, Procedure> getProcedures() {
		return procedures;
	}
}

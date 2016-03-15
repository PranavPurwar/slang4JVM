package com.slang.builders;

import java.util.LinkedHashMap;

import com.slang.ast.Module;
import com.slang.ast.Procedure;

// Builder for modules (Builder Pattern)
public class ModuleBuilder {
	private String moduleName;
	private LinkedHashMap<String, Procedure> procedures;

	public ModuleBuilder(String moduleName) {
		this.moduleName = moduleName.toUpperCase();
		procedures = new LinkedHashMap<String, Procedure>();
	}

	public void addProcedure(Procedure procedure) {
		String key = procedure.getName().toUpperCase();
		procedures.put(key, procedure);
	}

	public Procedure findProcedure(String procedureName) {
		String key = procedureName.toUpperCase();
		return procedures.get(key);
	}

	public boolean isProcedure(String procedureName) {
		String key = procedureName.toUpperCase();
		return (procedures.get(key) != null) ? true : false;
	}

	// Function which builds the module
	public Module build() {
		return new Module(moduleName, procedures);
	}
}

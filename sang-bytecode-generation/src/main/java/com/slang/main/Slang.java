package com.slang.main;

import com.slang.ast.Module;
import com.slang.compiler.Compiler;
import com.slang.frontend.RDParser;
import com.slang.frontend.SemanticAnalyser;
import com.slang.interpretor.Interpreter;

public class Slang {
	private Module module;
	private RDParser parser;

	public Slang(String name, String code) throws Exception {
		this.parser = new RDParser(name, code);
		this.module = parser.parse();

		SemanticAnalyser semanticAnalyser = new SemanticAnalyser(parser, module);
		this.module.accept(null, semanticAnalyser, null);
	}

	public void interpret() throws Exception {
		Interpreter interpreter = new Interpreter(parser);
		module.accept(null, interpreter, null);
	}

	public void compile() throws Exception {
		Compiler compiler = new Compiler();
		module.accept(null, compiler, null);
	}
}

package com.slang.bytecode.util;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.slang.ast.meta.DataType;
import com.slang.contexts.Symbol;

public class ByteCodeUtils {

	public static ByteCodeUtils getInstance() {
		return new ByteCodeUtils();
	}

	public String getDescription(DataType type, List<Symbol> formalParameters) {
		List<Type> types = new ArrayList<>();
		for(Symbol symbol : formalParameters){
			types.add(getType(symbol.getType()));
		}
		
		return Type.getMethodDescriptor(getType(type), (Type[])types.toArray());
	}

	public Type getType(DataType type) {
		switch (type) {
		case BOOLEAN:
			return Type.BOOLEAN_TYPE;
		case NUMERIC:
			return Type.DOUBLE_TYPE;
		case STRING:
			return Type.getType(String.class);
		default:
			return Type.VOID_TYPE;
		}
	}

}

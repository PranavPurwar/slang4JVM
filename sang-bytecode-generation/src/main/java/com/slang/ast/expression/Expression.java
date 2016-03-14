package com.slang.ast.expression;

import com.slang.ast.Visitable;
import com.slang.ast.meta.Type;

// Expression base class
public abstract class Expression implements Visitable {
	protected Type type;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}

package com.slang.ast.expression;

import com.slang.ast.Visitable;
import com.slang.ast.meta.DataType;

// Expression base class
public abstract class Expression implements Visitable {
	protected DataType type;

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
}

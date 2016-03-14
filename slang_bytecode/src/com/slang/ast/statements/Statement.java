package com.slang.ast.statements;

import com.slang.ast.Visitable;

// base class from which all statements are extended
public abstract class Statement implements Visitable {
	// Index points to the statement starting point in source code
	// used in error messages.
	protected int index;

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}

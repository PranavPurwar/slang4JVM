package com.slang.contexts;

import com.slang.ast.meta.Type;

// Represents a symbol
public class Symbol {
	private String name;
	private Type type;
	private String stringValue;
	private double doubleValue;
	private boolean booleanValue;
	// byte code index
	private int index;
	// to check for runtime error
	private boolean isValueNull = true;

	public Symbol() {
	}

	public Symbol(Type type) {
		this.name = null;
		this.type = type;
	}

	public Symbol(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.isValueNull = false;
		this.stringValue = stringValue;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.isValueNull = false;
		this.doubleValue = doubleValue;
	}

	public boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.isValueNull = false;
		this.booleanValue = booleanValue;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isValueNull() {
		return isValueNull;
	}
}

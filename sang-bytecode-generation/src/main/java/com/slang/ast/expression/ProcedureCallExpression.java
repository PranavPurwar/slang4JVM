package com.slang.ast.expression;

import java.util.ArrayList;

import com.slang.ast.Procedure;
import com.slang.ast.Visitor;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Node representing a function call
public class ProcedureCallExpression extends Expression {
	private Procedure procedure;
	private ArrayList<Expression> actualParameterExpressions;
	private String procedureName;

	public ProcedureCallExpression(Procedure procedure,
			ArrayList<Expression> aParameterExpressions) {
		this.procedure = procedure;
		this.actualParameterExpressions = aParameterExpressions;
	}

	// Constructor used for recursive function call
	public ProcedureCallExpression(String procedureName,
			ArrayList<Expression> aParameterExpressions) {
		this.procedureName = procedureName;
		this.actualParameterExpressions = aParameterExpressions;
	}

	@Override
	public Symbol accept(Context context, Visitor visitor) throws Exception {
		return visitor.visit(context, this);
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public ArrayList<Expression> getAcutalParameterExpressions() {
		return actualParameterExpressions;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;

	}
}

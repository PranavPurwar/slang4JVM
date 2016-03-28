package com.slang.frontend;

import java.util.ArrayList;

import com.slang.ast.Closure;
import com.slang.ast.Module;
import com.slang.ast.Procedure;
import com.slang.ast.Visitor;
import com.slang.ast.expression.BinaryExpression;
import com.slang.ast.expression.BooleanConstantExpression;
import com.slang.ast.expression.Expression;
import com.slang.ast.expression.LogicalExpression;
import com.slang.ast.expression.NegationExpression;
import com.slang.ast.expression.NumericConstantExpression;
import com.slang.ast.expression.ProcedureCallExpression;
import com.slang.ast.expression.RelationalExpression;
import com.slang.ast.expression.StringLiteralExpression;
import com.slang.ast.expression.UnaryExpression;
import com.slang.ast.expression.VariableExpression;
import com.slang.ast.meta.Operator;
import com.slang.ast.meta.DataType;
import com.slang.ast.statements.AssignmentStatement;
import com.slang.ast.statements.IfStatement;
import com.slang.ast.statements.PrintLineStatement;
import com.slang.ast.statements.PrintStatement;
import com.slang.ast.statements.ReturnStatement;
import com.slang.ast.statements.Statement;
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

public class SemanticAnalyser implements Visitor {
	private RDParser parser;
	private Module module;

	public SemanticAnalyser(RDParser parser, Module module) {
		this.parser = parser;
		this.module = module;
	}

	@Override
	public Symbol visit(Context context, Module module,
			ArrayList<Expression> cmdLineArguments) throws Exception {
		// Finds the main method and starts analysis from it
		Procedure mainProcedure = module.findProcedure("MAIN");
		mainProcedure.accept(context, this, null);
		return null;
	}

	@Override
	public Symbol visit(Context context, Closure closure,
			ArrayList<Expression> actualParameters) throws Exception {
		for (Statement statement : closure.getStatements()) {
			Symbol typeSymbol = statement.accept(null, this);
			// Checks for return statement and does type checking
			// Main function should not have return statement
			if (typeSymbol != null) {
				if (closure.getType() != typeSymbol.getType()
						|| closure.getName().equalsIgnoreCase("MAIN")) {
					throw typeError(statement.getIndex());
				}
			}
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, PrintStatement printStatement)
			throws Exception {
		DataType exprType = printStatement.getExpression().accept(null, this)
				.getType();
		if (exprType == DataType.ILLEGAL) {
			throw typeError(printStatement.getIndex());
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, PrintLineStatement printLineStatement)
			throws Exception {
		DataType exprType = printLineStatement.getExpression().accept(null, this)
				.getType();
		if (exprType == DataType.ILLEGAL) {
			throw typeError(printLineStatement.getIndex());
		}
		return null;
	}

	@Override
	public Symbol visit(Context context,
			VariableDeclarationStatement variableDeclarationStatement)
			throws Exception {
		return null;
	}

	@Override
	public Symbol visit(Context context, AssignmentStatement assignmentStatement)
			throws Exception {
		DataType exprType = assignmentStatement.getExpression().accept(null, this)
				.getType();
		DataType variableExprType = assignmentStatement.getVariableExpression()
				.accept(null, this).getType();
		if (variableExprType != exprType) {
			throw typeError(assignmentStatement.getIndex());
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, IfStatement ifStatement)
			throws Exception {
		DataType condExprType = ifStatement.getConditionExpression()
				.accept(null, this).getType();
		if (condExprType != DataType.BOOLEAN) {
			throw typeError(ifStatement.getIndex());
		}
		for (Statement statement : ifStatement.getTruePartStatements()) {
			statement.accept(null, this);
		}
		if (ifStatement.getFalsePartStatements() != null) {
			for (Statement statement : ifStatement.getFalsePartStatements()) {
				statement.accept(null, this);
			}
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, WhileStatement whileStatement)
			throws Exception {
		DataType condExprType = whileStatement.getConditionExpression()
				.accept(null, this).getType();
		if (condExprType != DataType.BOOLEAN) {
			throw typeError(whileStatement.getIndex());
		}
		for (Statement statement : whileStatement.getStatements()) {
			statement.accept(null, this);
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, ReturnStatement returnStatement)
			throws Exception {
		DataType exprType = returnStatement.getExpression().accept(null, this)
				.getType();
		return new Symbol(exprType);
	}

	@Override
	public Symbol visit(Context context,
			NumericConstantExpression numericConstantExpression)
			throws Exception {
		return new Symbol(numericConstantExpression.getSymbol().getType());
	}

	@Override
	public Symbol visit(Context context,
			StringLiteralExpression stringLiteralExpression) throws Exception {
		return new Symbol(stringLiteralExpression.getSymbol().getType());
	}

	@Override
	public Symbol visit(Context context,
			BooleanConstantExpression booleanConstantExpression)
			throws Exception {
		return new Symbol(booleanConstantExpression.getSymbol().getType());
	}

	@Override
	public Symbol visit(Context context, UnaryExpression unaryExpression)
			throws Exception {
		DataType lExprType = unaryExpression.getExpression().accept(null, this)
				.getType();
		if (lExprType == DataType.NUMERIC) {
			return typeSetterHelper(DataType.NUMERIC, unaryExpression);
		} else {
			return typeSetterHelper(DataType.ILLEGAL, unaryExpression);
		}
	}

	@Override
	public Symbol visit(Context context, BinaryExpression binaryExpression)
			throws Exception {
		DataType lExprType = binaryExpression.getLExpression().accept(null, this)
				.getType();
		DataType rExprType = binaryExpression.getRExpression().accept(null, this)
				.getType();

		if (lExprType != rExprType) {
			return typeSetterHelper(DataType.ILLEGAL, binaryExpression);
		} else if (lExprType == DataType.BOOLEAN) {
			return typeSetterHelper(DataType.ILLEGAL, binaryExpression);
		} else if (lExprType == DataType.STRING
				&& binaryExpression.getOperator() != Operator.PLUS) {
			return typeSetterHelper(DataType.ILLEGAL, binaryExpression);
		} else {
			return typeSetterHelper(lExprType, binaryExpression);
		}
	}

	@Override
	public Symbol visit(Context context, VariableExpression variableExpression)
			throws Exception {
		return new Symbol(variableExpression.getType());
	}

	@Override
	public Symbol visit(Context context, LogicalExpression logicalExpression)
			throws Exception {
		DataType lExprType = logicalExpression.getLExpression().accept(null, this)
				.getType();
		DataType rExprType = logicalExpression.getRExpression().accept(null, this)
				.getType();
		if (lExprType == DataType.BOOLEAN && rExprType == DataType.BOOLEAN) {
			return typeSetterHelper(DataType.BOOLEAN, logicalExpression);
		}
		return typeSetterHelper(DataType.ILLEGAL, logicalExpression);
	}

	@Override
	public Symbol visit(Context context, NegationExpression negationExpression)
			throws Exception {
		DataType exprType = negationExpression.getExpression().accept(null, this)
				.getType();
		if (exprType == DataType.BOOLEAN) {
			return typeSetterHelper(DataType.BOOLEAN, negationExpression);
		}
		return typeSetterHelper(DataType.ILLEGAL, negationExpression);
	}

	@Override
	public Symbol visit(Context context,
			RelationalExpression relationalExpression) throws Exception {
		DataType lExprType = relationalExpression.getLExpression()
				.accept(null, this).getType();
		DataType rExprType = relationalExpression.getRExpression()
				.accept(null, this).getType();

		if (lExprType == rExprType) {
			if (lExprType == DataType.NUMERIC) {
				relationalExpression.setType(DataType.BOOLEAN);
				return typeSetterHelper(DataType.BOOLEAN, relationalExpression);
			}
			if ((lExprType == DataType.STRING || lExprType == DataType.BOOLEAN)
					&& (relationalExpression.getOperator() == Operator.EQ || relationalExpression
							.getOperator() == Operator.NEQ)) {
				return typeSetterHelper(DataType.BOOLEAN, relationalExpression);
			}
		}
		return typeSetterHelper(DataType.ILLEGAL, relationalExpression);
	}

	@Override
	public Symbol visit(Context context,
			ProcedureCallExpression procedureCallExpression) throws Exception {
		Procedure procedure = procedureCallExpression.getProcedure();
		if (procedure == null) {
			procedure = module.findProcedure(procedureCallExpression
					.getProcedureName());
			// Sets the procedure in procedureCallExpression
			// Recursion is handled there, (avoided in interpreter and compiler
			procedureCallExpression.setProcedure(procedure);
			if (parameterTypeCheck(context, procedure.getFormalParameters(),
					procedureCallExpression.getAcutalParameterExpressions())) {
				return typeSetterHelper(DataType.ILLEGAL, procedureCallExpression);
			}
		} else {
			if (parameterTypeCheck(context, procedure.getFormalParameters(),
					procedureCallExpression.getAcutalParameterExpressions())) {
				return typeSetterHelper(DataType.ILLEGAL, procedureCallExpression);
			}
			procedure.accept(context, this,
					procedureCallExpression.getAcutalParameterExpressions());
		}
		DataType procedureReturnType = procedure.getType();
		return typeSetterHelper(procedureReturnType, procedureCallExpression);
	}

	private Exception typeError(int index) {
		String errorMessage = "SLANG COMPILE TIME ERROR - TYPE ERROR AT: "
				+ parser.getCurrentLine(index);
		return new Exception(errorMessage);
	}

	private Symbol typeSetterHelper(DataType type, Expression expression) {
		expression.setType(type);
		return new Symbol(type);
	}

	// Helper function to parameter type check
	private boolean parameterTypeCheck(Context context,
			ArrayList<Symbol> fParameters, ArrayList<Expression> aParamExpr)
			throws Exception {
		if (fParameters != null && aParamExpr != null
				&& fParameters.size() == aParamExpr.size()) {
			for (int i = 0; i < aParamExpr.size(); i++) {
				if (aParamExpr.get(i).accept(context, this).getType() != fParameters
						.get(i).getType()) {
					return true;
				}
			}
		}
		return false;
	}
}

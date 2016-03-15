package com.slang.interpreter;

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
import com.slang.ast.meta.Type;
import com.slang.ast.statements.AssignmentStatement;
import com.slang.ast.statements.IfStatement;
import com.slang.ast.statements.PrintLineStatement;
import com.slang.ast.statements.PrintStatement;
import com.slang.ast.statements.ReturnStatement;
import com.slang.ast.statements.Statement;
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.contexts.Context;
import com.slang.contexts.RuntimeContext;
import com.slang.contexts.Symbol;
import com.slang.frontend.RDParser;

public class Interpreter implements Visitor {
	private RDParser parser;

	public Interpreter(RDParser parser) {
		this.parser = parser;
	}

	@Override
	public Symbol visit(Context superContext, Module module,
			ArrayList<Expression> cmdLineArguments) throws Exception {
		// Finds the main method and starts interpretation from it
		Procedure mainProcedure = module.findProcedure("MAIN");
		mainProcedure.accept(superContext, this, null);
		return null;
	}

	@Override
	public Symbol visit(Context callerFnContext, Closure closure,
			ArrayList<Expression> actualParameterExpressions) throws Exception {
		RuntimeContext caleeFnContext = new RuntimeContext();
		ArrayList<Symbol> fParameters = closure.getFormalParameters();
		// Gets value for actual parameter and adds it into symbol table
		for (int i = 0; i < fParameters.size(); i++) {
			Symbol aParameterSymbol = actualParameterExpressions.get(i).accept(
					callerFnContext, this);
			Symbol fParameterSymbol = fParameters.get(i);
			caleeFnContext.getSymbolTable().addSymbol(
					getLocalVariable(aParameterSymbol, fParameterSymbol));
		}
		for (Statement statement : closure.getStatements()) {
			Symbol returnValueSymbol = statement.accept(caleeFnContext, this);
			// Checking for return statement
			if (returnValueSymbol != null) {
				return returnValueSymbol;
			}
		}

		// If no return statement find throws error except for 'MAIN' function
		if (closure.getName().equalsIgnoreCase("MAIN")) {
			return null;
		} else {
			throw runTimeError(closure.getIndex());
		}

	}

	@Override
	public Symbol visit(Context context, PrintStatement printStatement)
			throws Exception {
		Symbol symbol = printStatement.getExpression().accept(context, this);

		// Checks whether the value is null
		if (symbol == null || symbol.isValueNull()) {
			throw runTimeError(printStatement.getIndex());
		}

		System.out.print(getValue(symbol));
		return null;
	}

	@Override
	public Symbol visit(Context context, PrintLineStatement printLineStatement)
			throws Exception {
		Symbol symbol = printLineStatement.getExpression()
				.accept(context, this);

		if (symbol == null || symbol.isValueNull()) {
			throw runTimeError(printLineStatement.getIndex());
		}

		System.out.println(getValue(symbol));
		return null;
	}

	@Override
	public Symbol visit(Context context,
			VariableDeclarationStatement variableDeclarationStatement)
			throws Exception {
		Symbol varSymbol = new Symbol();
		varSymbol.setName(variableDeclarationStatement.getVariableName());
		varSymbol.setType(variableDeclarationStatement.getVariableType());
		context.getSymbolTable().addSymbol(varSymbol);
		return null;
	}

	@Override
	public Symbol visit(Context context, AssignmentStatement assignmentStatement)
			throws Exception {
		Symbol exprValue = assignmentStatement.getExpression().accept(context,
				this);

		if (exprValue == null || exprValue.isValueNull()) {
			throw runTimeError(assignmentStatement.getIndex());
		}

		context.getSymbolTable().assign(
				assignmentStatement.getVariableExpression(), exprValue);
		return null;
	}

	@Override
	public Symbol visit(Context context, IfStatement ifStatement)
			throws Exception {
		Symbol condExprValue = ifStatement.getConditionExpression().accept(
				context, this);

		if (condExprValue == null || condExprValue.isValueNull()) {
			throw runTimeError(ifStatement.getIndex());
		}

		if (condExprValue.getBooleanValue()) {
			for (Statement statement : ifStatement.getTruePartStatements()) {
				Symbol returnValue = statement.accept(context, this);
				if (returnValue != null) {
					return returnValue;
				}
			}
		} else if (ifStatement.getFalsePartStatements() != null) {
			for (Statement statement : ifStatement.getFalsePartStatements()) {
				Symbol returnValue = statement.accept(context, this);
				if (returnValue != null) {
					return returnValue;
				}
			}
		}

		return null;
	}

	@Override
	public Symbol visit(Context context, WhileStatement whileStatement)
			throws Exception {
		while (true) {
			Symbol condExprValue = whileStatement.getConditionExpression()
					.accept(context, this);

			if (condExprValue == null || condExprValue.isValueNull()) {
				throw runTimeError(whileStatement.getIndex());
			}

			if (!condExprValue.getBooleanValue()) {
				return null;
			}

			for (Statement statement : whileStatement.getStatements()) {
				Symbol returnValue = statement.accept(context, this);
				if (returnValue != null) {
					return returnValue;
				}
			}

		}
	}

	@Override
	public Symbol visit(Context context, ReturnStatement returnStatement)
			throws Exception {
		Symbol exprValue = returnStatement.getExpression()
				.accept(context, this);
		return exprValue;
	}

	@Override
	public Symbol visit(Context context, NumericConstantExpression ncExpression)
			throws Exception {
		Symbol tempSymbol = ncExpression.getSymbol();

		if (tempSymbol == null || tempSymbol.isValueNull()) {
			return null;
		}

		return tempSymbol;

	}

	@Override
	public Symbol visit(Context context,
			BooleanConstantExpression booleanConstantExpression)
			throws Exception {
		Symbol tempSymbol = booleanConstantExpression.getSymbol();

		if (tempSymbol == null || tempSymbol.isValueNull()) {
			return null;
		}

		return tempSymbol;
	}

	@Override
	public Symbol visit(Context context,
			StringLiteralExpression stringLiteralExpression) throws Exception {
		Symbol tempSymbol = stringLiteralExpression.getSymbol();

		if (tempSymbol == null || tempSymbol.isValueNull()) {
			return null;
		}

		return tempSymbol;
	}

	@Override
	public Symbol visit(Context context, UnaryExpression uExpression)
			throws Exception {
		Symbol uExprValue = new Symbol();
		uExprValue.setType(Type.NUMERIC);
		Symbol exprValue = uExpression.getExpression().accept(context, this);

		if (exprValue == null || exprValue.isValueNull()) {
			return null;
		}

		Double tempVarDouble = null;
		switch (uExpression.getOperator()) {
		case PLUS:
			tempVarDouble = exprValue.getDoubleValue();
			break;
		case MINUS:
			tempVarDouble = -exprValue.getDoubleValue();
			break;
		default:
			return null;
		}
		uExprValue.setDoubleValue(tempVarDouble);
		return uExprValue;
	}

	@Override
	public Symbol visit(Context context, BinaryExpression bExpression)
			throws Exception {
		Symbol lExprValue = bExpression.getLExpression().accept(context, this);
		Symbol rExprValue = bExpression.getRExpression().accept(context, this);

		if (lExprValue == null || rExprValue == null
				|| lExprValue.isValueNull() || rExprValue.isValueNull()) {
			return null;
		}

		Symbol exprValue = new Symbol();
		if (lExprValue.getType() == Type.NUMERIC) {
			exprValue.setType(Type.NUMERIC);
			Double tempVarDouble = null;
			switch (bExpression.getOperator()) {
			case PLUS:
				tempVarDouble = lExprValue.getDoubleValue()
						+ rExprValue.getDoubleValue();
				break;
			case MINUS:
				tempVarDouble = lExprValue.getDoubleValue()
						- rExprValue.getDoubleValue();
				break;
			case DIVIDE:
				tempVarDouble = lExprValue.getDoubleValue()
						/ rExprValue.getDoubleValue();
				break;
			case MULTIPLY:
				tempVarDouble = lExprValue.getDoubleValue()
						* rExprValue.getDoubleValue();
				break;
			default:
				return null;
			}
			exprValue.setDoubleValue(tempVarDouble);
		} else {
			exprValue.setType(Type.STRING);
			exprValue.setStringValue(lExprValue.getStringValue()
					+ rExprValue.getStringValue());
		}
		return exprValue;
	}

	@Override
	public Symbol visit(Context context, VariableExpression variableExpression)
			throws Exception {
		Symbol tempSymbol = context.getSymbolTable().getSymbol(
				variableExpression.getName());

		if (tempSymbol == null || tempSymbol.isValueNull()) {
			return null;
		}

		return tempSymbol;
	}

	@Override
	public Symbol visit(Context context, NegationExpression negationExpression)
			throws Exception {
		Symbol innerExprValue = negationExpression.getExpression().accept(
				context, this);
		if (innerExprValue == null || innerExprValue.isValueNull()) {
			return null;
		}
		Symbol exprValue = new Symbol(Type.BOOLEAN);
		exprValue.setBooleanValue(!innerExprValue.getBooleanValue());
		return exprValue;
	}

	@Override
	public Symbol visit(Context context, LogicalExpression logicalExpression)
			throws Exception {
		Symbol lExprValue = logicalExpression.getLExpression().accept(context,
				this);
		Symbol rExprValue = logicalExpression.getRExpression().accept(context,
				this);

		if (lExprValue == null || rExprValue == null
				|| lExprValue.isValueNull() || rExprValue.isValueNull()) {
			return null;
		}

		Symbol exprValue = new Symbol(Type.BOOLEAN);

		boolean tempVarBoolean = false;
		switch (logicalExpression.getOperator()) {
		case TOK_AND:
			tempVarBoolean = lExprValue.getBooleanValue()
					&& rExprValue.getBooleanValue();
			break;
		case TOK_OR:
			tempVarBoolean = lExprValue.getBooleanValue()
					|| rExprValue.getBooleanValue();
			break;
		default:
			return null;
		}
		exprValue.setBooleanValue(tempVarBoolean);
		return exprValue;
	}

	@Override
	public Symbol visit(Context context,
			RelationalExpression relationalExpression) throws Exception {
		Symbol lExprValue = relationalExpression.getLExpression().accept(
				context, this);
		Symbol rExprValue = relationalExpression.getRExpression().accept(
				context, this);

		if (lExprValue == null || rExprValue == null
				|| lExprValue.isValueNull() || rExprValue.isValueNull()) {
			return null;
		}

		Symbol exprValue = new Symbol(Type.BOOLEAN);
		Boolean tempVarBoolean = null;
		if (lExprValue.getType() == Type.NUMERIC) {
			switch (relationalExpression.getOperator()) {
			case GT:
				tempVarBoolean = lExprValue.getDoubleValue() > rExprValue
						.getDoubleValue();
				break;
			case GTE:
				tempVarBoolean = lExprValue.getDoubleValue() >= rExprValue
						.getDoubleValue();
				break;
			case LT:
				tempVarBoolean = lExprValue.getDoubleValue() < rExprValue
						.getDoubleValue();
				break;
			case LTE:
				tempVarBoolean = lExprValue.getDoubleValue() <= rExprValue
						.getDoubleValue();
				break;
			case EQ:
				tempVarBoolean = lExprValue.getDoubleValue() == rExprValue
						.getDoubleValue();
				break;
			case NEQ:
				tempVarBoolean = lExprValue.getDoubleValue() != rExprValue
						.getDoubleValue();
				break;
			default:
				return null;
			}
		} else if (lExprValue.getType() == Type.BOOLEAN) {
			switch (relationalExpression.getOperator()) {
			case EQ:
				tempVarBoolean = lExprValue.getBooleanValue() == rExprValue
						.getBooleanValue();
				break;
			case NEQ:
				tempVarBoolean = lExprValue.getBooleanValue() != rExprValue
						.getBooleanValue();
				break;
			default:
				return null;
			}
		} else {
			switch (relationalExpression.getOperator()) {
			case EQ:
				tempVarBoolean = lExprValue.getStringValue().equals(
						rExprValue.getStringValue());
				break;
			case NEQ:
				tempVarBoolean = !lExprValue.getStringValue().equals(
						rExprValue.getStringValue());
				break;
			default:
				return null;
			}
		}

		exprValue.setBooleanValue(tempVarBoolean);
		return exprValue;
	}

	@Override
	public Symbol visit(Context context,
			ProcedureCallExpression procedureCallExpression) throws Exception {
		Procedure procedure = procedureCallExpression.getProcedure();

		// Calls the function by passing the arguements
		return procedure.accept(context, this,
				procedureCallExpression.getAcutalParameterExpressions());
	}

	private Symbol getLocalVariable(Symbol aParameterSymbol,
			Symbol fParameterSymbol) {
		Symbol returnSymbol = new Symbol(fParameterSymbol.getName(),
				fParameterSymbol.getType());

		switch (returnSymbol.getType()) {
		case BOOLEAN:
			returnSymbol.setBooleanValue(aParameterSymbol.getBooleanValue());
			break;
		case NUMERIC:
			returnSymbol.setDoubleValue(aParameterSymbol.getDoubleValue());
			break;
		case STRING:
			returnSymbol.setStringValue(aParameterSymbol.getStringValue());
			break;
		default:
			break;
		}

		return returnSymbol;
	}

	private String getValue(Symbol symbol) {
		switch (symbol.getType()) {
		case BOOLEAN:
			return String.valueOf(symbol.getBooleanValue());
		case NUMERIC:
			return String.valueOf(symbol.getDoubleValue());
		case STRING:
			return String.valueOf(symbol.getStringValue());
		default:
			break;
		}
		return "";
	}

	private Exception runTimeError(int index) {
		String errorMessage = "SLANG RUNTIME ERROR AT LINE: "
				+ parser.getCurrentLine(index);
		return new Exception(errorMessage);
	}
}

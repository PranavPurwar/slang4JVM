package com.slang.ast;

import java.util.ArrayList;

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
import com.slang.ast.statements.AssignmentStatement;
import com.slang.ast.statements.IfStatement;
import com.slang.ast.statements.PrintLineStatement;
import com.slang.ast.statements.PrintStatement;
import com.slang.ast.statements.ReturnStatement;
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

// Visitor interface 
public interface Visitor {
	Symbol visit(Context context, Module module,
			ArrayList<Expression> cmdLineArguments) throws Exception;

	Symbol visit(Context context, Closure closure,
			ArrayList<Expression> actualParameters) throws Exception;

	Symbol visit(Context context, PrintStatement printStatement)
			throws Exception;

	Symbol visit(Context context, PrintLineStatement printLineStatement)
			throws Exception;

	Symbol visit(Context context,
			VariableDeclarationStatement variableDeclarationStatement)
			throws Exception;

	Symbol visit(Context context, AssignmentStatement assignmentStatement)
			throws Exception;

	Symbol visit(Context context, IfStatement ifStatement) throws Exception;

	Symbol visit(Context context, WhileStatement whileStatement)
			throws Exception;

	Symbol visit(Context context, ReturnStatement returnStatement)
			throws Exception;

	Symbol visit(Context context,
			NumericConstantExpression numericConstantExpression)
			throws Exception;

	Symbol visit(Context context,
			BooleanConstantExpression booleanConstantExpression)
			throws Exception;

	Symbol visit(Context context,
			StringLiteralExpression stringLiteralExpression) throws Exception;

	Symbol visit(Context context, BinaryExpression binaryExpression)
			throws Exception;

	Symbol visit(Context context, UnaryExpression unaryExpression)
			throws Exception;

	Symbol visit(Context context, VariableExpression variableExpression)
			throws Exception;

	Symbol visit(Context context, LogicalExpression logicalExpression)
			throws Exception;

	Symbol visit(Context context, RelationalExpression relationalExpression)
			throws Exception;

	Symbol visit(Context context, NegationExpression negationExpression)
			throws Exception;

	Symbol visit(Context context,
			ProcedureCallExpression procedureCallExpression) throws Exception;
}

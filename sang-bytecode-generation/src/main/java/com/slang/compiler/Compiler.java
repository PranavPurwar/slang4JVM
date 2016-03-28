package com.slang.compiler;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

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
import com.slang.ast.statements.AssignmentStatement;
import com.slang.ast.statements.IfStatement;
import com.slang.ast.statements.PrintLineStatement;
import com.slang.ast.statements.PrintStatement;
import com.slang.ast.statements.ReturnStatement;
import com.slang.ast.statements.Statement;
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.contexts.Context;
import com.slang.contexts.ByteCodeGenerationContext;
import com.slang.contexts.Symbol;

// Generates the byte code by visiting ast nodes
public class Compiler implements Visitor {

	public Compiler() {
	}

	@Override
	public Symbol visit(Context superContext, Module module,
			ArrayList<Expression> cmdLineArguments) throws Exception {
		String className = module.getName().toUpperCase() + ".class";
		FileOutputStream outputStream = new FileOutputStream(className);

		// Creates bytecode generation context for module
		ByteCodeGenerationContext context = new ByteCodeGenerationContext(
				module, outputStream);

		// Iterates through the function and generates equivalent byte code
		Iterator<Procedure> i = module.getProcedures().values().iterator();
		while (i.hasNext()) {
			Procedure procedure = i.next();
			// Visits the procedure node
			procedure.accept(context, this, null);
		}

		// Generates bytecode and writes it file
		context.generate(outputStream);
		return null;
	}

	@Override
	public Symbol visit(Context callerFnContext, Closure closure,
			ArrayList<Expression> actualParameters) throws Exception {
		ArrayList<Symbol> formalParameters = closure.getFormalParameters();

		// Sets the return type, argument types and argument names for BCEL API
		Type returnType = getBCELType(closure.getType());
		Type[] argTypes;
		String[] argNames;
		if (formalParameters.size() == 0) {
			argTypes = Type.NO_ARGS;
			argNames = new String[] {};
		} else {
			argTypes = new Type[formalParameters.size()];
			argNames = new String[formalParameters.size()];
			for (int i = 0; i < argTypes.length; i++) {
				argTypes[i] = getBCELType(formalParameters.get(i).getType());
				argNames[i] = "arg" + i;
			}
		}

		// Creates bytecode generation context for current function
		ByteCodeGenerationContext thisFnContext = new ByteCodeGenerationContext(
				(ByteCodeGenerationContext) callerFnContext, closure.getName()
						.toLowerCase(), returnType, argTypes, argNames);

		// Adds formal parameters to symbol table of current context
		for (Symbol symbol : formalParameters) {
			storeSymbol(thisFnContext, symbol, false);
		}

		// Visits all statements and generates byte code
		for (Statement statement : closure.getStatements()) {
			statement.accept(thisFnContext, this);
		}

		// Appends return statement for main function as Slang do not have
		// support for void type
		if (closure.getName().equalsIgnoreCase("MAIN")) {
			thisFnContext.getInstructionList().append(
					InstructionFactory.createReturn(Type.VOID));
		}

		// Sets the max stack size & the max number of local variables
		thisFnContext.getMethodGen().setMaxStack();
		thisFnContext.getMethodGen().setMaxLocals();

		// Generates method and adds it to class generator
		thisFnContext.getClassGen().addMethod(
				thisFnContext.getMethodGen().getMethod());

		// Disposes instruction lists (For enabling reuse of instruction
		// handles)
		thisFnContext.getInstructionList().dispose();
		return null;
	}

	@Override
	public Symbol visit(Context ctx, PrintStatement printStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Access 'out' static object of class 'System'
		context.getInstructionList().append(
				context.getInstructionFactory().createFieldAccess(
						"java.lang.System", "out",
						new ObjectType("java.io.PrintStream"),
						Constants.GETSTATIC));

		// Visits the expression node and generates equivalent byte code
		com.slang.ast.meta.DataType exprType = printStatement.getExpression()
				.getType();
		printStatement.getExpression().accept(ctx, this);

		// Invokes 'print' function of 'out' static object of class 'System'
		context.getInstructionList().append(
				context.getInstructionFactory().createInvoke(
						"java.io.PrintStream", "print", Type.VOID,
						new Type[] { getBCELType(exprType) },
						Constants.INVOKEVIRTUAL));
		return null;
	}

	@Override
	public Symbol visit(Context ctx, PrintLineStatement printLineStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Access 'out' static object of class 'System'
		context.getInstructionList().append(
				context.getInstructionFactory().createFieldAccess(
						"java.lang.System", "out",
						new ObjectType("java.io.PrintStream"),
						Constants.GETSTATIC));

		// Visits the expression node and generates equivalent byte code
		com.slang.ast.meta.DataType exprType = printLineStatement.getExpression()
				.getType();
		printLineStatement.getExpression().accept(ctx, this);

		// Invokes 'println' function of 'out' static object of class 'System'
		context.getInstructionList().append(
				context.getInstructionFactory().createInvoke(
						"java.io.PrintStream", "println", Type.VOID,
						new Type[] { getBCELType(exprType) },
						Constants.INVOKEVIRTUAL));
		return null;
	}

	@Override
	public Symbol visit(Context ctx, BinaryExpression bExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Gets the type of the expression
		com.slang.ast.meta.DataType lExprType = bExpression.getLExpression()
				.getType();

		// Decides operation based on the type
		if (lExprType == com.slang.ast.meta.DataType.NUMERIC) {
			// Visits expression nodes
			bExpression.getLExpression().accept(context, this);
			bExpression.getRExpression().accept(context, this);

			switch (bExpression.getOperator()) {
			case PLUS:
				context.getInstructionList().append(InstructionConstants.DADD);
				return null;
			case MINUS:
				context.getInstructionList().append(InstructionConstants.DSUB);
				return null;
			case DIVIDE:
				context.getInstructionList().append(InstructionConstants.DDIV);
				return null;
			case MULTIPLY:
				context.getInstructionList().append(InstructionConstants.DMUL);
				return null;
			default:
				return null;
			}
		} else {
			// If expressions are of String type, string concating is performed
			context.getInstructionList().append(
					context.getInstructionFactory().createNew(
							"java.lang.StringBuilder"));
			context.getInstructionList().append(InstructionConstants.DUP);

			bExpression.getLExpression().accept(context, this);
			context.getInstructionList().append(
					context.getInstructionFactory()
							.createInvoke("java.lang.StringBuilder", "<init>",
									Type.VOID, new Type[] { Type.STRING },
									Constants.INVOKESPECIAL));

			bExpression.getRExpression().accept(context, this);
			context.getInstructionList().append(
					context.getInstructionFactory()
							.createInvoke("java.lang.StringBuilder", "append",
									new ObjectType("java.lang.StringBuilder"),
									new Type[] { Type.STRING },
									Constants.INVOKEVIRTUAL));

			context.getInstructionList().append(
					context.getInstructionFactory().createInvoke(
							"java.lang.StringBuilder", "toString", Type.STRING,
							Type.NO_ARGS, Constants.INVOKEVIRTUAL));
			return null;
		}
	}

	@Override
	public Symbol visit(Context ctx, UnaryExpression uExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		switch (uExpression.getOperator()) {
		case PLUS:
			uExpression.getExpression().accept(context, this);
			return null;
		case MINUS:
			uExpression.getExpression().accept(context, this);
			context.getInstructionList().append(InstructionConstants.DNEG);
			return null;
		default:
			break;
		}
		return null;
	}

	@Override
	public Symbol visit(Context ctx, VariableExpression variableExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		Symbol varSymbol = context.getSymbolTable().getSymbol(
				variableExpression.getName());

		// Loads the memory location with the byte code index
		context.getInstructionList().append(
				InstructionFactory.createLoad(getBCELType(varSymbol.getType()),
						varSymbol.getIndex()));
		return null;
	}

	@Override
	public Symbol visit(Context ctx, AssignmentStatement assignmentStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Visits the expression and loads the value into the stack
		assignmentStatement.getExpression().accept(ctx, this);

		// Stores the value in the stack to the variable
		Symbol varSymbol = context.getSymbolTable().getSymbol(
				assignmentStatement.getVariableExpression().getName());
		context.getInstructionList()
				.append(InstructionFactory.createStore(
						getBCELType(varSymbol.getType()), varSymbol.getIndex()));
		return null;
	}

	@Override
	public Symbol visit(Context ctx,
			NumericConstantExpression numericConstantExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Pushes constant into the stack
		context.getInstructionList()
				.append(new PUSH(context.getConstantPoolGen(),
						numericConstantExpression.getSymbol().getDoubleValue()));

		return null;
	}

	@Override
	public Symbol visit(Context ctx,
			StringLiteralExpression stringLiteralExpression) throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Pushes constant into the stack
		context.getInstructionList().append(
				new PUSH(context.getConstantPoolGen(), stringLiteralExpression
						.getSymbol().getStringValue()));
		return null;
	}

	@Override
	public Symbol visit(Context ctx,
			BooleanConstantExpression booleanConstantExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Pushes constant into the stack
		context.getInstructionList()
				.append(new PUSH(context.getConstantPoolGen(),
						booleanConstantExpression.getSymbol().getBooleanValue()));
		return null;
	}

	@Override
	public Symbol visit(Context ctx, LogicalExpression logicalExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		logicalExpression.getLExpression().accept(context, this);
		logicalExpression.getRExpression().accept(context, this);

		switch (logicalExpression.getOperator()) {
		case TOK_AND:
			context.getInstructionList().append(InstructionConstants.IAND);
			return null;
		case TOK_OR:
			context.getInstructionList().append(InstructionConstants.IOR);
			return null;
		default:
			return null;
		}
	}

	@Override
	public Symbol visit(Context ctx, RelationalExpression relationalExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		com.slang.ast.meta.DataType type = relationalExpression.getLExpression()
				.getType();

		relationalExpression.getLExpression().accept(context, this);
		relationalExpression.getRExpression().accept(context, this);

		if (type == com.slang.ast.meta.DataType.NUMERIC) {
			switch (relationalExpression.getOperator()) {
			case LT:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), -1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1));
				context.getInstructionList().append(InstructionConstants.IXOR);
				return null;
			case LTE:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), -1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1));
				context.getInstructionList().append(InstructionConstants.IXOR);
				return null;
			case GT:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), -1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				return null;
			case GTE:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				context.getInstructionList().append(InstructionConstants.I2D);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), -1.0));
				context.getInstructionList().append(InstructionConstants.DCMPG);
				return null;
			case EQ:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				BranchInstruction ifneBranch_1 = InstructionFactory
						.createBranchInstruction(Constants.IFNE, null);
				context.getInstructionList().append(ifneBranch_1);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1));
				BranchInstruction gotoBranch_1 = InstructionFactory
						.createBranchInstruction(Constants.GOTO, null);
				context.getInstructionList().append(gotoBranch_1);
				InstructionHandle ifneTarget_1 = context.getInstructionList()
						.append(new PUSH(context.getConstantPoolGen(), 0));
				InstructionHandle gotoTarget_1 = context.getInstructionList()
						.append(new PUSH(context.getConstantPoolGen(), 1));
				context.getInstructionList().append(InstructionConstants.IAND);
				ifneBranch_1.setTarget(ifneTarget_1);
				gotoBranch_1.setTarget(gotoTarget_1);
				return null;
			case NEQ:
				context.getInstructionList().append(InstructionConstants.DCMPG);
				BranchInstruction ifneBranch_2 = InstructionFactory
						.createBranchInstruction(Constants.IFNE, null);
				context.getInstructionList().append(ifneBranch_2);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 0));
				BranchInstruction gotoBranch_2 = InstructionFactory
						.createBranchInstruction(Constants.GOTO, null);
				context.getInstructionList().append(gotoBranch_2);
				InstructionHandle ifneTarget_2 = context.getInstructionList()
						.append(new PUSH(context.getConstantPoolGen(), 1));
				InstructionHandle gotoTarget_2 = context.getInstructionList()
						.append(new PUSH(context.getConstantPoolGen(), 0));
				context.getInstructionList().append(InstructionConstants.IOR);
				ifneBranch_2.setTarget(ifneTarget_2);
				gotoBranch_2.setTarget(gotoTarget_2);
				return null;
			default:
				return null;
			}
		} else if (type == com.slang.ast.meta.DataType.BOOLEAN) {
			switch (relationalExpression.getOperator()) {
			case EQ:
				context.getInstructionList().append(InstructionConstants.IXOR);
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1));
				context.getInstructionList().append(InstructionConstants.IXOR);
				return null;
			case NEQ:
				context.getInstructionList().append(InstructionConstants.IXOR);
				return null;
			default:
				return null;
			}
		} else {
			switch (relationalExpression.getOperator()) {
			case EQ:
				context.getInstructionList().append(
						context.getInstructionFactory().createInvoke(
								"java.lang.String", "equals", Type.BOOLEAN,
								new Type[] { Type.OBJECT },
								Constants.INVOKEVIRTUAL));
				return null;

			case NEQ:
				context.getInstructionList().append(
						context.getInstructionFactory().createInvoke(
								"java.lang.String", "equals", Type.BOOLEAN,
								new Type[] { Type.OBJECT },
								Constants.INVOKEVIRTUAL));
				context.getInstructionList().append(
						new PUSH(context.getConstantPoolGen(), 1));
				context.getInstructionList().append(InstructionConstants.IXOR);
				return null;

			default:
				return null;
			}

		}

	}

	@Override
	public Symbol visit(Context ctx, NegationExpression negationExpression)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		negationExpression.getExpression().accept(context, this);
		context.getInstructionList().append(
				new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.IXOR);

		return null;
	}

	@Override
	public Symbol visit(Context ctx, IfStatement ifStatement) throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		ifStatement.getConditionExpression().accept(context, this);

		// Creates branching instruction, if expression is false branch
		// instruction to set target
		BranchInstruction ifCond_branch = InstructionFactory
				.createBranchInstruction(Constants.IFEQ, null);
		context.getInstructionList().append(ifCond_branch);

		for (Statement statement : ifStatement.getTruePartStatements()) {
			statement.accept(context, this);
		}

		// Creates go to branch, avoids the false if condition is true
		BranchInstruction ifCondGoTo_branch = InstructionFactory
				.createBranchInstruction(Constants.GOTO, null);
		context.getInstructionList().append(ifCondGoTo_branch);

		// Handle to the false part statements
		InstructionHandle falsePartBegin_handle = context.getInstructionList()
				.append(new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.POP);
		if (ifStatement.getFalsePartStatements() != null) {
			for (Statement statement : ifStatement.getFalsePartStatements()) {
				statement.accept(context, this);
			}
		}

		// Handle to the end of false part
		InstructionHandle falsePartEnd_handle = context.getInstructionList()
				.append(new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.POP);

		ifCond_branch.setTarget(falsePartBegin_handle);
		ifCondGoTo_branch.setTarget(falsePartEnd_handle);

		return null;
	}

	@Override
	public Symbol visit(Context ctx, WhileStatement whileStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Go to branch to condition evaluation
		BranchInstruction gotoCond_branch = InstructionFactory
				.createBranchInstruction(Constants.GOTO, null);
		context.getInstructionList().append(gotoCond_branch);

		// Handle for beginning of statements
		InstructionHandle beginStatements_handle = context.getInstructionList()
				.append(new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.POP);
		for (Statement statement : whileStatement.getStatements()) {
			statement.accept(context, this);
		}

		// Condition evaluation
		InstructionHandle cond_handle = context.getInstructionList().append(
				new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.POP);
		whileStatement.getConditionExpression().accept(context, this);
		context.getInstructionList().append(
				new PUSH(context.getConstantPoolGen(), 1));
		context.getInstructionList().append(InstructionConstants.IXOR);
		// Branches to statements if condition is true
		BranchInstruction checkCond_branch = InstructionFactory
				.createBranchInstruction(Constants.IFEQ, null);
		context.getInstructionList().append(checkCond_branch);

		gotoCond_branch.setTarget(cond_handle);
		checkCond_branch.setTarget(beginStatements_handle);

		return null;

	}

	@Override
	public Symbol visit(Context ctx, ReturnStatement returnStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		returnStatement.getExpression().accept(context, this);

		// Loads the memory location corresponding to the symbol
		context.getInstructionList().append(
				InstructionFactory.createReturn(getBCELType(returnStatement
						.getExpression().getType())));
		return null;
	}

	@Override
	public Symbol visit(Context ctx,
			VariableDeclarationStatement variableDeclarationStatement)
			throws Exception {
		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Creates symbol in symbol table and byte code for corresponding
		// variable
		Symbol varSymbol = new Symbol(
				variableDeclarationStatement.getVariableName(),
				variableDeclarationStatement.getVariableType());
		storeSymbol(context, varSymbol, true);
		return null;
	}

	@Override
	public Symbol visit(Context ctx,
			ProcedureCallExpression procedureCallExpression) throws Exception {
		Procedure procedure = procedureCallExpression.getProcedure();
		ArrayList<Symbol> formalParameters = procedureCallExpression
				.getProcedure().getFormalParameters();
		ArrayList<Expression> actualParameters = procedureCallExpression
				.getAcutalParameterExpressions();

		ByteCodeGenerationContext context = (ByteCodeGenerationContext) ctx;

		// Sets the return type
		Type returnType = getBCELType(procedureCallExpression.getType());

		// Sets the parameter type array
		Type[] typeParameterArray = new Type[formalParameters.size()];
		for (int i = 0; i < typeParameterArray.length; i++) {
			typeParameterArray[i] = getBCELType(formalParameters.get(i)
					.getType());
			actualParameters.get(i).accept(context, this);
		}

		// Invokes the function
		context.getInstructionList().append(
				context.getInstructionFactory().createInvoke(
						context.getModule().getName().toUpperCase(),
						procedure.getName().toLowerCase(), returnType,
						typeParameterArray, Constants.INVOKESTATIC));
		return null;
	}

	// Function to add symbol to context and byte code
	private void storeSymbol(ByteCodeGenerationContext context,
			Symbol varSymbol, boolean isLocalVariable) {
		// Sets byte code index in symbol
		varSymbol.setIndex(context.getVariableIndexOffset());

		// creates variable according to the type and updates index
		switch (varSymbol.getType()) {
		case NUMERIC:
			// Function parameters automatically added by BCEL
			if (isLocalVariable) {
				context.getInstructionList().append(
						InstructionFactory.createNull(Type.DOUBLE));
				context.getInstructionList().append(
						InstructionFactory.createStore(Type.DOUBLE,
								varSymbol.getIndex()));
			}
			context.updateVariableIndexOffset(varSymbol.getIndex() + 2);
			break;
		case STRING:
			if (isLocalVariable) {
				context.getInstructionList().append(
						InstructionFactory.createNull(Type.OBJECT));
				context.getInstructionList().append(
						InstructionFactory.createStore(Type.OBJECT,
								varSymbol.getIndex()));
			}
			context.updateVariableIndexOffset(varSymbol.getIndex() + 1);
			break;
		case BOOLEAN:
			if (isLocalVariable) {
				context.getInstructionList().append(
						InstructionFactory.createNull(Type.BOOLEAN));
				context.getInstructionList().append(
						InstructionFactory.createStore(Type.INT,
								varSymbol.getIndex()));
			}
			context.updateVariableIndexOffset(varSymbol.getIndex() + 1);
			break;
		default:
			break;
		}
		// Adds symbol to symbol table
		context.getSymbolTable().addSymbol(varSymbol);
	}

	// Function which returns BCEL type corresponding to slang type
	private Type getBCELType(com.slang.ast.meta.DataType type) {
		switch (type) {
		case NUMERIC:
			return Type.DOUBLE;
		case STRING:
			return Type.STRING;
		case BOOLEAN:
			return Type.BOOLEAN;
		default:
			return Type.VOID;
		}
	}
}

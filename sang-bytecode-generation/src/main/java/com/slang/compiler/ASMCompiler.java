package com.slang.compiler;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.slang.ast.Closure;
import com.slang.ast.Module;
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
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.bytecode.util.ByteCodeUtils;
import com.slang.contexts.Context;
import com.slang.contexts.Symbol;

public class ASMCompiler implements Visitor, Opcodes {

	private ClassWriter cw;
	private MethodVisitor mv;
	public ASMCompiler() {
		cw = new ClassWriter(0);
		cw.visit(V1_7, ACC_PUBLIC, "Slang", null, "java/lang/Object", null);
	}
	
	private ByteCodeUtils utils = ByteCodeUtils.getInstance();
	@Override
	public Symbol visit(Context context, Module module, ArrayList<Expression> cmdLineArguments) throws Exception {
		for(Closure closure : module.getProcedures().values()){
			mv = cw.visitMethod(ACC_PUBLIC, closure.getName(),
					utils.getDescription(closure.getType(), closure.getFormalParameters()), null, null);
			closure.accept(context, this, null);
			mv.visitEnd();
		}
		return null;
	}

	@Override
	public Symbol visit(Context context, Closure closure, ArrayList<Expression> actualParameters) throws Exception {
//		mv.v
		return null;
	}

	@Override
	public Symbol visit(Context context, PrintStatement printStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, PrintLineStatement printLineStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, VariableDeclarationStatement variableDeclarationStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, AssignmentStatement assignmentStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, IfStatement ifStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, WhileStatement whileStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, ReturnStatement returnStatement) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, NumericConstantExpression numericConstantExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, BooleanConstantExpression booleanConstantExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, StringLiteralExpression stringLiteralExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, BinaryExpression binaryExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, UnaryExpression unaryExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, VariableExpression variableExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, LogicalExpression logicalExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, RelationalExpression relationalExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, NegationExpression negationExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol visit(Context context, ProcedureCallExpression procedureCallExpression) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

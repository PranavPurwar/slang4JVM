package com.slang.frontend;

import java.util.ArrayList;

import com.slang.ast.Module;
import com.slang.ast.Procedure;
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
import com.slang.ast.meta.Type;
import com.slang.ast.statements.AssignmentStatement;
import com.slang.ast.statements.IfStatement;
import com.slang.ast.statements.PrintLineStatement;
import com.slang.ast.statements.PrintStatement;
import com.slang.ast.statements.ReturnStatement;
import com.slang.ast.statements.Statement;
import com.slang.ast.statements.VariableDeclarationStatement;
import com.slang.ast.statements.WhileStatement;
import com.slang.builders.ModuleBuilder;
import com.slang.builders.ProcedureBuilder;
import com.slang.contexts.ParserContext;
import com.slang.contexts.Symbol;

public class RDParser extends Lexer {
	Token currentToken;
	Token lastToken;
	ModuleBuilder moduleBuilder;

	public RDParser(String name, String code) {
		super(code);
		moduleBuilder = new ModuleBuilder(name);
	}

	// Entry point to the parser
	public Module parse() throws Exception {
		getNext();
		return parseFunctions();
	}

	// Parses all the functions and builds the module
	private Module parseFunctions() throws Exception {
		while (currentToken == Token.TOK_FUNCTION) {
			Procedure procedure = parseFunction();
			if (procedure == null) {
				throw syntaxError();
			}
			moduleBuilder.addProcedure(procedure);
			getNext();
		}
		return moduleBuilder.build();
	}

	// Parses and builds function
	private Procedure parseFunction() throws Exception {
		ProcedureBuilder procedureBuilder = new ProcedureBuilder(
				new ParserContext());
		procedureBuilder.setIndex(getIndex());
		if (currentToken != Token.TOK_FUNCTION) {
			throw syntaxError();
		}
		getNext();

		// Checking and setting the return type
		Type returnType = getType(currentToken);
		if (returnType == null) {
			throw syntaxError();
		}
		procedureBuilder.setReturnType(returnType);

		// Parsing the name of the function
		getNext();
		if (currentToken != Token.TOK_UNQUOTED_STRING) {
			throw syntaxError();
		}
		procedureBuilder.setProcedureName(getString());

		getNext();
		if (currentToken != Token.TOK_OPEN_PAREN) {
			throw syntaxError();
		}

		// Parse argument list
		ArrayList<Symbol> fParameters = parseFunctionParameters();
		procedureBuilder.setFormalParameters(fParameters);
		if (currentToken != Token.TOK_CLOSED_PAREN) {
			throw syntaxError();
		}

		// Parsing the function code
		getNext();
		ArrayList<Statement> statements = getStatementList(procedureBuilder);
		procedureBuilder.setStatements(statements);

		if (currentToken != Token.TOK_END) {
			throw syntaxError();
		}

		return procedureBuilder.build();
	}

	// Parses function parameters
	private ArrayList<Symbol> parseFunctionParameters() throws Exception {
		ArrayList<Symbol> fParameters = new ArrayList<Symbol>();
		getNext();
		while (currentToken == Token.TOK_VAR_BOOL
				|| currentToken == Token.TOK_VAR_NUMBER
				|| currentToken == Token.TOK_VAR_STRING) {

			Type type = getType(currentToken);
			getNext();
			if (currentToken != Token.TOK_UNQUOTED_STRING) {
				throw syntaxError();
			}
			String name = getString();
			Symbol symbol = new Symbol(name, type);
			fParameters.add(symbol);
			getNext();
			if (currentToken != Token.TOK_COMMA) {
				break;
			}
			getNext();
		}
		return fParameters;
	}

	// Parses and returns statement list
	private ArrayList<Statement> getStatementList(ProcedureBuilder pBuilder)
			throws Exception {
		ArrayList<Statement> statements = new ArrayList<Statement>();
		while (currentToken != Token.TOK_NULL && currentToken != Token.TOK_ELSE
				&& currentToken != Token.TOK_ENDIF
				&& currentToken != Token.TOK_WEND
				&& currentToken != Token.TOK_END) {
			Statement s = parseStatement(pBuilder);
			if (s != null) {
				statements.add(s);
			}
		}
		return statements;
	}

	// Parses statement
	private Statement parseStatement(ProcedureBuilder pBuilder)
			throws Exception {
		Statement statement = null;
		int tempVarIndex = getIndex();
		switch (currentToken) {
		case TOK_VAR_STRING:
		case TOK_VAR_NUMBER:
		case TOK_VAR_BOOL:
			statement = parseVariableDeclarationStatement(pBuilder);
			getNext();
			break;
		case TOK_PRINT:
			statement = parsePrintStatement(pBuilder);
			getNext();
			break;
		case TOK_PRINTLN:
			statement = parsePrintLineStatement(pBuilder);
			getNext();
			break;
		case TOK_UNQUOTED_STRING:
			statement = parseAssignmentStatement(pBuilder);
			getNext();
			break;
		case TOK_IF:
			statement = parseIfStatement(pBuilder);
			getNext();
			break;
		case TOK_WHILE:
			statement = parseWhileStatement(pBuilder);
			getNext();
			break;
		case TOK_RETURN:
			statement = parseReturnStatement(pBuilder);
			getNext();
			break;
		default:
			throw syntaxError();
		}
		statement.setIndex(tempVarIndex);
		return statement;
	}

	// <LExpr> ::= <RExpr> { && | ||} <LExpr>
	private Expression parseExpression(ProcedureBuilder pBuilder)
			throws Exception {
		Expression expression = parseRExpression(pBuilder);
		while (currentToken == Token.TOK_AND || currentToken == Token.TOK_OR) {
			Token operatorToken = currentToken;
			getNext();
			Expression optionalExpression = parseExpression(pBuilder);
			expression = new LogicalExpression(expression, optionalExpression,
					operatorToken);
		}
		return expression;
	}

	// <RExpr> ::= <SExpr> { > | < | >= | <= | <> | == } <SExpr>
	private Expression parseRExpression(ProcedureBuilder pBuilder)
			throws Exception {
		Expression expression = parseSExpression(pBuilder);
		if (currentToken == Token.TOK_GT || currentToken == Token.TOK_LT
				|| currentToken == Token.TOK_GTE
				|| currentToken == Token.TOK_LTE
				|| currentToken == Token.TOK_NEQ
				|| currentToken == Token.TOK_EQ) {
			Token operatorToken = currentToken;
			getNext();
			Expression optionalExpression = parseSExpression(pBuilder);
			expression = new RelationalExpression(expression,
					optionalExpression, getOperator(operatorToken));
		}
		return expression;
	}

	// <SExpr> ::= <Term> { + | - } <SExpr>
	private Expression parseSExpression(ProcedureBuilder pBuilder)
			throws Exception {
		Expression expression = parseTerm(pBuilder);
		while (currentToken == Token.TOK_PLUS
				|| currentToken == Token.TOK_MINUS) {
			Token operatorToken = currentToken;
			getNext();
			Expression optionalExpression = parseSExpression(pBuilder);
			expression = new BinaryExpression(expression, optionalExpression,
					getOperator(operatorToken));
		}
		return expression;
	}

	// <Term> ::= <Factor> | <Factor> { * | / } <Term>
	private Expression parseTerm(ProcedureBuilder pBuilder) throws Exception {
		Expression termExpr = parseFactor(pBuilder);
		while (currentToken == Token.TOK_MULTIPLY
				|| currentToken == Token.TOK_DIVIDE) {
			Token operatorToken = currentToken;
			getNext();
			Expression optionalTerm = parseTerm(pBuilder);
			termExpr = new BinaryExpression(termExpr, optionalTerm,
					getOperator(operatorToken));
		}
		return termExpr;
	}

	// <Factor> ::= NUMBER | STRING | TRUE | FALSE | VARIABLE | (<Expr>) |
	// { + | - } <Factor>
	private Expression parseFactor(ProcedureBuilder pBuilder) throws Exception {
		Expression factorExpr = null;
		if (currentToken == Token.TOK_NUMERIC) {
			factorExpr = new NumericConstantExpression(getNumber());
			getNext();
		} else if (currentToken == Token.TOK_STRING) {
			factorExpr = new StringLiteralExpression(getString());
			getNext();
		} else if (currentToken == Token.TOK_BOOL_TRUE
				|| currentToken == Token.TOK_BOOL_FALSE) {
			boolean tempVarBoolean = (currentToken == Token.TOK_BOOL_TRUE) ? true
					: false;
			factorExpr = new BooleanConstantExpression(tempVarBoolean);
			getNext();
		} else if (currentToken == Token.TOK_OPEN_PAREN) {
			getNext();
			factorExpr = parseExpression(pBuilder);
			if (currentToken != Token.TOK_CLOSED_PAREN) {
				String errorMessage = "Slang Compile Time Error - Missing ')' at line:"
						+ getPreviousLine(getIndex());
				throw new Exception(errorMessage);
			}
			getNext();
		} else if (currentToken == Token.TOK_PLUS
				|| currentToken == Token.TOK_MINUS) {
			Token operatorToken = currentToken;
			getNext();
			Expression tempVarExpr = parseFactor(pBuilder);
			factorExpr = new UnaryExpression(tempVarExpr,
					getOperator(operatorToken));
		} else if (currentToken == Token.TOK_NEGATION) {
			getNext();
			Expression tempVarExpr = parseFactor(pBuilder);
			factorExpr = new NegationExpression(tempVarExpr);
		} else if (currentToken == Token.TOK_UNQUOTED_STRING) {
			String name = getString();
			// If unquoted string is a procedure, parses the procedure call
			// expression
			if (moduleBuilder.isProcedure(name)
					|| name.equals(pBuilder.getProcedureName())) {
				return parseProcedureCallExpression(pBuilder, name);
			} else {
				Symbol symbol = pBuilder.getContext().getSymbolTable()
						.getSymbol(name);
				if (symbol == null) {
					throw syntaxError();
				}
				getNext();
				factorExpr = new VariableExpression(symbol.getName(),
						symbol.getType());
			}
		} else {
			throw syntaxError();
		}

		return factorExpr;
	}

	private Expression parseProcedureCallExpression(ProcedureBuilder pBuilder,
			String procedureName) throws Exception {
		Procedure procedure = moduleBuilder.findProcedure(procedureName);
		getNext();
		if (currentToken != Token.TOK_OPEN_PAREN) {
			throw syntaxError();
		}
		ArrayList<Expression> aParameterExpressions = new ArrayList<Expression>();
		do {
			getNext();
			Expression expression = parseExpression(pBuilder);
			aParameterExpressions.add(expression);
		} while (currentToken == Token.TOK_COMMA);
		if (currentToken != Token.TOK_CLOSED_PAREN) {
			throw syntaxError();
		}
		getNext();

		// If procedure is not already found, it is recursive call
		if (procedure != null) {
			return new ProcedureCallExpression(procedure, aParameterExpressions);
		} else {
			return new ProcedureCallExpression(procedureName,
					aParameterExpressions);
		}
	}

	private Statement parsePrintStatement(ProcedureBuilder pBuilder)
			throws Exception {
		getNext();
		Expression e = parseExpression(pBuilder);
		if (currentToken != Token.TOK_SEMI) {
			throw syntaxError();
		}
		return new PrintStatement(e);
	}

	private Statement parsePrintLineStatement(ProcedureBuilder pBuilder)
			throws Exception {
		getNext();
		Expression e = parseExpression(pBuilder);
		if (currentToken != Token.TOK_SEMI) {
			throw syntaxError();
		}
		return new PrintLineStatement(e);

	}

	private Statement parseVariableDeclarationStatement(
			ProcedureBuilder pBuilder) throws Exception {
		Token typeToken = currentToken;
		getNext();

		if (currentToken == Token.TOK_UNQUOTED_STRING) {
			if (pBuilder.getContext().getSymbolTable().getSymbol(getString()) != null) {
				throw syntaxError();
			}
			Symbol symbol = new Symbol();
			symbol.setName(getString());
			symbol.setType(getType(typeToken));
			getNext();
			if (currentToken == Token.TOK_SEMI) {
				pBuilder.getContext().getSymbolTable().addSymbol(symbol);
				return new VariableDeclarationStatement(symbol.getName(),
						symbol.getType());
			} else {
				throw syntaxError();
			}
		} else {
			throw syntaxError();
		}
	}

	private Statement parseAssignmentStatement(ProcedureBuilder pBuilder)
			throws Exception {
		String variableName = getString();
		Symbol symbol = pBuilder.getContext().getSymbolTable()
				.getSymbol(variableName);
		if (symbol == null) {
			throw syntaxError();
		}
		VariableExpression variable = new VariableExpression(symbol.getName(),
				symbol.getType());

		getNext();
		if (currentToken != Token.TOK_ASSIGN) {
			String errorMessage = "Slang Compile Time Error - Missing '=' at line: "
					+ getPreviousLine(getIndex());
			throw new Exception(errorMessage);
		}

		getNext();
		Expression expression = parseExpression(pBuilder);

		if (currentToken != Token.TOK_SEMI) {
			throw syntaxError();
		}
		return new AssignmentStatement(variable, expression, symbol);
	}

	private Statement parseIfStatement(ProcedureBuilder pBuilder)
			throws Exception {
		ArrayList<Statement> truePartStatements = null;
		ArrayList<Statement> falsePartStatements = null;
		getNext();
		if (currentToken != Token.TOK_OPEN_PAREN) {
			throw syntaxError();
		}
		getNext();
		Expression condExpr = parseExpression(pBuilder);
		if (currentToken != Token.TOK_CLOSED_PAREN) {
			throw syntaxError();
		}
		getNext();
		if (currentToken != Token.TOK_THEN) {
			throw syntaxError();
		}
		getNext();
		truePartStatements = getStatementList(pBuilder);
		if (currentToken == Token.TOK_ENDIF) {
			return new IfStatement(condExpr, truePartStatements,
					falsePartStatements);
		}
		if (currentToken != Token.TOK_ELSE) {
			throw syntaxError();
		}
		getNext();
		falsePartStatements = getStatementList(pBuilder);
		return new IfStatement(condExpr, truePartStatements,
				falsePartStatements);
	}

	private Statement parseWhileStatement(ProcedureBuilder pBuilder)
			throws Exception {
		getNext();
		if (currentToken != Token.TOK_OPEN_PAREN) {
			throw syntaxError();
		}
		getNext();
		Expression condExpr = parseExpression(pBuilder);
		if (currentToken != Token.TOK_CLOSED_PAREN) {
			throw syntaxError();
		}
		getNext();
		ArrayList<Statement> statements = getStatementList(pBuilder);
		if (currentToken != Token.TOK_WEND) {
			throw syntaxError();
		}
		return new WhileStatement(condExpr, statements);
	}

	private Statement parseReturnStatement(ProcedureBuilder pBuilder)
			throws Exception {
		getNext();
		Expression expression = parseExpression(pBuilder);
		if (currentToken != Token.TOK_SEMI) {
			throw syntaxError();
		}
		return new ReturnStatement(expression);
	}

	private Token getNext() {
		lastToken = currentToken;
		currentToken = getToken();
		return currentToken;
	}

	private Operator getOperator(Token operatorToken) {
		switch (operatorToken) {
		case TOK_PLUS:
			return Operator.PLUS;
		case TOK_MINUS:
			return Operator.MINUS;
		case TOK_MULTIPLY:
			return Operator.MULTIPLY;
		case TOK_DIVIDE:
			return Operator.DIVIDE;
		case TOK_LT:
			return Operator.LT;
		case TOK_LTE:
			return Operator.LTE;
		case TOK_GT:
			return Operator.GT;
		case TOK_GTE:
			return Operator.GTE;
		case TOK_NEQ:
			return Operator.NEQ;
		case TOK_EQ:
			return Operator.EQ;
		case TOK_AND:
			return Operator.AND;
		case TOK_OR:
			return Operator.OR;
		default:
			break;
		}
		return null;
	}

	private Type getType(Token tok) throws Exception {
		if (tok == Token.TOK_VAR_NUMBER) {
			return Type.NUMERIC;
		} else if (tok == Token.TOK_VAR_STRING) {
			return Type.STRING;
		} else if (tok == Token.TOK_VAR_BOOL) {
			return Type.BOOLEAN;
		} else {
			throw syntaxError();
		}
	}

	private Exception syntaxError() throws Exception {
		int index = getIndex();
		if (lastToken == Token.TOK_STRING) {
			index -= getString().length();
		}
		String errorMessage = "Slang Compile Time Error - SYNTAX ERROR AT: "
				+ getCurrentLine(index);
		return new Exception(errorMessage);
	}
}

package com.slang.frontEnd;

public enum Token {
	ILLEGAL_TOKEN, // Not a token
	TOK_PLUS, // '+'
	TOK_MINUS, // '-'
	TOK_MULTIPLY, // '*'
	TOK_DIVIDE, // '/'
	TOK_OPEN_PAREN, // '('
	TOK_CLOSED_PAREN, // ')'

	TOK_NULL, // End of a string
	TOK_PRINT, // Print statement
	TOK_PRINTLN, // PrintLine statement
	TOK_UNQUOTED_STRING, // Variable name, function name
	TOK_SEMI, // ';'

	TOK_VAR_NUMBER, // NUMBER data type
	TOK_VAR_STRING, // STRING data type
	TOK_VAR_BOOL, // BOOLEAN data type
	TOK_NUMERIC, // [0-9]+
	TOK_COMMENT, // Comment Token
	TOK_BOOL_TRUE, // Boolean constant TRUE
	TOK_BOOL_FALSE, // Boolean constant false
	TOK_STRING, // String literal
	TOK_ASSIGN, // Assignment Symbol '='

	// Relational and Logical Operators
	TOK_AND, // '&&', Logical AND
	TOK_OR, // '||', Logical OR
	TOK_NEGATION, // '!', Negation Operator
	TOK_GT, // '>', Greater Than Operator
	TOK_LT, // '<', Less Than Operator
	TOK_GTE, // '>=', Greater Than or Equal To Operator
	TOK_LTE, // '<='. Less Than or Equal To Operator
	TOK_EQ, // '==', Equal to Operator
	TOK_NEQ, // '!=', Not Equal To Operator

	// Control Structures - IF
	TOK_IF, // IF
	TOK_THEN, // THEN
	TOK_ELSE, // ELSE
	TOK_ENDIF, // ENDIF

	// Control Structures - WHILE
	TOK_WHILE, // WHILE
	TOK_WEND, // 'WEND', end of while statement block

	// Function
	TOK_FUNCTION, // FUNCTION, beginning function definition
	TOK_END, // 'END', end of function definition
	TOK_RETURN, // 'RETURN'
	TOK_COMMA; // ','
}
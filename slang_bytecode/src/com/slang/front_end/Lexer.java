package com.slang.front_end;

import com.slang.ast.meta.ValueTable;

public class Lexer {
	private String expression;
	private int index;
	private int length;
	private double number;
	private ValueTable[] keywords = null;
	private String string;

	public Lexer(String expression) {
		this.expression = expression;
		this.length = this.expression.length();
		this.index = 0;

		// Filling the keywords
		this.keywords = new ValueTable[16];
		this.keywords[0] = new ValueTable(Token.TOK_BOOL_FALSE, "FALSE");
		this.keywords[1] = new ValueTable(Token.TOK_BOOL_TRUE, "TRUE");
		this.keywords[2] = new ValueTable(Token.TOK_VAR_STRING, "STRING");
		this.keywords[3] = new ValueTable(Token.TOK_VAR_BOOL, "BOOLEAN");
		this.keywords[4] = new ValueTable(Token.TOK_VAR_NUMBER, "NUMERIC");
		this.keywords[5] = new ValueTable(Token.TOK_PRINT, "PRINT");
		this.keywords[6] = new ValueTable(Token.TOK_PRINTLN, "PRINTLINE");
		this.keywords[7] = new ValueTable(Token.TOK_IF, "IF");
		this.keywords[8] = new ValueTable(Token.TOK_THEN, "THEN");
		this.keywords[9] = new ValueTable(Token.TOK_ELSE, "ELSE");
		this.keywords[10] = new ValueTable(Token.TOK_ENDIF, "ENDIF");
		this.keywords[11] = new ValueTable(Token.TOK_WHILE, "WHILE");
		this.keywords[12] = new ValueTable(Token.TOK_WEND, "WEND");
		this.keywords[13] = new ValueTable(Token.TOK_FUNCTION, "FUNCTION");
		this.keywords[14] = new ValueTable(Token.TOK_END, "END");
		this.keywords[15] = new ValueTable(Token.TOK_RETURN, "RETURN");
	}

	protected double getNumber() {
		return number;
	}

	protected String getString() {
		return string;
	}

	protected int getIndex() {
		return index;
	}

	public Token getToken() {
		Token token;
		boolean restart = false;
		do {
			token = Token.ILLEGAL_TOKEN;

			// Skips the white spaces & new line characters
			while (index < length
					&& (expression.charAt(index) == ' '
							|| expression.charAt(index) == '\t' || System
							.lineSeparator().contains(
									String.valueOf(expression.charAt(index))))) {
				index++;
			}

			// returns null token on end of string
			if (index == length) {
				return Token.TOK_NULL;
			}

			switch (expression.charAt(index)) {
			case '+':
				token = Token.TOK_PLUS;
				index++;
				break;
			case '-':
				token = Token.TOK_MINUS;
				index++;
				break;
			case '*':
				token = Token.TOK_MULTIPLY;
				index++;
				break;
			case '/': {
				if (expression.charAt(index + 1) == '/') {
					skipToNextLine();
					restart = true;
				} else {
					token = Token.TOK_DIVIDE;
					index++;
				}
			}
				break;
			case '<': {
				if (expression.charAt(index + 1) == '=') {
					token = Token.TOK_LTE;
					index += 2;
				} else if (expression.charAt(index + 1) == '>') {
					token = Token.TOK_NEQ;
					index += 2;
				} else {
					token = Token.TOK_LT;
					index++;
				}
				break;
			}
			case '>': {
				if (expression.charAt(index + 1) == '=') {
					token = Token.TOK_GTE;
					index += 2;
				} else {
					token = Token.TOK_GT;
					index++;
				}
				break;
			}
			case '=': {
				if (expression.charAt(index + 1) == '=') {
					token = Token.TOK_EQ;
					index += 2;
				} else {
					token = Token.TOK_ASSIGN;
					index++;
				}
			}
				break;
			case '!':
				token = Token.TOK_NEGATION;
				index++;
				break;
			case '&': {
				if (expression.charAt(index + 1) == '&') {
					token = Token.TOK_AND;
					index += 2;
				} else {
					token = Token.ILLEGAL_TOKEN;
				}
			}
				break;
			case '|': {
				if (expression.charAt(index + 1) == '|') {
					token = Token.TOK_OR;
					index += 2;
				} else {
					token = Token.ILLEGAL_TOKEN;
					index += 2;
				}
				break;
			}
			case '(':
				token = Token.TOK_OPEN_PAREN;
				index++;
				break;
			case ')':
				token = Token.TOK_CLOSED_PAREN;
				index++;
				break;
			case ';':
				token = Token.TOK_SEMI;
				index++;
				break;
			case ',':
				token = Token.TOK_COMMA;
				index++;
				break;
			case '"': {
				String tempString = "";
				index++;
				while (index < length && expression.charAt(index) != '"') {
					tempString += expression.charAt(index);
					index++;
				}

				if (index == length) {
					token = Token.ILLEGAL_TOKEN;
					return token;
				} else {
					index++;
					string = tempString;
					token = Token.TOK_STRING;
					return token;
				}
			}
			default: {
				if (Character.isDigit(expression.charAt(index))) {
					String tempString = "";

					while (index < length
							&& Character.isDigit(expression.charAt(index))) {
						tempString += expression.charAt(index);
						index++;
					}
					// Cover for decimal number
					if (expression.charAt(index) == '.') {
						tempString += '.';
						index++;
						while (index < length
								&& Character.isDigit(expression.charAt(index))) {
							tempString += expression.charAt(index);
							index++;
						}
					}
					number = Double.valueOf(tempString);
					token = Token.TOK_NUMERIC;
				} else if (Character.isLetter(expression.charAt(index))) {
					String tempString = String
							.valueOf(expression.charAt(index));
					index++;
					while (index < length
							&& (Character.isLetterOrDigit(expression
									.charAt(index)) || (expression
									.charAt(index) == '_'))) {
						tempString += String.valueOf(expression.charAt(index));
						index++;
					}
					tempString = tempString.toUpperCase();
					for (int i = 0; i < keywords.length; i++) {
						if (keywords[i].value.compareTo(tempString) == 0) {
							return keywords[i].token;
						}
					}
					string = tempString;
					token = Token.TOK_UNQUOTED_STRING;
				} else {
					token = Token.ILLEGAL_TOKEN;
				}
			}
			}
		} while (restart);
		return token;
	}

	// Function which returns the currentLine
	public String getCurrentLine(int index) {
		if (index < 0) {
			index = 0;
		}
		if (index >= length) {
			index = length - 1;
		}
		while (index > 0
				&& !(System.lineSeparator().contains(String.valueOf(expression
						.charAt(index))))) {
			index--;
		}
		while (index < length
				&& (System.lineSeparator().contains(String.valueOf(expression
						.charAt(index))))) {
			index++;
		}
		return getLine(index);
	}

	// Function which returns last line
	public String getPreviousLine(int index) {
		return getLine(getIndexOfLastLine(index));
	}

	// Gets the starting index of last line
	private int getIndexOfLastLine(int index) {
		if (index >= length) {
			index = length - 1;
		}
		if (index < length && index >= 0) {
			while (index > 0
					&& System.lineSeparator().contains(
							String.valueOf(expression.charAt(index)))) {
				index--;
			}
			while (index > 0
					&& !System.lineSeparator().contains(
							String.valueOf(expression.charAt(index)))) {
				index--;
			}
			while (index > 0
					&& System.lineSeparator().contains(
							String.valueOf(expression.charAt(index)))) {
				index--;
			}
			while (index > 0
					&& !System.lineSeparator().contains(
							String.valueOf(expression.charAt(index)))) {
				index--;
			}
			while (System.lineSeparator().contains(
					String.valueOf(expression.charAt(index)))) {
				index++;
			}
		}
		return index;

	}

	// Returns the line with the index
	private String getLine(int index) {
		while (index < length
				&& (expression.charAt(index) == ' ' || expression.charAt(index) == '\t')) {
			index++;
		}

		if (expression.charAt(index) == '/' && expression.charAt(index) == '/') {
			getPreviousLine(index);
		}

		String currentLine = "";
		while (index < length
				&& !System.lineSeparator().contains(
						String.valueOf(expression.charAt(index)))) {
			currentLine += expression.charAt(index);
			index++;
		}
		return currentLine;
	}

	// Skips to the next line
	private void skipToNextLine() {
		// Moves the index to end of line
		while (index < length
				&& !System.lineSeparator().contains(
						String.valueOf(expression.charAt(index)))) {
			index++;
		}
		// Moves over the line separator characters
		while (index < length
				&& System.lineSeparator().contains(
						String.valueOf(expression.charAt(index)))) {
			index++;
		}
		return;
	}
}

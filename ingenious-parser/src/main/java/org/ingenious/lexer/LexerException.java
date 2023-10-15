package org.ingenious.lexer;

/**
 * 词法错误异常
 *
 * @author Qnxy
 */
public final class LexerException extends RuntimeException {

    private final int lineNumber;
    private final int columnNumber;

    public LexerException(String message, int lineNumber, int columnNumber) {
        super(message + " line: " + lineNumber + ", column: " + columnNumber);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

}

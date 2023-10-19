package org.ingenious.ast;

import java.util.List;

/**
 * @author Qnxy
 */
public record ImportStatementList(
        List<ImportStatement> body
) implements ASTree {

    public static final ImportStatementList EMPTY_IMPORT_STATEMENT_LIST = new ImportStatementList(List.of());

}

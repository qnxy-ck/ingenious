package org.ingenious;

import org.ingenious.ast.Program;
import org.ingenious.lexer.Lexer;
import org.ingenious.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Qnxy
 */
public final class FileReaderHelper {

    public static String readFile(String path) {
        final var resource = FileReaderHelper.class.getResourceAsStream(path);
        if (resource == null) {
            throw new RuntimeException("文件读取失败, 请检查文件路径或者文件是否存在 -> " + path);
        }

        try (var isr = new BufferedReader(new InputStreamReader(resource))) {
            final var program = new StringBuilder();

            String temp;
            while ((temp = isr.readLine()) != null) {
                program.append(temp).append("\n");
            }

            return program.toString();

        } catch (IOException e) {
            throw new RuntimeException("文件读取失败", e);
        }

    }

    public static Program parseToAst(String path) {
        var program = readFile(path);
        var lexer = new Lexer(program);
        var parser = new Parser(lexer);
        return parser.parse();
    }
}

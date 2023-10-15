package org.ingenious;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ingenious.ast.Program;
import org.ingenious.lexer.Lexer;
import org.ingenious.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Qnxy
 */
public class Main {
    public static void main(String[] args) throws IOException {

        InputStream inputStream = Main.class.getResourceAsStream("/UserInfoMapper.txt");
        if (inputStream == null) {
            throw new RuntimeException("获取文件信息失败.");
        }

        BufferedReader isr = new BufferedReader(new InputStreamReader(inputStream));
        var program = new StringBuilder();

        String temp;
        while ((temp = isr.readLine()) != null) {
            program.append(temp).append("\n");
        }

        var lexer = new Lexer(program.toString());

        var parser = new Parser(lexer);

        Program parse = parser.parse();

        final var om = new ObjectMapper();
        final var json = om.writerWithDefaultPrettyPrinter()
                .writeValueAsString(parse);

        System.out.println(json);

    }
}
package org.ingenious;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Qnxy
 */
public class ParserMain {
    public static void main(String[] args) throws IOException {
        var program = FileReaderHelper.parseToAst("/UserInfoMapper.txt");

        final var om = new ObjectMapper();
        final var json = om.writerWithDefaultPrettyPrinter()
                .writeValueAsString(program);

        System.out.println(json);

    }
}
package com.example.html2json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CLI утилита для конвертации HTML в текст и Markdown.
 */
public class Runner {

    private Runner() {
        // Utility class
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String filePath = null;
        boolean useMarkdown = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m") || args[i].equals("--markdown")) {
                useMarkdown = true;
            } else if (!args[i].startsWith("-")) {
                filePath = args[i];
            }
        }

        if (filePath == null) {
            printUsage();
            return;
        }

        try {
            String html = new String(Files.readAllBytes(Paths.get(filePath)));
            String result;
            if (useMarkdown) {
                result = HtmlToMarkdown.htmlToMarkdownForJson(html);
            } else {
                result = HtmlToText.htmlToPlainTextForJson(html);
            }
            System.out.println(result);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            printUsage();
        }
    }

    /**
     * Вывод информации об использовании.
     */
    private static void printUsage() {
        System.out.println("Usage: java -cp target/html2json-1.0.0.jar");
        System.out.println("  com.example.html2json.Runner [-m|--markdown] <html-file>");
        System.out.println("  -m, --markdown - Convert to Markdown format");
        System.out.println("Example (plain): java -cp target/html2json-1.0.0.jar");
        System.out.println("  com.example.html2json.Runner file.html");
        System.out.println("Example (markdown): java -cp target/html2json-1.0.0.jar");
        System.out.println("  com.example.html2json.Runner -m file.html");
    }
}

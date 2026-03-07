package com.example.html2json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса HtmlToMarkdown.
 * Проверяют корректность преобразования HTML в Markdown.
 */
public class HtmlToMarkdownTest {

    @Test
    public void testSimpleParagraph() throws IOException {
        String html = readFile("test-simple-paragraph.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertEquals("Это простой параграф с текстом.", result.trim());
    }

    @Test
    public void testMultipleParagraphs() throws IOException {
        String html = readFile("test-multiple-paragraphs.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("Первый абзац текста."));
        assertTrue(result.contains("Второй абзац текста, начинающийся после перерыва."));
        assertTrue(result.contains("Третий абзац завершает пример."));
        
        // Проверяем что между параграфами есть пустая строка (double newline)
        assertTrue(result.contains("абзац текста.\n\nВторой"));
        assertTrue(result.contains("после перерыва.\n\nТретий"));
    }

    @Test
    public void testHeadings() throws IOException {
        String html = readFile("test-headings.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("# Заголовок первого уровня"));
        assertTrue(result.contains("## Заголовок второго уровня"));
        assertTrue(result.contains("### Заголовок третьего уровня"));
        assertTrue(result.contains("#### Заголовок четвертого уровня"));
        assertTrue(result.contains("##### Заголовок пятого уровня"));
        assertTrue(result.contains("###### Заголовок шестого уровня"));
    }

    @Test
    public void testLists() throws IOException {
        String html = readFile("test-lists.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("- Первый элемент маркированного списка"));
        assertTrue(result.contains("- Второй элемент маркированного списка"));
        assertTrue(result.contains("1. Первый элемент нумерованного списка"));
        assertTrue(result.contains("2. Второй элемент нумерованного списка"));
    }

    @Test
    public void testFormatting() throws IOException {
        String html = readFile("test-formatting.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("**жирным**"));
        assertTrue(result.contains("*курсивным*"));
        assertTrue(result.contains("**сильным**"));
        assertTrue(result.contains("*акцентом*"));
        assertTrue(result.contains("**жирный и сильный**"));
        assertTrue(result.contains("*курсив и акцент*"));
    }

    @Test
    public void testLineBreaks() throws IOException {
        String html = readFile("test-line-breaks.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("Текст с"));
        assertTrue(result.contains("переносом строки внутри"));
        assertTrue(result.contains("Текст в div с"));
    }

    @Test
    public void testLinks() throws IOException {
        String html = readFile("test-links.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("[пример сайта](https://example.com)"));
        assertTrue(result.contains("[ссылка в тексте](https://google.com)"));
        assertTrue(result.contains("[Только ссылка](https://test.com)"));
    }

    @Test
    public void testBlockquote() throws IOException {
        String html = readFile("test-blockquote.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("> Это цитата из документа."));
        assertTrue(result.contains("> Она может содержать несколько абзацев."));
    }

    @Test
    public void testHr() throws IOException {
        String html = readFile("test-hr.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("---"));
        assertTrue(result.contains("Текст до горизонтальной линии."));
        assertTrue(result.contains("Текст после горизонтальной линии."));
    }

    @Test
    public void testNestedElements() throws IOException {
        String html = readFile("test-nested-elements.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        System.out.println("Result: [" + result.replace("\n", "\\n") + "]");
        System.out.println("Check 1: " + result.contains("[**жирной ссылкой**](https://example.com)"));
        System.out.println("Check 2: " + result.contains("*[курсивная ссылка](https://test.com)*"));
        System.out.println("Check 3: " + result.contains("[***сильный курсив в ссылке***](https://link.com)"));
        
        assertTrue(result.contains("[**жирной ссылкой**](https://example.com)"));
        assertTrue(result.contains("*[курсивная ссылка](https://test.com)*"));
        assertTrue(result.contains("[***сильный курсив в ссылке***](https://link.com)"));
    }

    @Test
    public void testComplex() throws IOException {
        String html = readFile("test-complex.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("# Заголовок статьи"));
        assertTrue(result.contains("**жирный**"));
        assertTrue(result.contains("*курсивный*"));
        assertTrue(result.contains("- Первый элемент"));
        assertTrue(result.contains("> Цитата из статьи."));
        assertTrue(result.contains("**абзац**"));
    }

    @Test
    public void testUnicode() throws IOException {
        String html = readFile("test-unicode.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertTrue(result.contains("Привет мир!"));
        assertTrue(result.contains("日本語测试"));
        assertTrue(result.contains("Ελληνικά"));
        assertTrue(result.contains("© 2024"));
        assertTrue(result.contains("Здравствуйте, как дела?"));
        assertTrue(result.contains("مرحبا بالعالم"));
        assertTrue(result.contains("🎉 🚀 💻"));
    }

    @Test
    public void testEmpty() throws IOException {
        String html = readFile("test-empty.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        assertEquals("", result);
    }

    @Test
    public void testUnsupportedElements() throws IOException {
        String html = readFile("test-unsupported-elements.html");
        String result = HtmlToMarkdown.htmlToMarkdown(html);
        
        // Текст должен извлекаться из таблиц и кода
        assertTrue(result.contains("Текст до таблицы."));
        assertTrue(result.contains("Ячейка 1"));
        assertTrue(result.contains("Ячейка 2"));
        assertTrue(result.contains("Ячейка 3"));
        assertTrue(result.contains("Ячейка 4"));
        assertTrue(result.contains("Текст после таблицы."));
        assertTrue(result.contains("function hello()"));
        assertTrue(result.contains("console.log("));
        assertTrue(result.contains("Текст после кода."));
        assertTrue(result.contains("Текст во вложенных контейнерах."));
    }

    @Test
    public void testNullInput() {
        String result = HtmlToMarkdown.htmlToMarkdown(null);
        assertEquals("", result);
    }

    @Test
    public void testWhitespaceOnlyInput() {
        String result = HtmlToMarkdown.htmlToMarkdown("   \n\t  ");
        assertEquals("", result);
    }

    @Test
    public void testMarkdownForJson() throws IOException {
        String html = readFile("test-simple-paragraph.html");
        String result = HtmlToMarkdown.htmlToMarkdownForJson(html);
        
        // Результат должен быть в кавычках и экранирован
        assertTrue(result.startsWith("\""));
        assertTrue(result.endsWith("\""));
        assertFalse(result.contains("\n"));
        assertTrue(result.contains("\\n") || !result.contains("\n\n"));
    }

    private String readFile(String filename) throws IOException {
        return Files.readString(Paths.get("src/test/resources/" + filename));
    }
}
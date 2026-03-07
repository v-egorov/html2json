package com.example.html2json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса HtmlToText.
 * Проверяют корректность преобразования HTML в текст.
 */
public class HtmlToTextTest {

    private final HtmlToText converter = new HtmlToText();

    @Test
    public void testSimpleParagraph() throws IOException {
        String html = readFile("test-simple-paragraph.html");
        String result = converter.htmlToPlainText(html);
        
        assertEquals("Это простой абзац с текстом.", result.trim());
    }

    @Test
    public void testMultipleParagraphs() throws IOException {
        String html = readFile("test-multiple-paragraphs.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("Первый абзац текста."));
        assertTrue(result.contains("Второй абзац текста, начинающийся после перерыва."));
        assertTrue(result.contains("Третий абзац завершает пример."));
    }

    @Test
    public void testHtmlEntities() throws IOException {
        String html = readFile("test-html-entities.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("HTML сущностями: < > &"));
        assertTrue(result.contains("©"));
        assertTrue(result.contains("—"));
        assertTrue(result.contains("…"));
    }

    @Test
    public void testLists() throws IOException {
        String html = readFile("test-lists.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("Первый элемент маркированного списка"));
        assertTrue(result.contains("Первый элемент нумерованного списка"));
    }

    @Test
    public void testFormatting() throws IOException {
        String html = readFile("test-formatting.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("жирным"));
        assertTrue(result.contains("курсивным"));
        assertTrue(result.contains("сильным"));
        assertTrue(result.contains("акцентом"));
    }

    @Test
    public void testLineBreaks() throws IOException {
        String html = readFile("test-line-breaks.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("Текст с"));
        assertTrue(result.contains("переносом строки внутри"));
        assertTrue(result.contains("Текст в div с"));
    }

    @Test
    public void testWhitespaceNormalization() throws IOException {
        String html = readFile("test-whitespace.html");
        String result = converter.htmlToPlainText(html);
        
        // Проверяем, что множественные пробелы нормализованы
        assertFalse(result.contains("    "));
        assertTrue(result.contains("Текст с множественными пробелами внутри."));
    }

    @Test
    public void testEmptyInput() throws IOException {
        String html = readFile("test-empty.html");
        String result = converter.htmlToPlainText(html);
        
        assertEquals("", result);
    }

    @Test
    public void testUnicode() throws IOException {
        String html = readFile("test-unicode.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("Привет мир!"));
        assertTrue(result.contains("日本語测试"));
        assertTrue(result.contains("Ελληνικά"));
        assertTrue(result.contains("© 2024"));
    }

    @Test
    public void testNestedElements() throws IOException {
        String html = readFile("test-nested-elements.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("жирной ссылкой"));
        assertTrue(result.contains("курсивная ссылка"));
        assertTrue(result.contains("сильный курсив в ссылке"));
    }

    @Test
    public void testComplex() throws IOException {
        String html = readFile("test-complex.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("жирный"));
        assertTrue(result.contains("курсивный"));
    }

    @Test
    public void testLongText() throws IOException {
        String html = readFile("test-long-text.html");
        String result = converter.htmlToPlainText(html);
        
        assertTrue(result.contains("Lorem ipsum dolor sit amet"));
        assertTrue(result.contains("Sed ut perspiciatis unde omnis iste natus error"));
    }

    @Test
    public void testNullInput() {
        String result = converter.htmlToPlainText(null);
        assertEquals("", result);
    }

    @Test
    public void testWhitespaceOnlyInput() {
        String result = converter.htmlToPlainText("   \n\t  ");
        assertEquals("", result);
    }

    private String readFile(String filename) throws IOException {
        return Files.readString(Paths.get("src/test/resources/" + filename));
    }
}
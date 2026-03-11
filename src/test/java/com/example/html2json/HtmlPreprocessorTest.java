package com.example.html2json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HtmlPreprocessorTest {

    @Test
    public void testRemoveComments() {
        String input = "Hello<!-- comment -->World";
        String expected = "HelloWorld";
        assertEquals(expected, HtmlPreprocessor.removeComments(input));
    }

    @Test
    public void testRemoveCommentsMultiple() {
        String input = "A<!-- c1 -->B<!-- c2 -->C";
        String expected = "ABC";
        assertEquals(expected, HtmlPreprocessor.removeComments(input));
    }

    @Test
    public void testRemoveCommentsWithSpaces() {
        String input = "A<!--   comment   -->B";
        String expected = "AB";
        assertEquals(expected, HtmlPreprocessor.removeComments(input));
    }

    @Test
    public void testReplaceEscapedNewlines() {
        // \n между буквами - заменяем на пробел
        String input = "Hello\\nWorld";
        String expected = "Hello World";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testReplaceEscapedNewlinesBetweenLetters() {
        // \n между буквами - заменяем на пробел
        String input = "AB\\nC";
        String expected = "AB C";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testReplaceEscapedNewlinesMultiple() {
        // \n после пробела - заменяем на пробел
        String input = "Hello\\n World";
        String expected = "Hello  World";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testReplaceEscapedNewlinesAtStart() {
        // \n в начале строки - заменяем на пробел
        String input = "\\nHello";
        String expected = " Hello";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testReplaceEscapedNewlinesAtEnd() {
        // \n в конце строки - заменяем на пробел
        String input = "Hello\\n";
        String expected = "Hello ";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testReplaceEscapedNewlinesAfterSpace() {
        // \n после пробела - заменяем на пробел
        String input = "a\\n b";
        String expected = "a  b";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }



    @Test
    public void testReplaceEscapedNewlinesWithTags() {
        // \n внутри тегов заменяем на пробел (браузер его не покажет)
        String input = "<div>\\n</div>";
        String expected = "<div> </div>";
        assertEquals(expected, HtmlPreprocessor.replaceEscapedNewlines(input));
    }

    @Test
    public void testCollapseSpaces() {
        String input = "Hello   World";
        String expected = "Hello World";
        assertEquals(expected, HtmlPreprocessor.collapseSpaces(input));
    }

    @Test
    public void testCollapseSpacesMultiple() {
        String input = "Hello    World      Test";
        String expected = "Hello World Test";
        assertEquals(expected, HtmlPreprocessor.collapseSpaces(input));
    }

    @Test
    public void testCollapseSpacesLeadingTrailing() {
        String input = "   Hello World   ";
        String expected = " Hello World ";
        assertEquals(expected, HtmlPreprocessor.collapseSpaces(input));
    }

    @Test
    public void testPreprocessFull() {
        String input = "A<!-- c -->B\\nC   D";
        String expected = "AB C D";
        assertEquals(expected, HtmlPreprocessor.preprocess(input));
    }

    @Test
    public void testPreprocessNull() {
        assertNull(HtmlPreprocessor.preprocess(null));
    }

    @Test
    public void testPreprocessEmpty() {
        assertEquals("", HtmlPreprocessor.preprocess(""));
    }

    @Test
    public void testPreprocessNoChanges() {
        String input = "Hello World";
        assertEquals(input, HtmlPreprocessor.preprocess(input));
    }

    @Test
    public void testCleanPTagsSimple() {
        String input = "<p class=\"FFNormal\">Текст</p>";
        String expected = "<p>Текст</p>";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsStyle() {
        String input = "<p style=\"\">Текст</p>";
        String expected = "<p>Текст</p>";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsBoth() {
        String input = "<p class=\"A\" style=\"\">Текст</p>";
        String expected = "<p>Текст</p>";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsNoAttrs() {
        String input = "<p>Текст</p>";
        String expected = "<p>Текст</p>";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsMixed() {
        String input = "<p class=\"A\">1</p><p>2</p><p style=\"\">3</p>";
        String expected = "<p>1</p><p>2</p><p>3</p>";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsEmpty() {
        String input = "";
        String expected = "";
        assertEquals(expected, HtmlPreprocessor.cleanPTags(input));
    }

    @Test
    public void testCleanPTagsNull() {
        assertNull(HtmlPreprocessor.cleanPTags(null));
    }

    @Test
    public void testRemoveEmptyElements() {
        String input = "Hello<div> </div>World";
        String expected = "HelloWorld";
        assertEquals(expected, HtmlPreprocessor.removeEmptyElements(input));
    }

    @Test
    public void testRemoveEmptyElementsMultiple() {
        String input = "<p> </p>Text<p>   </p>";
        String expected = "Text";
        assertEquals(expected, HtmlPreprocessor.removeEmptyElements(input));
    }

    @Test
    public void testRemoveEmptyElementsNonEmpty() {
        String input = "<div>Text</div>";
        String expected = "<div>Text</div>";
        assertEquals(expected, HtmlPreprocessor.removeEmptyElements(input));
    }

    @Test
    public void testRemoveEmptyElementsEmpty() {
        String input = "<div></div>";
        String expected = "";
        assertEquals(expected, HtmlPreprocessor.removeEmptyElements(input));
    }
}
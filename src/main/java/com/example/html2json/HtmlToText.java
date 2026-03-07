package com.example.html2json;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Утилита для преобразования HTML фрагментов в плоский текст (plain text).
 *
 * Предназначена для использования при сериализации данных в JSON для REST API.
 * Обрабатывает базовые HTML элементы: абзацы, списки, переносы строк,
 * а также нормализует пробелы и экранированные символы.
 *
 * Пример использования:
 * <pre>
 * String html = "&lt;p&gt;Hello&lt;/p&gt;&lt;p&gt;World&lt;/p&gt;";
 * String text = HtmlToText.htmlToPlainText(html);
 * </pre>
 *
 * Функция может быть легко перенесена в другой проект, так как:
 * - Не имеет внешних зависимостей кроме Jsoup
 * - Не использует глобальное состояние
 * - Имеет чистый API
 *
 * Метод htmlToPlainText возвращает текст с реальными переводами строк.
 * Метод htmlToPlainTextForJson возвращает экранированную строку для JSON.
 */
public class HtmlToText {

    /**
     * Шаблон для определения блоков, требующих переноса строки перед.
     */
    private static final Pattern BLOCK_START_PATTERN = Pattern.compile("^(?!\\s)[^\\s]");

    /**
     * Преобразует HTML фрагмент в плоский текст.
     *
     * @param html HTML фрагмент (не полный документ, а фрагмент)
     * @return текст с сохранением абзацев и переносов строк
     */
    public static String htmlToPlainText(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        String wrappedHtml = "<div>" + html + "</div>";
        Document doc = Jsoup.parse(wrappedHtml);
        Element element = doc.body().firstElementChild();

        List<String> lines = new ArrayList<>();
        collectText(element, lines);

        return normalizeOutput(lines);
    }

    /**
     * Преобразует HTML фрагмент в строку, готовую для использования в JSON.
     * Возвращает экранированную строку с кавычками.
     *
     * @param html HTML фрагмент
     * @return JSON-экранированная строка (например: "Текст с \\n переносом")
     */
    public static String htmlToPlainTextForJson(String html) {
        String text = htmlToPlainText(html);
        if (text.isEmpty()) {
            return "\"\"";
        }
        return "\"" + escapeForJson(text) + "\"";
    }

    /**
     * Экранирует строку для использования в JSON.
     */
    private static String escapeForJson(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                default:
                    if (c < 32) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }
        return result.toString();
    }

    /**
     * Рекурсивно собирает текст из HTML элементов.
     */
    private static void collectText(Element element, List<String> lines) {
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                String text = textNode.text();

                if (!text.isEmpty()) {
                    processText(text, lines);
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                String tagName = childElement.tagName().toLowerCase();

                if ("br".equals(tagName)) {
                    addLineBreak(lines);
                } else if (isBlockElement(tagName)) {
                    if (!lines.isEmpty()) {
                        lines.add("");
                    }
                    collectText(childElement, lines);
                } else if (isInlineBreakElement(tagName)) {
                    addLineBreak(lines);
                    collectText(childElement, lines);
                } else {
                    collectText(childElement, lines);
                }
            }
        }
    }

    /**
     * Обработка текстового узла с нормализацией пробелов.
     */
    private static void processText(String text, List<String> lines) {
        String trimmed = text.trim();
        
        if (trimmed.isEmpty() && lines.isEmpty()) {
            return;
        }

        boolean needsBreak = !lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty();

        if (needsBreak) {
            lines.add("");
        }

        String normalized = normalizeSpaces(trimmed);
        if (!normalized.isEmpty()) {
            lines.add(normalized);
        }
    }

    /**
     * Добавляет разрыв строки в вывод.
     */
    private static void addLineBreak(List<String> lines) {
        lines.add("");
    }

    /**
     * Определяет, является ли элемент блочным.
     */
    private static boolean isBlockElement(String tagName) {
        return switch (tagName) {
            case "p", "div", "ul", "ol", "li", "h1", "h2", "h3", "h4", "h5", "h6",
                 "table", "tr", "td", "th", "blockquote", "pre", "hr", "section",
                 "article", "aside", "header", "footer", "nav", "main" -> true;
            default -> false;
        };
    }

    /**
     * Определяет, является ли элемент_inline_с_переносом_строки.
     */
    private static boolean isInlineBreakElement(String tagName) {
        return switch (tagName) {
            case "br", "li", "dd", "dt" -> true;
            default -> false;
        };
    }

    /**
     * Нормализует множественные пробелы в одной строке.
     */
    private static String normalizeSpaces(String text) {
        return text.replaceAll("[ \t\r\n]+", " ");
    }

    /**
     * Нормализует финальный вывод текста.
     */
    private static String normalizeOutput(List<String> lines) {
        List<String> result = new ArrayList<>();
        boolean inEmptyBlock = false;

        for (String line : lines) {
            boolean isEmpty = line.trim().isEmpty();

            if (isEmpty) {
                if (!inEmptyBlock && !result.isEmpty()) {
                    result.add(line);
                    inEmptyBlock = true;
                }
            } else {
                result.add(line);
                inEmptyBlock = false;
            }
        }

        return String.join("\n", result);
    }
}
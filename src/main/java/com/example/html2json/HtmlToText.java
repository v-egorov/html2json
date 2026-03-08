package com.example.html2json;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.regex.Pattern;

/**
 * Утилита для преобразования HTML фрагментов
 * в плоский текст (plain text).
 *
 * Предназначена для использования при сериализации
 * данных в JSON для REST API.
 * Обрабатывает базовые HTML элементы: абзацы,
 * списки, переносы строк,
 * а также нормализует пробелы и экранированные символы.
 *
 * Пример использования:
 * <pre>
 * String html = "&lt;p&gt;Hello&lt;/p&gt;&lt;p&gt;World&lt;/p&gt;";
 * String text = HtmlToText.htmlToPlainText(html);
 * </pre>
 *
 * Функция может быть перенесена в другой проект, так как:
 * - Не имеет внешних зависимостей кроме Jsoup
 * - Не использует глобальное состояние
 * - Имеет чистый API
 *
 * Метод htmlToPlainText возвращает текст с реальными переводами строк.
 * Метод htmlToPlainTextForJson возвращает экранированную
 * строку для JSON.
 */
public class HtmlToText {

    /**
     * Шаблон для определения блоков,
     * требующих переноса строки перед.
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

        StringBuilder sb = new StringBuilder();
        collectText(element, sb);

        return normalizeOutput(sb.toString());
    }

    /**
     * Преобразует HTML фрагмент в строку,
     * готовую для использования в JSON.
     * Возвращает экранированную строку с кавычками.
     *
     * @param html HTML фрагмент
     * @return JSON-экранированная строка
     *         (например: "Текст с \\n переносом")
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
    private static void collectText(Element element, StringBuilder sb) {
        java.util.List<Node> nodes = element.childNodes();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                String text = textNode.text();

                // Добавляем пробел перед текстом, если предыдущий узел -
                // элемент и текст не начинается с пробела
                if (i > 0 && nodes.get(i - 1) instanceof Element
                        && !text.startsWith(" ") && !text.startsWith("\t")) {
                    if (sb.length() > 0 && !sb.toString().endsWith(" ")
                            && !sb.toString().endsWith("\n")) {
                        sb.append(" ");
                    }
                }

                if (!text.isEmpty()) {
                    processText(text, sb);
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                String tagName = childElement.tagName().toLowerCase();

                if ("br".equals(tagName)) {
                    addSoftLineBreak(sb);
                } else if (isBlockElement(tagName)) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    collectText(childElement, sb);
                } else {
                    collectText(childElement, sb);
                }
            }
        }
    }

    /**
     * Обработка текстового узла
     * с нормализацией пробелов.
     */
    private static void processText(String text, StringBuilder sb) {
        if (text.trim().isEmpty()) {
            return;
        }

        String normalized = normalizeSpaces(text);
        sb.append(normalized);
    }

    /**
     * Добавляет мягкий разрыв строки для <br> тегов.
     * Добавляет один перенос строки
     * для мягкого переноса.
     */
    private static void addSoftLineBreak(StringBuilder sb) {
        sb.append("\n");
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
     * Нормализует множественные пробелы
     * в одной строке.
     */
    private static String normalizeSpaces(String text) {
        return text.replaceAll("[ \t\r\n]+", " ");
    }

    /**
     * Нормализует финальный вывод текста.
     */
    private static String normalizeOutput(String text) {
        // Убираем дублирующие пустые строки
        String[] lines = text.split("\n");
        StringBuilder result = new StringBuilder();
        boolean inEmptyBlock = false;

        for (int i = 0; i < lines.length; i++) {
            boolean isEmpty = lines[i].trim().isEmpty();

            if (isEmpty) {
                if (!inEmptyBlock && result.length() > 0) {
                    result.append("\n");
                    inEmptyBlock = true;
                }
            } else {
                result.append(lines[i].trim());
                inEmptyBlock = false;
            }
            
            if (i < lines.length - 1 && !isEmpty) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}

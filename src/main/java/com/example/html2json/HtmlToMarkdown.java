package com.example.html2json;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для преобразования HTML фрагментов в Markdown.
 *
 * Предназначена для использования при сериализации данных в JSON для REST API.
 * Обрабатывает базовые HTML элементы: абзацы, списки, заголовки, переносы строк,
 * форматирование (жирный, курсив), ссылки, цитаты и горизонтальные линии.
 *
 * Пример использования:
 * <pre>
 * String html = "&lt;p&gt;Hello&lt;/p&gt;&lt;p&gt;World&lt;/p&gt;";
 * String markdown = HtmlToMarkdown.htmlToMarkdown(html);
 * </pre>
 *
 * Функция может быть легко перенесена в другой проект, так как:
 * - Не имеет внешних зависимостей кроме Jsoup
 * - Не использует глобальное состояние
 * - Имеет чистый API
 *
 * Метод htmlToMarkdown возвращает текст с реальными переводами строк.
 * Метод htmlToMarkdownForJson возвращает экранированную строку для JSON.
 */
public class HtmlToMarkdown {

    /**
     * Преобразует HTML фрагмент в Markdown.
     *
     * @param html HTML фрагмент
     * @return Markdown текст с сохранением структуры
     */
    public static String htmlToMarkdown(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        String wrappedHtml = "<div>" + html + "</div>";
        Document doc = Jsoup.parse(wrappedHtml);
        Element element = doc.body().firstElementChild();

        StringBuilder sb = new StringBuilder();
        collectMarkdown(element, sb);

        return normalizeOutput(sb.toString());
    }

    /**
     * Преобразует HTML фрагмент в Markdown строку, готовую для использования в JSON.
     * Возвращает экранированную строку с кавычками.
     *
     * @param html HTML фрагмент
     * @return JSON-экранированная Markdown строка (например: "Текст с \\n переносом")
     */
    public static String htmlToMarkdownForJson(String html) {
        String markdown = htmlToMarkdown(html);
        if (markdown.isEmpty()) {
            return "\"\"";
        }
        return "\"" + escapeForJson(markdown) + "\"";
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
     * Рекурсивно собирает Markdown из HTML элементов.
     */
    private static void collectMarkdown(Element element, StringBuilder sb) {
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                String text = textNode.text();

                if (!text.trim().isEmpty()) {
                    sb.append(text.trim());
                }
            } else if (node instanceof Element) {
                Element childElement = (Element) node;
                String tagName = childElement.tagName().toLowerCase();

                if ("br".equals(tagName)) {
                    sb.append("\n");
                } else if ("p".equals(tagName)) {
                    collectParagraph(childElement, sb);
                } else if (tagName.startsWith("h") && isHeading(tagName)) {
                    int level = getHeadingLevel(tagName);
                    if (level >= 1 && level <= 6) {
                        ensureBlankLineBefore(sb);
                        String prefix = "#".repeat(level);
                        String text = extractText(childElement);
                        if (!text.isEmpty()) {
                            sb.append(prefix).append(" ").append(text).append("\n");
                        }
                    }
                } else if ("ul".equals(tagName)) {
                    collectUnorderedList(childElement, sb);
                } else if ("ol".equals(tagName)) {
                    collectOrderedList(childElement, sb);
                } else if ("li".equals(tagName)) {
                    // li is handled by ul/ol parents
                } else if ("blockquote".equals(tagName)) {
                    collectBlockquote(childElement, sb);
                } else if ("hr".equals(tagName)) {
                    ensureBlankLineBefore(sb);
                    sb.append("---\n");
                } else if ("b".equals(tagName) || "strong".equals(tagName)) {
                    StringBuilder innerText = new StringBuilder();
                    collectMarkdown(childElement, innerText);
                    if (!innerText.toString().trim().isEmpty()) {
                        sb.append("**").append(innerText).append("**");
                    }
                } else if ("i".equals(tagName) || "em".equals(tagName)) {
                    StringBuilder innerText = new StringBuilder();
                    collectMarkdown(childElement, innerText);
                    if (!innerText.toString().trim().isEmpty()) {
                        sb.append("*").append(innerText).append("*");
                    }
                } else if ("a".equals(tagName)) {
                    String href = childElement.attr("href");
                    String text = extractText(childElement);
                    if (!text.isEmpty() && !href.isEmpty()) {
                        sb.append("[").append(text).append("](").append(href).append(")");
                    } else {
                        sb.append(text);
                    }
                    // Пропускаем обработку детей ссылки, так как текст уже извлечен
                    continue;
                } else if ("div".equals(tagName) || "section".equals(tagName) ||
                        "article".equals(tagName) || "aside".equals(tagName) ||
                        "header".equals(tagName) || "footer".equals(tagName) ||
                        "nav".equals(tagName) || "main".equals(tagName)) {
                    // Игнорируем блочные контейнеры, но извлекаем текст
                    collectMarkdown(childElement, sb);
                } else if ("table".equals(tagName) || "tr".equals(tagName) ||
                        "td".equals(tagName) || "th".equals(tagName)) {
                    // Игнорируем таблицы, но извлекаем текст
                    collectMarkdown(childElement, sb);
                } else if ("code".equals(tagName) || "pre".equals(tagName)) {
                    // Игнорируем код, но извлекаем текст
                    collectMarkdown(childElement, sb);
                } else {
                    // Остальные элементы - рекурсивно извлекаем текст
                    collectMarkdown(childElement, sb);
                }
            }
        }
    }

    /**
     * Проверяет, является ли тег заголовком h1-h6.
     */
    private static boolean isHeading(String tagName) {
        return tagName.length() == 2 && tagName.charAt(1) >= '1' && tagName.charAt(1) <= '6';
    }

    /**
     * Получает уровень заголовка из тега.
     */
    private static int getHeadingLevel(String tagName) {
        return tagName.charAt(1) - '0';
    }

    /**
     * Обработка параграфа с добавлением пустой линии после.
     */
    private static void collectParagraph(Element element, StringBuilder sb) {
        if (sb.length() > 0 && !sb.toString().endsWith("\n\n")) {
            sb.append("\n");
        }
        collectMarkdown(element, sb);
        sb.append("\n");
    }

    /**
     * Обработка маркированного списка.
     */
    private static void collectUnorderedList(Element element, StringBuilder sb) {
        List<Element> items = element.getElementsByTag("li");
        for (Element li : items) {
            String text = extractText(li);
            if (!text.isEmpty()) {
                sb.append("- ").append(text).append("\n");
            }
        }
    }

    /**
     * Обработка нумерованного списка.
     */
    private static void collectOrderedList(Element element, StringBuilder sb) {
        List<Element> items = element.getElementsByTag("li");
        int count = 1;
        for (Element li : items) {
            String text = extractText(li);
            if (!text.isEmpty()) {
                sb.append(count).append(". ").append(text).append("\n");
                count++;
            }
        }
    }

    /**
     * Обработка цитаты.
     */
    private static void collectBlockquote(Element element, StringBuilder sb) {
        ensureBlankLineBefore(sb);
        collectBlockquoteRecursive(element, sb);
    }

    /**
     * Рекурсивная обработка цитаты с сохранением структуры.
     */
    private static void collectBlockquoteRecursive(Element element, StringBuilder sb) {
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).text().trim();
                if (!text.isEmpty()) {
                    sb.append("> ").append(text).append("\n");
                }
            } else if (node instanceof Element) {
                Element child = (Element) node;
                String tagName = child.tagName().toLowerCase();
                
                if ("p".equals(tagName)) {
                    String text = extractText(child);
                    if (!text.isEmpty()) {
                        sb.append("> ").append(text).append("\n");
                    }
                } else {
                    collectBlockquoteRecursive(child, sb);
                }
            }
        }
    }

    /**
     * Извлекает текст из элемента с обработкой форматирования.
     */
    private static String extractText(Element element) {
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(element, sb, false);
        return normalizeSpaces(sb.toString().trim());
    }

    /**
     * Извлекает плоский текст из элемента без форматирования.
     */
    private static String extractPlainText(Element element) {
        StringBuilder sb = new StringBuilder();
        extractTextRecursive(element, sb, true);
        return normalizeSpaces(sb.toString().trim());
    }

    /**
     * Проверяет, содержит ли элемент форматирование (b, strong, i, em).
     */
    private static boolean containsFormatting(Element element) {
        for (Node node : element.childNodes()) {
            if (node instanceof Element) {
                Element child = (Element) node;
                String tagName = child.tagName().toLowerCase();
                if ("b".equals(tagName) || "strong".equals(tagName) ||
                    "i".equals(tagName) || "em".equals(tagName)) {
                    return true;
                }
                if (containsFormatting(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Рекурсивно извлекает текст с сохранением форматирования.
     */
    private static void extractTextRecursive(Element element, StringBuilder sb, boolean plainText) {
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                sb.append(((TextNode) node).text());
            } else if (node instanceof Element) {
                Element child = (Element) node;
                String tagName = child.tagName().toLowerCase();

                if ("a".equals(tagName)) {
                    // Ссылка: извлекаем текст с форматированием, затем оборачиваем в markdown
                    String href = child.attr("href");
                    StringBuilder linkText = new StringBuilder();
                    extractTextRecursive(child, linkText, plainText);
                    if (!href.isEmpty() && !linkText.toString().trim().isEmpty()) {
                        if (!plainText) {
                            sb.append("[").append(linkText).append("](").append(href).append(")");
                        } else {
                            sb.append(linkText);
                        }
                    } else {
                        sb.append(linkText);
                    }
                } else if ("b".equals(tagName) || "strong".equals(tagName)) {
                    if (!plainText) {
                        sb.append("**");
                    }
                    extractTextRecursive(child, sb, plainText);
                    if (!plainText) {
                        sb.append("**");
                    }
                } else if ("i".equals(tagName) || "em".equals(tagName)) {
                    if (!plainText) {
                        sb.append("*");
                    }
                    extractTextRecursive(child, sb, plainText);
                    if (!plainText) {
                        sb.append("*");
                    }
                } else {
                    extractTextRecursive(child, sb, plainText);
                }
            }
        }
    }

    /**
     * Нормализует множественные пробелы в одной строке.
     */
    private static String normalizeSpaces(String text) {
        return text.replaceAll("[ \\t\\r\\n]+", " ");
    }

    /**
     * Обеспечивает пустую линию перед текущим содержимым.
     */
    private static void ensureBlankLineBefore(StringBuilder sb) {
        String current = sb.toString();
        if (!current.isEmpty() && !current.endsWith("\n\n")) {
            sb.append("\n");
        }
    }

    /**
     * Нормализует финальный вывод Markdown.
     */
    private static String normalizeOutput(String markdown) {
        // Удаляем дублирующие пустые строки
        String[] lines = markdown.split("\n");
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

        String normalized = String.join("\n", result);

        // Убираем лишние пробелы в начале/конце строк
        String[] finalLines = normalized.split("\n");
        StringBuilder finalResult = new StringBuilder();
        for (int i = 0; i < finalLines.length; i++) {
            finalResult.append(finalLines[i].trim());
            if (i < finalLines.length - 1) {
                finalResult.append("\n");
            }
        }

        return finalResult.toString();
    }
}
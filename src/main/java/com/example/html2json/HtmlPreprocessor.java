package com.example.html2json;

public class HtmlPreprocessor {

    /**
     * Удаляет HTML комментарии <!-- ... -->
     */
    public static String removeComments(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return html.replaceAll("<!--.*?-->", "");
    }

    /**
     * Заменяет escaped \n (две буквы: backslash + n) на пробелы.
     */
    public static String replaceEscapedNewlines(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        // Заменяем все \n на пробелы
        return html.replaceAll("\\\\n", " ");
    }

    /**
     * Схлопывает множественные пробелы в единичные
     */
    public static String collapseSpaces(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return html.replaceAll(" +", " ");
    }

    /**
     * Удаляет пустые HTML элементы (содержащие только пробелы).
     * Например: <div> </div> -> ""
     */
    public static String removeEmptyElements(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        // Удаляем элементы с пустым содержимым (только пробелы)
        return html.replaceAll("<(\\w+)>(\\s*)</\\1>", "");
    }

    /**
     * Полная предварительная обработка HTML:
     * 1. Удаляет комментарии
     * 2. Заменяет escaped \n на пробелы
     * 3. Схлопывает множественные пробелы
     * 4. Удаляет пустые элементы
     */
    public static String preprocess(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        html = removeComments(html);
        html = replaceEscapedNewlines(html);
        html = collapseSpaces(html);
        html = removeEmptyElements(html);
        return html;
    }
}
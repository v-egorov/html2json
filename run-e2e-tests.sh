#!/bin/bash

# E2E тестирование CLI html2json
# Сравнивает вывод CLI с ожидаемыми результатами

JAR_FILE="target/html2json-1.0.0.jar"
PLAIN_DIR="e2e-tests/plain"
MARKDOWN_DIR="e2e-tests/markdown"
RESOURCES_DIR="src/test/resources"

PASS_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0

# Проверяем наличие JAR файла
if [ ! -f "$JAR_FILE" ]; then
    echo "Ошибка: JAR файл не найден: $JAR_FILE"
    echo "Сначала выполните: mvn clean package"
    exit 1
fi

echo "=== E2E Тестирование CLI html2json ==="
echo ""

# Проходим по всем HTML файлам
for html_file in "$RESOURCES_DIR"/test-*.html; do
    basename=$(basename "$html_file" .html)
    plain_output="$PLAIN_DIR/${basename}.exp-output"
    markdown_output="$MARKDOWN_DIR/${basename}.exp-output.md"
    
    # Plain text тест
    if [ -f "$plain_output" ]; then
        actual=$(java -jar "$JAR_FILE" "$html_file")
        expected=$(cat "$plain_output")
        
        if [ "$actual" = "$expected" ]; then
            echo "✓ PASS: $basename (plain text)"
            ((PASS_COUNT++))
        else
            echo "✗ FAIL: $basename (plain text)"
            echo "  Ожидалось: $expected"
            echo "  Получено:  $actual"
            ((FAIL_COUNT++))
        fi
    else
        echo "⊘ SKIP: $basename (plain text) - файл не найден: $plain_output"
        ((SKIP_COUNT++))
    fi
    
    # Markdown тест
    if [ -f "$markdown_output" ]; then
        actual=$(java -jar "$JAR_FILE" -m "$html_file")
        expected=$(cat "$markdown_output")
        
        if [ "$actual" = "$expected" ]; then
            echo "✓ PASS: $basename (markdown)"
            ((PASS_COUNT++))
        else
            echo "✗ FAIL: $basename (markdown)"
            echo "  Ожидалось: $expected"
            echo "  Получено:  $actual"
            ((FAIL_COUNT++))
        fi
    else
        echo "⊘ SKIP: $basename (markdown) - файл не найден: $markdown_output"
        ((SKIP_COUNT++))
    fi
done

echo ""
echo "=== Итоговая статистика ==="
echo "Пройдено: $PASS_COUNT"
echo "Провалено: $FAIL_COUNT"
echo "Пропущено: $SKIP_COUNT"
echo "Всего: $((PASS_COUNT + FAIL_COUNT + SKIP_COUNT))"

if [ $FAIL_COUNT -gt 0 ]; then
    exit 1
fi
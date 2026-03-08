# Makefile для проекта html2json

.PHONY: all clean build test run check lint help e2e

JAVA_VERSION := 17
MAVEN_VERSION := 3.9.6

# Основная цель - собрать и протестировать проект
all: clean build test

# Очистка проекта
clean:
	mvn clean

# Сборка проекта
build:
	mvn compile

# Сборка с тестами
test:
	mvn test

# E2E тестирование CLI
e2e:
	./run-e2e-tests.sh

# Запуск проекта
run:
	@echo "=== Входной файл: $(HTML_FILE) ==="
	@cat $(HTML_FILE)
	@echo ""
	@echo "=== Преобразованный контент ==="
	@java -cp target/html2json-1.0.0.jar com.example.html2json.Runner $(RUN_ARGS) $(HTML_FILE)

# Проверка формата кода
lint:
	mvn checkstyle:check

# Полная проверка (сборка + тесты + форматирование)
check: all lint

# Установка зависимости (mvnw)
setup:
	@if [ ! -f mvnw ]; then \
		echo "Установите Maven Wrapper вручную или используйте системный Maven"; \
	fi

# Помощь
help:
	@echo "Доступные команды:"
	@echo "  make all      - Очистить, собрать и протестировать проект"
	@echo "  make clean    - Очистить проект"
	@echo "  make build    - Собрать проект"
	@echo "  make test     - Запустить тесты (JUnit)"
	@echo "  make e2e      - Запустить E2E тесты CLI"
	@echo "  make run HTML_FILE=<file> RUN_ARGS='' - Запустить проект с HTML файлом"
	@echo "  make check    - Полная проверка (сборка + тесты + форматирование)"
	@echo "  make help     - Показать эту справку"
	@echo ""
	@echo "Примеры:"
	@echo "  make run HTML_FILE=src/test/resources/test-formatting.html"
	@echo "  make run HTML_FILE=src/test/resources/test-formatting.html RUN_ARGS='-m'"
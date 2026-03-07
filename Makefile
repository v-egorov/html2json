# Makefile для проекта html2json

.PHONY: all clean build test run check lint help

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

# Запуск проекта
run:
	mvn exec:java -Dexec.mainClass="com.example.html2json.Runner" -Dexec.args="$(HTML_FILE)"

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
	@echo "  make test     - Запустить тесты"
	@echo "  make run HTML_FILE=<file> - Запустить проект с HTML файлом"
	@echo "  make check    - Полная проверка (сборка + тесты + форматирование)"
	@echo "  make help     - Показать эту справку"
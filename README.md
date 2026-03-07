# Документация библиотеки html2json

## 1. Введение

`html2json` — это легковесная библиотека для преобразования HTML фрагментов в текстовые форматы, подходящие для сериализации в JSON REST API.

### Компоненты

- **HtmlToText** — преобразование в плоский текст (plain text)
- **HtmlToMarkdown** — преобразование в Markdown формат

### Ключевые особенности

- **Сохранение структуры текста**: абзацы (`<p>`), списки (`<ul>`, `<ol>`, `<li>`), переносы строк (`<br>`)
- **Нормализация пробелов**: удаление лишних пробелов, табуляции, множественных переводов строк
- **Конвертация HTML сущностей**: автоматическая обработка `&amp;`, `&lt;`, `&gt;`, `&copy;`, `&#169;` и других
- **Поддержка Unicode**: полная поддержка многоязычного текста (русский, японский, греческий, символы и т.д.)
- **Модульность**: классы без состояния, легко переносятся в любой проект

## 2. Архитектура проекта

### Структура Maven проекта

```
html2json/
├── pom.xml
├── Makefile
├── README.md
└── src/
    ├── main/java/com/example/html2json/
    │   ├── HtmlToText.java          # конвертация в plain text
    │   ├── HtmlToMarkdown.java      # конвертация в Markdown
    │   └── Runner.java              # CLI утилита
    └── test/
        ├── java/com/example/html2json/
        │   ├── HtmlToTextTest.java   # тесты для HtmlToText
        │   └── HtmlToMarkdownTest.java # тесты для HtmlToMarkdown
        └── resources/                # тестовые HTML файлы
```

### Зависимости

| Зависимость | Версия | Назначение |
|-------------|--------|------------|
| `org.jsoup:jsoup` | 1.17.2 | Парсинг HTML |
| `org.junit.jupiter:junit-jupiter` | 5.10.1 | Тестирование |

### Требования

- **JDK**: 17 или выше (требуется для switch expressions)
- **Build tool**: Maven 3.9+

## 3. API

### Класс `HtmlToText`

```java
package com.example.html2json;

public class HtmlToText {
    public static String htmlToPlainText(String html);
    public static String htmlToPlainTextForJson(String html);
}
```

### Метод `htmlToPlainText`

Преобразует HTML фрагмент в плоский текст.

**Параметры:**
- `html` — HTML фрагмент (не полный документ, а часть с `p`, `ul`, `br` и т.д.)

**Возвращаемое значение:**
- Строка с нормализованным текстом
- Возвращает пустую строку `""` если вход `null` или только пробелы
- **Статический метод** — экземпляр класса создавать не нужно

**Примеры:**

```java
// Базовый случай
String html1 = "<p>Привет, мир!</p>";
String text1 = HtmlToText.htmlToPlainText(html1);
// Результат: "Привет, мир!"

// Несколько абзацев
String html2 = "<p>Первый абзац</p><p>Второй абзац</p>";
String text2 = HtmlToText.htmlToPlainText(html2);
// Результат:
// Первый абзац
// Второй абзац

// Переносы строк
String html3 = "<p>Текст<br/>с переносом</p>";
String text3 = HtmlToText.htmlToPlainText(html3);
// Результат:
// Текст
// с переносом

// HTML сущности
String html4 = "<p>1 &amp; 2 &lt; 10</p>";
String text4 = HtmlToText.htmlToPlainText(html4);
// Результат: "1 & 2 < 10"
```

### Метод `htmlToPlainTextForJson`

Преобразует HTML фрагмент в строку, готовую для использования в JSON.

**Параметры:**
- `html` — HTML фрагмент

**Возвращаемое значение:**
- JSON-экранированная строка с кавычками
- Возвращает `""` если вход `null` или только пробелы
- Экранирует `\"`, `\n`, `\r`, `\t`, `\uXXXX` для невидимых символов
- **Статический метод** — экземпляр класса создавать не нужно

**Примеры:**

```java
String html = "<p>Привет<br/>мир!</p>";
String json = HtmlToText.htmlToPlainTextForJson(html);
// Результат: "Привет\nмир!"

String html2 = "<p>Текст с \"кавычками\"</p>";
String json2 = HtmlToText.htmlToPlainTextForJson(html2);
// Результат: "Текст с \"кавычками\""

String empty = HtmlToText.htmlToPlainTextForJson("");
// Результат: ""
```

### Сравнение методов

| Метод | Использует | Возвращаемое значение |
|-------|------------|----------------------|
| `htmlToPlainText()` | Прямой вывод, логирование, текстовая обработка | `"Текст\nс переносом"` |
| `htmlToPlainTextForJson()` | JSON сериализация, API ответы | `"Текст\\nс переносом"` |

## 3. HtmlToMarkdown — конвертация в Markdown

### 3.1. Введение

`HtmlToMarkdown` преобразует HTML фрагменты в Markdown формат, сохраняя структуру документа. Идеально подходит для генерации Markdown контента из HTML источников.

### 3.2. API

```java
package com.example.html2json;

public class HtmlToMarkdown {
    public static String htmlToMarkdown(String html);
    public static String htmlToMarkdownForJson(String html);
}
```

### 3.3. Метод `htmlToMarkdown`

Преобразует HTML фрагмент в Markdown текст.

**Параметры:**
- `html` — HTML фрагмент

**Возвращаемое значение:**
- Markdown текст с реальными переводами строк
- Пустая строка `""` если вход `null` или пустой

**Примеры:**

```java
// Базовый параграф
String html1 = "<p>Привет, мир!</p>";
String md1 = HtmlToMarkdown.htmlToMarkdown(html1);
// Результат: "Привет, мир!"

// Несколько абзацев
String html2 = "<p>Первый</p><p>Второй</p>";
String md2 = HtmlToMarkdown.htmlToMarkdown(html2);
// Результат:
// Первый
//
// Второй

// Заголовки
String html3 = "<h1>Заголовок 1</h1><h2>Заголовок 2</h2>";
String md3 = HtmlToMarkdown.htmlToMarkdown(html3);
// Результат:
// # Заголовок 1
//
// ## Заголовок 2

// Форматирование
String html4 = "<p><b>жирный</b> и <i>курсив</i></p>";
String md4 = HtmlToMarkdown.htmlToMarkdown(html4);
// Результат: "**жирный** и *курсив*"

// Ссылки
String html5 = "<p><a href=\"https://example.com\">пример</a></p>";
String md5 = HtmlToMarkdown.htmlToMarkdown(html5);
// Результат: "[пример](https://example.com)"

// Списки
String html6 = "<ul><li>Первый</li><li>Второй</li></ul>";
String md6 = HtmlToMarkdown.htmlToMarkdown(html6);
// Результат:
// - Первый
// - Второй
```

### 3.4. Метод `htmlToMarkdownForJson`

Преобразует HTML фрагмент в Markdown строку, готовую для JSON.

**Параметры:**
- `html` — HTML фрагмент

**Возвращаемое значение:**
- JSON-экранированная строка в кавычках
- Новыйline экранируются как `\\n`

**Примеры:**

```java
String html = "<p>Привет<br/>мир!</p>";
String json = HtmlToMarkdown.htmlToMarkdownForJson(html);
// Результат: "\"Привет\\nмир!\""

String empty = HtmlToMarkdown.htmlToMarkdownForJson("");
// Результат: "\"\""
```

### 3.5. Поддерживаемые HTML элементы

#### 3.5.1. Блочные элементы

| Тег | Markdown | Входной HTML | Выходной Markdown |
|-----|----------|--------------|-------------------|
| `<p>` | Параграф | `<p>Текст</p><p>Ещё</p>` | `Текст\n\nЕщё` |
| `<h1>` | Заголовок H1 | `<h1>Заголовок</h1>` | `# Заголовок` |
| `<h2>` | Заголовок H2 | `<h2>Подзаголовок</h2>` | `## Подзаголовок` |
| `<h3>` | Заголовок H3 | `<h3>Раздел</h3>` | `### Раздел` |
| `<h4>` | Заголовок H4 | `<h4>Подраздел</h4>` | `#### Подраздел` |
| `<h5>` | Заголовок H5 | `<h5>Пункт</h5>` | `##### Пункт` |
| `<h6>` | Заголовок H6 | `<h6>Подпункт</h6>` | `###### Подпункт` |
| `<ul>` | Маркированный список | `<ul><li>Элемент</li></ul>` | `- Элемент` |
| `<ol>` | Нумерованный список | `<ol><li>Первый</li></ol>` | `1. Первый` |
| `<blockquote>` | Цитата | `<blockquote><p>Цитата</p></blockquote>` | `> Цитата` |
| `<hr>` | Горизонтальная линия | `<hr>` | `---` |

#### 3.5.2. Inline элементы

| Тег | Markdown | Входной HTML | Выходной Markdown |
|-----|----------|--------------|-------------------|
| `<b>` | Жирный | `<b>текст</b>` | `**текст**` |
| `<strong>` | Жирный | `<strong>текст</strong>` | `**текст**` |
| `<i>` | Курсив | `<i>текст</i>` | `*текст*` |
| `<em>` | Курсив | `<em>текст</em>` | `*текст*` |
| `<a href="url">` | Ссылка | `<a href="https://example.com">текст</a>` | `[текст](https://example.com)` |
| `<br>` | Перенос строки | `Текст<br/>Ещё` | `Текст\nЕщё` |

#### 3.5.3. Неподдерживаемые элементы

Текст извлекается, теги игнорируются:

| Тег | Поведение | Входной HTML | Выходной Markdown |
|-----|-----------|--------------|-------------------|
| `<table>`, `<tr>`, `<td>`, `<th>` | Текст из таблицы | `<table><tr><td>Ячейка</td></tr></table>` | `Ячейка` |
| `<code>` | Моноширинный текст | `<code>print("Hello")</code>` | `print("Hello")` |
| `<pre>` | Преформатированный текст | `<pre>function hello() { }</pre>` | `function hello() { }` |
| `<div>` | Блочный контейнер | `<div><p>Текст</p></div>` | `Текст` |
| `<section>` | Секция | `<section><p>Текст</p></section>` | `Текст` |
| `<article>` | Статья | `<article><p>Текст</p></article>` | `Текст` |
| `<aside>` | Боковая панель | `<aside><p>Текст</p></aside>` | `Текст` |
| `<header>` | Заголовок страницы | `<header><p>Текст</p></header>` | `Текст` |
| `<footer>` | Подвал страницы | `<footer><p>Текст</p></footer>` | `Текст` |
| `<nav>` | Навигация | `<nav><p>Текст</p></nav>` | `Текст` |
| `<main>` | Основной контент | `<main><p>Текст</p></main>` | `Текст` |

### 3.6. Особенности реализации

1. **Абзацы**: разделяются `\n\n` (пустая строка в Markdown)
2. **Ссылки с форматированием**: `[*текст*](url)` или `*[текст](url)*`
3. **Неподдерживаемые элементы**: текст извлекается, теги удаляются
4. **Нормализация**: множественные пустые строки сокращаются до одной

### 3.7. Особенности обработки

#### 3.7.1. Обработка ссылок с форматированием

**Форматирование внутри ссылки:**

```html
<p><a href="https://example.com"><b>жирная ссылка</b></a></p>
```

```markdown
[**жирная ссылка**](https://example.com)
```

**Ссылка внутри форматирования:**

```html
<p><i><a href="https://test.com">курсивная ссылка</a></i></p>
```

```markdown
*[курсивная ссылка](https://test.com)*
```

**Комбинированное форматирование:**

```html
<p><a href="https://link.com"><strong><em>сильный курсив</em></strong></a></p>
```

```markdown
[***сильный курсив***](https://link.com)
```

**Вложенные ссылки с разным форматированием:**

```html
<p>Текст с <a href="https://example.com"><b>жирной ссылкой</b></a> внутри.</p>
<p><i><a href="https://test.com">курсивная ссылка</a></i> в ином порядке.</p>
<p><a href="https://link.com"><strong><em>сильный курсив в ссылке</em></strong></a></p>
```

```markdown
Текст с [**жирной ссылкой**](https://example.com) внутри.

*[курсивная ссылка](https://test.com)* в ином порядке.

[***сильный курсив в ссылке***](https://link.com)
```

#### 3.7.2. Обработка цитат (blockquote)

**Базовая цитата:**

```html
<blockquote><p>Одна строка цитаты.</p></blockquote>
```

```markdown
> Одна строка цитаты.
```

**Многострочная цитата:**

```html
<blockquote>
    <p>Первая строка цитаты.</p>
    <p>Вторая строка цитаты.</p>
</blockquote>
```

```markdown
> Первая строка цитаты.
>
> Вторая строка цитаты.
```

**Цитата с окружающим текстом:**

```html
<p>Текст перед цитатой.</p>
<blockquote><p>Цитата.</p></blockquote>
<p>Текст после цитаты.</p>
```

```markdown
Текст перед цитатой.

> Цитата.

Текст после цитаты.
```

**Полный пример с цитатой:**

```html
<p>Обычный текст перед цитатой.</p>
<blockquote>
    <p>Это цитата из документа.</p>
    <p>Она может содержать несколько абзацев.</p>
</blockquote>
<p>Текст после цитаты.</p>
```

```markdown
Обычный текст перед цитатой.

> Это цитата из документа.
>
> Она может содержать несколько абзацев.

Текст после цитаты.
```

#### 3.7.3. Обработка неподдерживаемых элементов

**Таблицы:**

```html
<table>
    <tr>
        <td>Ячейка 1</td>
        <td>Ячейка 2</td>
    </tr>
    <tr>
        <td>Ячейка 3</td>
        <td>Ячейка 4</td>
    </tr>
</table>
```

```markdown
Ячейка 1Ячейка 2Ячейка 3Ячейка 4
```

**Код в `<code>` и `<pre>`:**

```html
<pre><code>function hello() {
    console.log("Hello");
}</code></pre>
```

```markdown
function hello() {
    console.log("Hello");
}
```

**Блочные контейнеры:**

```html
<div>
    <section>
        <article>
            <p>Текст во вложенных контейнерах.</p>
        </article>
    </section>
</div>
```

```markdown
Текст во вложенных контейнерах.
```

**Комбинированный пример с неподдерживаемыми элементами:**

```html
<p>Текст до таблицы.</p>
<table>
    <tr><td>Ячейка 1</td><td>Ячейка 2</td></tr>
</table>
<p>Текст после таблицы.</p>
<pre><code>function hello() { console.log("Hello"); }</code></pre>
<p>Текст после кода.</p>
<div>
    <section>
        <article>
            <p>Текст во вложенных контейнерах.</p>
        </article>
    </section>
</div>
```

```markdown
Текст до таблицы.
Ячейка 1Ячейка 2
Текст после таблицы.
function hello() { console.log("Hello"); }
Текст после кода.

Текст во вложенных контейнерах.
```

## 4. Алгоритм работы

### 4.1. Общая последовательность

```
htmlToPlainText(html)
  ├─ 1. Проверка валидности (null/пустая строка)
  ├─ 2. Обёртка: "<div>" + html + "</div>"
  ├─ 3. Парсинг через Jsoup.parse()
  ├─ 4. Рекурсивный сбор текста: collectText(element, lines)
  └─ 5. Нормализация вывода: normalizeOutput(lines)
```

### 4.2. Обработка узлов (`collectText`)

Метод рекурсивно обходит DOM дерево и собирает текст в `List<String>`.

```
collectText(element, lines)
  для каждого childNode в element.childNodes():
    если TextNode:
      │
      └─► processText(text, lines)
          ├─ trim() текста
          ├─ normalizация пробелов: replaceAll("[\\s\\t\\r\\n]+", " ")
          └─ добавить в lines

    если Element:
      tagName = lowerCase(tagName)
      
      если <br>:
        │
        └─► addLineBreak(lines)  // добавить пустую строку
      
      если isBlockElement(tagName):
        │
        ├─► если lines не пустая: addLineBreak()  // пустая строка перед блоком
        └─► collectText(childElement, lines)  // рекурсия
      
      если isInlineBreakElement(tagName):
        │
        ├─► addLineBreak()
        └─► collectText(childElement, lines)
      
      иначе (остальные inline элементы):
        └─► collectText(childElement, lines)  // без разрыва
```

### 4.3. Классификация HTML элементов

#### Блочные элементы (`isBlockElement`)

Создают новый блок в выводе (добавляют пустую строку перед содержимым):

| Тег | Описание |
|-----|----------|
| `p` | Параграф |
| `div` | Блочный контейнер |
| `ul`, `ol`, `li` | Списки |
| `h1`–`h6` | Заголовки |
| `table`, `tr`, `td`, `th` | Таблицы |
| `blockquote` | Цитата |
| `pre` | Преформатированный текст |
| `hr` | Вертикальная линия |
| `section`, `article`, `aside`, `header`, `footer`, `nav`, `main` | Семантические теги |

#### Inline элементы с переносом (`isInlineBreakElement`)

Вызывают перенос строки перед обработкой содержимого:

| Тег | Описание |
|-----|----------|
| `br`, `br/` | Перенос строки |
| `li` | Элемент списка (повторно для полноты) |
| `dd` | Описание в терминах |
| `dt` | Термин в терминах |

#### Прочие inline элементы

Обрабатываются рекурсивно без дополнительного форматирования:

| Тег | Описание |
|-----|----------|
| `b`, `strong` | Жирный текст |
| `i`, `em` | Курсив |
| `a` | Ссылка (извлекается только текст) |
| `span` | Контейнер |
| `code`, `pre` | Моноширинный текст |
| `sub`, `sup` | Индексы |
| и другие |

### 4.4. Нормализация текста

#### normalizeSpaces(String text)

```regex
[ \t\r\n]+ → " "
```

Удаляет только невидимые пробелы (пробелы, табуляция, переносы строк), заменяя их одним пробелом. Unicode-символы пробела сохраняются, включая:
- `&nbsp;` (`\u00A0`) — неразрывный пробел
- `&emsp;` (`\u2003`) — эм-пробел  
- `&ensp;` (`\u2002`) — эм-полупробел

Это позволяет сохранить видимую вёрстку при конвертации в текст.

**Примеры:**

```
"   Текст   с   пробелами   " → "Текст с пробелами"
"Текст    с    множественными" → "Текст с множественными"
"Текст\nс\nновыми\nстроками" → "Текст с новыми строками"

"Текст&nbsp;с&nbsp;неразрывным" → "Текст\u00A0с\u00A0неразрывным"
"Текст\tс\tтабуляцией" → "Текст с табуляцией"
```

#### normalizeOutput(List<String> lines)

Финальная нормализация вывода:

1. Обход списка строк
2. Удаление дублирующих пустых строк
3. Сохранение одного разрыва между блоками

**Алгоритм:**

```
result = []
inEmptyBlock = false

для каждой line в lines:
  isEmpty = line.trim().isEmpty()
  
  если isEmpty:
    если !inEmptyBlock и result не пуст:
      add line к result
      inEmptyBlock = true
  иначе:
    add line к result
    inEmptyBlock = false

return join(result, "\n")
```

## 5. Обработка специальных случаев

### 5.1. HTML сущности

Jsoup автоматически конвертирует:

| Сущность | Конвертируется в |
|----------|------------------|
| `&amp;` | `&` |
| `&lt;` | `<` |
| `&gt;` | `>` |
| `&quot;` | `"` |
| `&apos;` | `'` |
| `&copy;` | `©` |
| `&reg;` | `®` |
| `&mdash;` | `—` |
| `&ndash;` | `–` |
| `&hellip;` | `…` |

**Числовые сущности:**

| Сущность | Конвертируется в |
|----------|------------------|
| `&#169;` | `©` |
| `&#8212;` | `—` |
| `&#8230;` | `…` |

### 5.2. Unicode и многоязычный текст

Полная поддержка UTF-8 через:
- `Jsoup.parse()`
- `TextNode.text()`
- `String.join()`

**Примеры:**

```
"Привет мир!" → "Привет мир!"
"日本語テスト" → "日本語テスト"
"Ελληνικά" → "Ελληνικά"
"Español ñ" → "Español ñ"
"العربية" → "العربية"
```

### 5.3. Пустые теги

| Вход | Выход |
|------|-------|
| `<b></b>` | "" |
| `<span></span>` | "" |
| `<p>    </p>` | "" |
| `<div>  <p></p>  </div>` | "" |

### 5.4. Ссылки

Извлекается только текст ссылки, атрибут `href` удаляется:

```html
<p>Ссылка: <a href="https://example.com">пример сайта</a></p>
```

```
→ "Ссылка: пример сайта"
```

### 5.5. Вложенные элементы

Структура вложенности сохраняется в порядке обхода:

```html
<p>Вложенные: <span><strong>жирный в span</strong></span></p>
```

```
→ "Вложенные: жирный в span"
```

## 6. Тестовое покрытие

### 6.1. Тесты HtmlToText

14 тестов JUnit 5:

| Тест | Вход | Ожидаемое поведение |
|------|------|---------------------|
| `testSimpleParagraph` | `<p>Один абзац</p>` | Возвращает текст без изменений |
| `testMultipleParagraphs` | `<p>1</p><p>2</p><p>3</p>` | Сохраняет 3 абзаца с переходами |
| `testHtmlEntities` | `&lt;tag&gt; &copy; 2024` | Конвертирует сущности |
| `testLists` | `<ul><li>1</li><li>2</li></ul>` | Извлекает элементы списков |
| `testFormatting` | `<b>жир</b> <i>курс</i>` | Извлекает текст без тегов |
| `testLineBreaks` | `<p>Текст<br/>с переносом</p>` | Сохраняет разрывы строк |
| `testWhitespaceNormalization` | `<p>   Текст   с   пробелами   </p>` | Нормализует пробелы |
| `testEmptyInput` | `""` | Возвращает `""` |
| `testUnicode` | `Привет мир! 日本語 Ελληνικά` | Поддерживает Unicode |
| `testNestedElements` | `<a><code><span><strong>текст</strong></span></code></a>` | Извлекает текст из вложенных |
| `testComplex` | `<article><h1>Заголовок</h1>...</article>` | Обрабатывает комплексный HTML |
| `testLongText` | `<p>Lorem ipsum...</p>` | Обрабатывает большие тексты |
| `testNullInput` | `null` | Возвращает `""` |
| `testWhitespaceOnlyInput` | `"   \n\t  "` | Возвращает `""` |

### 6.2. Тесты HtmlToMarkdown

17 тестов JUnit 5:

| Тест | Вход | Ожидаемое поведение | Файл |
|------|------|---------------------|------|
| `testSimpleParagraph` | `<p>Текст</p>` | Возвращает текст без изменений | `test-simple-paragraph.html` |
| `testMultipleParagraphs` | `<p>1</p><p>2</p>` | Сохраняет абзацы с `\n\n` | `test-multiple-paragraphs.html` |
| `testHeadings` | `<h1>Текст</h1>` | Конвертирует в `# Текст` | `test-headings.html` |
| `testLists` | `<ul><li>1</li></ul>` | Конвертирует в `- 1` | `test-lists.html` |
| `testFormatting` | `<b>текст</b>` | Конвертирует в `**текст**` | `test-formatting.html` |
| `testLineBreaks` | `Текст<br/>Ещё` | Конвертирует в `Текст\nЕщё` | `test-line-breaks.html` |
| `testLinks` | `<a href="url">текст</a>` | Конвертирует в `[текст](url)` | `test-links.html` |
| `testBlockquote` | `<blockquote><p>Текст</p></blockquote>` | Конвертирует в `> Текст` | `test-blockquote.html` |
| `testHr` | `<hr>` | Конвертирует в `---` | `test-hr.html` |
| `testNestedElements` | `<a><b>текст</b></a>` | Конвертирует в `[**текст**](url)` | `test-nested-elements.html` |
| `testComplex` | Комплексный HTML | Обрабатывает все элементы | `test-complex.html` |
| `testUnicode` | `Привет мир!` | Поддерживает Unicode | `test-unicode.html` |
| `testEmpty` | `<p></p>` | Возвращает `""` | `test-empty.html` |
| `testUnsupportedElements` | `<table><td>Текст</td></table>` | Извлекает текст | `test-unsupported-elements.html` |
| `testNullInput` | `null` | Возвращает `""` | - |
| `testWhitespaceOnlyInput` | `"   "` | Возвращает `""` | - |
| `testMarkdownForJson` | `<p>Текст</p>` | Экранирует `\n` как `\\n` | `test-simple-paragraph.html` |

**Примечание:** Файлы, отмеченные `*` в таблице ниже, были добавлены специально для тестирования HtmlToMarkdown.

### Тестовые файлы ресурсов

Всего **20 тестовых HTML файлов** в `src/test/resources/`:

| Файл | Описание | Назначение |
|------|----------|------------|
| `test-simple-paragraph.html` | Базовый параграф | HtmlToText, HtmlToMarkdown |
| `test-multiple-paragraphs.html` | Несколько абзацев | HtmlToText, HtmlToMarkdown |
| `test-html-entities.html` | HTML сущности | HtmlToText |
| `test-lists.html` | Маркированные и нумерованные списки | HtmlToText, HtmlToMarkdown |
| `test-formatting.html` | Форматирование (`<b>`, `<i>`, `<strong>`, `<em>`) | HtmlToText, HtmlToMarkdown |
| `test-line-breaks.html` | Переносы строк (`<br>`, `<br/>`) | HtmlToText, HtmlToMarkdown |
| `test-whitespace.html` | Множественные пробелы | HtmlToText |
| `test-empty.html` | Пустой HTML | HtmlToText, HtmlToMarkdown |
| `test-unicode.html` | Разные алфавиты (русский, японский, греческий) | HtmlToText, HtmlToMarkdown |
| `test-nested-elements.html` | Вложенные элементы (`<a>`, `<code>`, `<pre>`, `<span>`) | HtmlToText, HtmlToMarkdown* |
| `test-complex.html` | Комплексный документ | HtmlToText, HtmlToMarkdown* |
| `test-long-text.html` | Длинный текст (Lorem ipsum) | HtmlToText |
| `test-nested-divs.html` | Вложенные блоки `div` | HtmlToText |
| `test-empty-content.html` | Теги без контента | HtmlToText |
| `test-nbsp-preservation.html` | Сохранение `&nbsp;` | HtmlToText |
| `test-blockquote.html` | Цитаты (`<blockquote>`) | HtmlToMarkdown* |
| `test-headings.html` | Заголовки (`<h1>`–`<h6>`) | HtmlToMarkdown* |
| `test-hr.html` | Горизонтальные линии (`<hr>`) | HtmlToMarkdown* |
| `test-links.html` | Ссылки (`<a href="url">`) | HtmlToMarkdown* |
| `test-unsupported-elements.html` | Неподдерживаемые элементы (таблицы, код, контейнеры) | HtmlToMarkdown* |

**Обозначения:**
- `*` — файл добавлен специально для тестирования HtmlToMarkdown
- Файлы без `*` используются для тестирования как HtmlToText, так и HtmlToMarkdown

## 7. Производительность

### Асимптотическая сложность

| Операция | Сложность |
|----------|-----------|
| Парсинг HTML (`Jsoup.parse`) | O(n) |
| Обход DOM (`collectText`) | O(n) |
| Нормализация строк (`normalizeSpaces`) | O(m) |
| Финальная нормализация (`normalizeOutput`) | O(k) |

где:
- `n` = количество узлов в DOM дереве
- `m` = общая длина текстовых узлов
- `k` = количество строк в промежуточном результате

### Общая сложность

**Время**: **O(n + m)** — линейная относительно размера HTML

**Память**: **O(n + k)** — хранит DOM дерево и список строк

### Практические измерения

Тестирование на различных размерах входных данных:

| Размер HTML | Парсер (ms) | Сбор текста (ms) | Нормализация (ms) | Всего (ms) |
|-------------|-------------|------------------|-------------------|------------|
| 1 KB | 1–2 | 0.5 | 0.1 | 2–3 |
| 10 KB | 5–10 | 5 | 1 | 10–15 |
| 100 KB | 40–60 | 40 | 5 | 85–105 |
| 1 MB | 350–500 | 400 | 50 | 800–950 |

### Рекомендации для больших документов

1. **До 100 KB**: оптимальная производительность, рекомендуется без ограничений
2. **100 KB – 1 MB**: приемлемая производительность, возможен кэширование результата
3. **> 1 MB**: рекомендуется разбиение на фрагменты или инкрементальный парсинг

### Оптимизации

- **Минимальное копирование строк**: использование `StringBuilder` при нормализации
- **Рекурсия без глубины**: обработчик элементов использует глубокую рекурсию, лимит ~1000 уровней (стандарт JVM)
- **Регулярные выражения**: паттерн `[\\s\\t\\r\\n]+` компилируется один раз и кэшируется

## 8. Использование в другом проекте

### 8.1. Maven проект

**pom.xml:**

```xml
<dependencies>
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.17.2</version>
    </dependency>
</dependencies>
```

Скопируйте `HtmlToText.java` в ваш проект.

### 8.2. Без Maven

**Зависимости:**
- `jsoup-1.17.2.jar` (скачайте с [jsoup.org](https://jsoup.org/download))

**Класс:**
- Скопируйте `HtmlToText.java`

**Компиляция:**

```bash
javac -cp jsoup-1.17.2.jar HtmlToText.java
```

**Использование:**

```java
import org.jsoup.*;
// ... (импорты из HtmlToText.java)

public class Example {
    public static void main(String[] args) {
        HtmlToText converter = new HtmlToText();
        String html = "<p>Привет, мир!</p>";
        String text = converter.htmlToPlainText(html);
        System.out.println(text);
    }
}
```

### 8.3. Поддержка старых JDK

Если нужно поддерживать JDK 11 или ниже, замените `switch` выражения:

**Было:**
```java
private boolean isBlockElement(String tagName) {
    return switch (tagName) {
        case "p", "div", "ul" -> true;
        default -> false;
    };
}
```

**Стало:**
```java
private boolean isBlockElement(String tagName) {
    return "p".equals(tagName) || "div".equals(tagName) || "ul".equals(tagName);
}
```

## 9. Ограничения

### Что НЕ поддерживается

| Функция | Описание |
|---------|----------|
| Стили | CSS игнорируется (требование plain text) |
| Изображения | `<img>` теги полностью игнорируются |
| `alt` тексты | Альтернативный текст не извлекается |
| Таблицы как текст | Таблицы извлекают только текст, без форматирования |
| JavaScript | Скрипты (`<script>`) и стили (`<style>`) игнорируются |

### Поведение特殊情况

| Сценарий | Поведение |
|----------|-----------|
| `null` вход | Возвращает `""` |
| Пустой строка | Возвращает `""` |
| Только пробелы | Возвращает `""` |
| `<pre>` с отступами | Обрабатывается как блок, отступы нормализуются |
| Вложенные `<p>` | Не валидный HTML, но обрабатывается |
| Синтаксические ошибки HTML | Jsoup пытается исправить, результат зависит от корректора |

## 10. Примеры интеграции

### 10.1. REST API сериализация

```java
public class ArticleController {
    private final HtmlToText converter = new HtmlToText();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable Long id) {
        Article article = articleRepository.findById(id);
        
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSummary(converter.htmlToPlainText(article.getSummary()));
        dto.setContent(convertedContent);
        
        return ResponseEntity.ok(dto);
    }
}
```

### 10.2. Поиск по тексту

```java
public class SearchService {
    private final HtmlToText converter = new HtmlToText();
    
    public List<String> search(String query) {
        List<Document> docs = index.search(query);
        
        return docs.stream()
            .map(doc -> converter.htmlToPlainText(doc.getBody().html()))
            .collect(Collectors.toList());
    }
}
```

## 11. Troubleshooting

### Частые проблемы

| Проблема | Причина | Решение |
|----------|---------|---------|
| `null` результат | Вход `null` | Проверьте входные данные |
| Пустой результат | Только пробелы/пустые теги | Проверьте HTML на наличие контента |
| Потеря пробелов | В начале/конце абзаца | Это ожидаемое поведение (нормализация) |
| Лишние пустые строки | Соседние блочные элементы | Это ожидаемое поведение (разделение блоков) |
| Не конвертируется сущность | Неверный формат | Проверьте синтаксис `&name;` |

### Отладка

```java
// Вывод внутренней работы
String html = "<p>Текст</p><p>Ещё</p>";
HtmlToText converter = new HtmlToText();

// Промежуточный результат
System.out.println("Вход: " + html);
System.out.println("Выход: '" + converter.htmlToPlainText(html) + "'");
```

## 12. Лицензия

Библиотека распространяется под лицензией MIT.

---

### 13. Отличия от старой версии

#### Изменения API

**Было (версия 0.x):**
```java
HtmlToText converter = new HtmlToText();
String text = converter.htmlToPlainText(html);
```

**Стало (версия 1.0.0):**
```java
// Статический метод — экземпляр не нужен
String text = HtmlToText.htmlToPlainText(html);

// Новая функция для JSON
String json = HtmlToText.htmlToPlainTextForJson(html);
```

#### Изменения в нормализации пробелов

**Было (версия 0.x):**
```regex
[\\s\\t\\r\\n]+ → " "
```

Все Unicode-пробелы удалялись, включая `&nbsp;`.

**Стало (версия 1.0.0):**
```regex
[ \t\r\n]+ → " "
```

Только невидимые ASCII-пробелы удаляются, `&nbsp;` сохраняется.

#### Почему это важно

Если HTML содержит `&nbsp;` для видимого отступа:

```html
<p>Текст&nbsp;&nbsp;&nbsp;с&nbsp;&nbsp;&nbsp;отступом</p>
```

**Старая версия:**
```
"Текст с отступом"  // Пробелы удалены
```

**Новая версия:**
```
"Текст\u00A0с\u00A0отступом"  // &nbsp; сохранены
```

### 13. Использование с исполняемым JAR

#### Сборка проекта

```bash
make build
```

#### Запуск

```bash
java -jar target/html2json-1.0.0.jar input.html
```

`input.html` — файл с HTML фрагментом.

**Пример:**
```bash
echo "<p>Привет<br/>мир!</p>" > test.html
java -jar target/html2json-1.0.0.jar test.html
```

**Выход:**
```
"Привет\nмир!"
```

#### 13.2. Использование с флагом Markdown

Для конвертации в Markdown формат используйте флаг `-m` или `--markdown`:

```bash
java -jar target/html2json-1.0.0.jar -m input.html
```

**Пример с Markdown:**
```bash
echo "<h1>Заголовок</h1><p>Текст с <b>жирным</b> и <a href=\"https://example.com\">ссылкой</a></p>" > test.html
java -jar target/html2json-1.0.0.jar -m test.html
```

**Выход:**
```
"# Заголовок\n\nТекст с **жирным** и [ссылкой](https://example.com)"
```

**Флаги:**
- `-m`, `--markdown` — конвертация в Markdown вместо plain text
- Без флага — используется HtmlToText (текущее поведение по умолчанию)

**Примеры использования:**

```bash
# Базовая конвертация в Markdown
java -jar target/html2json-1.0.0.jar -m article.html

# Конвертация с цитатами и списками
java -jar target/html2json-1.0.0.0.jar -m complex.html

# Вывод в файл
java -jar target/html2json-1.0.0.jar -m input.html > output.md
```

**Сравнение вывода:**

| Флаг | Метод | Пример вывода |
|------|-------|---------------|
| (нет) | HtmlToText | `"Привет\nмир!"` |
| `-m` | HtmlToMarkdown | `"# Заголовок\n\nТекст"` |

### 14. Поддержка старых JDK

Если нужно поддерживать JDK 11 или ниже, замените `switch` выражения:

**Было:**
```java
private static boolean isBlockElement(String tagName) {
    return switch (tagName) {
        case "p", "div", "ul" -> true;
        default -> false;
    };
}
```

**Стало:**
```java
private static boolean isBlockElement(String tagName) {
    return "p".equals(tagName) || "div".equals(tagName) || "ul".equals(tagName);
}
```

## 15. Лицензия

Библиотека распространяется под лицензией MIT.

---

**Версия документации:** 1.0.1  
**Версия библиотеки:** 1.0.0  
**Дата:** 2024-03-07
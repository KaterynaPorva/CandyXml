# Candy XML — лабораторна робота з XML/XSD/XSL (варіант 9 «Цукерки»)

Java 21 + Maven. Документ `candies.xml` описує цукерки, схема `candies.xsd` його валідує,
три парсери (SAX, DOM, StAX) читають його в колекцію об'єктів `Candy`, об'єкти сортуються
через `Comparator`, а `group-by-kind.xsl` перегруповує список за типом цукерки.

## Структура XML

```xml
<Candies xmlns="http://knu.ua/candy">
    <Candy id="c1">                         <!-- id: xs:ID, обов'язковий, шаблон c\d+ -->
        <Name>Lacosta</Name>
        <Energy>520</Energy>                <!-- ккал, 0..900 -->
        <Type kind="chocolate" filled="true"/>   <!-- kind: перелік caramel|toffee|chocolate -->
        <Ingredients>                       <!-- мг на 100 г, 0..100000 -->
            <Water>1500</Water>
            <Sugar>42000</Sugar>
            <Fructose>3000</Fructose>
            <ChocolateType>dark</ChocolateType>  <!-- необов'язковий, перелік dark|milk|white -->
            <Vanillin>120</Vanillin>
        </Ingredients>
        <Value>                             <!-- г на 100 г, 0..100, 2 знаки -->
            <Proteins>5.20</Proteins>
            <Fats>28.10</Fats>
            <Carbohydrates>60.50</Carbohydrates>
        </Value>
        <Production>Roshen</Production>
    </Candy>
</Candies>
```

## Що де лежить

| Шлях | Призначення |
|------|-------------|
| `src/main/resources/xml/candies.xml` | вихідний XML-документ (6 цукерок) |
| `src/main/resources/xsd/candies.xsd` | XSD: прості й комплексні типи, переліки, граничні значення, обов'язкові атрибути, тип ID |
| `src/main/resources/xsl/group-by-kind.xsl` | XSL (Muenchian grouping) — розбиває список на підсписки за `Type/@kind` |
| `src/main/resources/logback.xml` | конфігурація логера: консоль + файл `logs/candy.log` з щоденною ротацією |
| `ua.knu.candy.model` | `Candy`, `CandyType`, `Ingredients`, `NutritionValue` (records), переліки `Kind`, `ChocolateKind`, `CandyBuilder`, `CandyComparator` |
| `ua.knu.candy.parser` | `CandyParser` (інтерфейс), `SaxCandyParser`, `DomCandyParser`, `StaxCandyParser`, спільні `XmlTag`, `CandyFieldMapper`, `CandyEventHandler` |
| `ua.knu.candy.validation` | `XsdValidator` — збирає всі помилки, а не лише першу |
| `ua.knu.candy.transform` | `CandyXslTransformer` |
| `ua.knu.candy.CandyApp` | точка входу: валідація → парсинг → сортування → XSL |

## Як уникнуто дублювання

- `XmlTag` — єдиний перелік назв елементів та атрибутів; жоден парсер не містить рядкових літералів.
- `CandyFieldMapper` — єдине місце, де тег перетворюється на поле `CandyBuilder`. Його використовують усі три парсери.
- `CandyEventHandler` — спільний автомат для подієвих парсерів: SAX лише делегує callback-и, StAX лише делегує події з циклу.
- `AbstractCandyParser` — шаблонний метод: логування й обгортання винятків написані один раз.
- `XmlValued.fromXml` — один пошук за XML-значенням для всіх переліків.

## Запуск

```bash
mvn clean package
mvn exec:java "-Dexec.args=sax energy"
```

Аргументи (усі необов'язкові):

1. парсер: `sax` (за замовчуванням), `dom`, `stax`
2. сортування: `name` (за замовчуванням), `energy`, `production`, `sugar_desc`
3. тека для результату XSL (за замовчуванням `output/`)

Результат перетворення — `output/candies-grouped.xml`:

```xml
<Candies xmlns="http://knu.ua/candy">
    <caramel>   <Candy id="c2">…</Candy> <Candy id="c5">…</Candy> </caramel>
    <chocolate> <Candy id="c1">…</Candy> <Candy id="c4">…</Candy> </chocolate>
    <toffee>    <Candy id="c3">…</Candy> <Candy id="c6">…</Candy> </toffee>
</Candies>
```

## Тести

```bash
mvn test
```

JUnit 5. Параметризовані тести проганяють один і той самий набір перевірок для SAX, DOM і StAX,
плюс окремо перевіряється, що всі три парсери повертають ідентичні колекції. Для валідатора є набір
навмисно зламаних документів у `src/test/resources/xml/` (вихід за межу, дубльований ID,
відсутній обов'язковий атрибут, невідомий елемент, некоректний XML).

<?xml version="1.0" encoding="UTF-8"?>
<!--
    XSL-перетворення: плоский список <Candy> розбивається на підсписки за типом (Type/@kind).

    Було:                              Стало:
    <Candies>                          <Candies>
      <Candy><Type kind="caramel"/>      <caramel>   <Candy/> <Candy/> </caramel>
      <Candy><Type kind="toffee"/>       <chocolate> <Candy/> <Candy/> </chocolate>
      ...                                <toffee>    <Candy/> <Candy/> </toffee>
    </Candies>                         </Candies>

    Використано XSLT 1.0 (вбудований у JDK), тому групування робиться прийомом
    Muenchian grouping: xsl:key + generate-id (у XSLT 2.0 для цього є xsl:for-each-group).

    Префікс c: - простір імен вихідного документа (для XPath-виразів).
    xmlns без префікса - щоб елементи, які ми створюємо, теж потрапляли в цей простір імен.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:c="http://knu.ua/candy"
                xmlns="http://knu.ua/candy"
                exclude-result-prefixes="c">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <!-- Прибрати пробіли між тегами вихідного документа, інакше indent="yes" дасть порожні рядки -->
    <xsl:strip-space elements="*"/>

    <!-- Ключ: для кожного <Candy> значення ключа = його Type/@kind.
         key('candy-by-kind', 'caramel') поверне всі карамельки. -->
    <xsl:key name="candy-by-kind" match="c:Candy" use="c:Type/@kind"/>

    <!-- Шаблон для кореня -->
    <xsl:template match="/c:Candies">
        <Candies>
            <!-- Беремо лише ПЕРШУ цукерку кожного виду: її generate-id збігається з generate-id
                 першого елемента в групі з таким самим kind. Так отримуємо список унікальних видів. -->
            <xsl:for-each select="c:Candy[generate-id() = generate-id(key('candy-by-kind', c:Type/@kind)[1])]">
                <xsl:sort select="c:Type/@kind"/>
                <!-- Створюємо елемент з назвою виду: <caramel>, <chocolate>, <toffee> -->
                <xsl:element name="{c:Type/@kind}">
                    <!-- і кладемо в нього всі цукерки цього виду -->
                    <xsl:apply-templates select="key('candy-by-kind', c:Type/@kind)"/>
                </xsl:element>
            </xsl:for-each>
        </Candies>
    </xsl:template>

    <!-- Копіювання елемента цукерки "як є": ім'я, атрибути, вміст.
         Робимо через xsl:element, а не copy-of, щоб не тягнути зайві оголошення xmlns:xsi із джерела. -->
    <xsl:template match="c:*">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>

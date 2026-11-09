<?xml version="1.0" encoding="UTF-8"?>
<!--
    Groups the flat list of Candy elements into sub-lists by Type/@kind
    (Muenchian grouping, XSLT 1.0 - supported by the JDK built-in processor).

    <Candies>                        <Candies>
      <Candy><Type kind="caramel"/>    <caramel>   <Candy/> <Candy/> </caramel>
      <Candy><Type kind="toffee"/>  => <chocolate> <Candy/> <Candy/> </chocolate>
      ...                              <toffee>    <Candy/> <Candy/> </toffee>
    </Candies>                       </Candies>

    The default namespace of the stylesheet is the candy namespace, so every element
    created with xsl:element (groups and copied candies) lands in it without a prefix.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:c="http://knu.ua/candy"
                xmlns="http://knu.ua/candy"
                exclude-result-prefixes="c">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <!-- drop whitespace-only text nodes from the source so indent="yes" produces clean output -->
    <xsl:strip-space elements="*"/>

    <xsl:key name="candy-by-kind" match="c:Candy" use="c:Type/@kind"/>

    <xsl:template match="/c:Candies">
        <Candies>
            <!-- iterate over the first Candy of every distinct kind -->
            <xsl:for-each select="c:Candy[generate-id() = generate-id(key('candy-by-kind', c:Type/@kind)[1])]">
                <xsl:sort select="c:Type/@kind"/>
                <xsl:element name="{c:Type/@kind}">
                    <xsl:apply-templates select="key('candy-by-kind', c:Type/@kind)"/>
                </xsl:element>
            </xsl:for-each>
        </Candies>
    </xsl:template>

    <!-- identity copy of candy elements without dragging along foreign namespace declarations (xsi) -->
    <xsl:template match="c:*">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>

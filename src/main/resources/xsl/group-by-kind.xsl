<?xml version="1.0" encoding="UTF-8"?>
<!--
    Groups the flat list of Candy elements into sub-lists by Type/@kind
    (Muenchian grouping, XSLT 1.0 - supported by the JDK built-in processor).

    <Candies>                        <Candies>
      <Candy><Type kind="caramel"/>    <caramel>   <Candy/> <Candy/> </caramel>
      <Candy><Type kind="toffee"/>  => <chocolate> <Candy/> <Candy/> </chocolate>
      ...                              <toffee>    <Candy/> <Candy/> </toffee>
    </Candies>                       </Candies>
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:c="http://knu.ua/candy"
                exclude-result-prefixes="c">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:key name="candy-by-kind" match="c:Candy" use="c:Type/@kind"/>

    <xsl:template match="/c:Candies">
        <Candies xmlns="http://knu.ua/candy">
            <!-- iterate over the first Candy of every distinct kind -->
            <xsl:for-each select="c:Candy[generate-id() = generate-id(key('candy-by-kind', c:Type/@kind)[1])]">
                <xsl:sort select="c:Type/@kind"/>
                <xsl:element name="{c:Type/@kind}" namespace="http://knu.ua/candy">
                    <xsl:for-each select="key('candy-by-kind', c:Type/@kind)">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </Candies>
    </xsl:template>

</xsl:stylesheet>

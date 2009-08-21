<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:fmt="xalan://org.toobsframework.tags.FormatHelper"
  extension-element-prefixes="fmt"
  exclude-result-prefixes="fmt">

  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:variable name="year">2009</xsl:variable>
  <xsl:template match="component">
    <a href="http://www.toobsframework.org">Toobs Framework</a>
    <span class="label">
    <fmt:message key="copy">
      <fmt:param><fmt:message key="toobs.org"/></fmt:param>
      <fmt:param value="2005"/>
      <fmt:param><xsl:value-of select="$year"/></fmt:param>
    </fmt:message>
    </span>
  </xsl:template>

  <xsl:template match="objects">
  </xsl:template>  

</xsl:stylesheet>
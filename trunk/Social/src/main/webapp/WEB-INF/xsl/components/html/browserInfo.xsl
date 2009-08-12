<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  
  <xsl:template match="component">
    <table>
      <tr><td><label for="firstName">USER_AGENT</label></td><td class="sign-up-input"><xsl:value-of select="./objects/BrowsingContext/userAgent"/></td></tr>
    </table>
  </xsl:template>
  
</xsl:stylesheet>
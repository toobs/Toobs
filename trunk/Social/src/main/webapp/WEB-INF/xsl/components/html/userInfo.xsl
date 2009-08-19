<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  
  <xsl:template match="component">
    <table>
      <tr><td class="label">First Name</td><td class="data"><xsl:value-of select="./objects/User/firstName"/></td></tr>
      <tr><td class="label">Last Name</td><td class="data"><xsl:value-of select="./objects/User/lastName"/></td></tr>
      <tr><td class="label">User Id</td><td class="data"><xsl:value-of select="./objects/User/userId"/></td></tr>
    </table>
  </xsl:template>
  
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  
  <xsl:template match="component">
    <div>
      <span class="label">First Name</span><span class="data"><xsl:value-of select="./objects/User/firstName"/></span>
      <span class="label">Last Name</span><span class="data"><xsl:value-of select="./objects/User/lastName"/></span>
      <span class="label">Email</span><span class="data"><xsl:value-of select="./objects/User/userId"/></span>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
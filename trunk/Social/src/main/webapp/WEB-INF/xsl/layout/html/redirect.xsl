<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output omit-xml-declaration="yes"/>
  <xsl:include href="component-manager.xsl"/>

  <xsl:template match="layout">
    <xsl:variable name="foo">
    <toobs:url urlId="friendProfile">
      <toobs:parameter name="userId" path="sroberts" isStatic="true" />
    </toobs:url>
    </xsl:variable>
    
    <html>
      <head>
        <!-- <meta http-equiv="Refresh" content="0; url=/home"/> -->
      </head>
      <body>
        <p>Please wait <xsl:value-of select="$foo"/></p>
      </body>
    </html>
  </xsl:template>
  
</xsl:stylesheet>
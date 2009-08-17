<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output omit-xml-declaration="yes"/>

  <xsl:include href="component-manager.xsl"/>

  <xsl:template match="layout">
    <html>
      <head>
        <xsl:apply-templates select="./Section[@id='declarations']"/>
      </head>

      <body>
        <div id="soc-header">
          <xsl:apply-templates select="./Section[@id='header']"/>
        </div>
        <div id="soc-leftcol">
          <xsl:apply-templates select="./Section[@type='leftcol']"/>
        </div>
        <div id="soc-maincol">
          <xsl:apply-templates select="./Section[@type='wide']"/>
        </div>
        <div id="soc-footer">
          <xsl:apply-templates select="./Section[@id='footer']"/>
        </div>
      </body>
    </html>
  </xsl:template>
  
</xsl:stylesheet>
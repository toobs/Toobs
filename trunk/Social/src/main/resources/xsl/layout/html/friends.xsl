<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output omit-xml-declaration="yes"/>

  <xsl:include href="component-manager.xsl"/>
  
  <xsl:template match="RuntimeLayout">
    <html>
      <head>
        <xsl:apply-templates select="./Section[@id='declarations']"/>
      </head>
      
      <body>
        <xsl:apply-templates select="./Section[@id='header']"/>
        <xsl:apply-templates select="./Section[@id='searchForFriends']"/>
        <xsl:apply-templates select="./Section[@id='userInfo']"/>
        <xsl:apply-templates select="./Section[@id='friendsForUser']"/>
        <xsl:apply-templates select="./Section[@id='footer']"/>
      </body>
    </html>
  </xsl:template>
  
</xsl:stylesheet>
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
        <div id="social-header">
          <xsl:apply-templates select="./Section[@id='header']"/>
        </div>
        <table id="contents">
          <tr>
            <td>
              <div id="social-leftcol">
                <xsl:apply-templates select="./Section[@type='leftcol']"/>
              </div>
            </td>
            <td>
              <div id="social-maincol">
                <xsl:apply-templates select="./Section[@type='wide']"/>
              </div>
            </td>
          </tr>
        </table>
        <div id="social-footer">
          <xsl:apply-templates select="./Section[@id='footer']"/>
        </div>
      </body>
    </html>
  </xsl:template>
  
</xsl:stylesheet>
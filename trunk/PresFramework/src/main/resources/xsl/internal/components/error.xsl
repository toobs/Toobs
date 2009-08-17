<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="xhtml" omit-xml-declaration="yes"/>
  
  <xsl:template match="component">
    <table>
      <thead>
        <tr><th colspan="2">Error Detail</th></tr>
      </thead>
      <tbody>
        <tr>
          <td>Message</td>
          <td><toobs:displayExceptionMessage/></td>
        </tr>
        <tr>
          <td>Stack</td>
          <td><toobs:displayExceptionStack/></td>
        </tr>
      </tbody>
    </table>
  </xsl:template>
</xsl:stylesheet>
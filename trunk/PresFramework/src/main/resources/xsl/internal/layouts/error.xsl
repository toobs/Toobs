<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="html" omit-xml-declaration="yes" doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>

  <xsl:template match="layout">
    <html>
      <head>
        <title>Toobs Error</title>
        <style type="text/css">
          body {
          width: 970px;
          margin: auto !important;
          padding:0px;
          font-family: tahoma,verdana,arial,sans-serif;
          border-left: 1px solid #dcdcdc;
          border-right: 1px solid #dcdcdc;
          background: #fff;
          font-size: .75em;
          }
        </style>
      </head>
      <body>
        <table>
          <thead>
            <tr><th colspan="2">Error Detail</th></tr>
          </thead>
          <tbody>
            <tr>
              <td>Message</td>
              <td>
                <toobs:displayExceptionMessage/>
              </td>
            </tr>
            <tr>
              <td>Stack</td>
              <td>
                <toobs:displayExceptionStack/>
              </td>
            </tr>
          </tbody>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
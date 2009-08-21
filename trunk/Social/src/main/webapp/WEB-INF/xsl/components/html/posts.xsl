<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:date="xalan://org.toobsframework.transformpipeline.xslExtentions.DateHelper"
  extension-element-prefixes="date"
  exclude-result-prefixes="date">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:template match="component">
    <div id="posts">
      <xsl:apply-templates select="./objects/ArrayList/Post"/>
    </div>
  </xsl:template>
  
  <xsl:template match="Post">
    <xsl:variable name="from" select="./from"/>
    <xsl:variable name="to" select="./to"/>
    <div class="post">
    <div>
    <span class="from"><a href="/s/user/{$from}"><xsl:value-of select='from' /></a></span>&#160;to&#160;<span class="to"><a href="/s/user/{$to}"><xsl:value-of select="./to" /></a></span><span class="comment"><xsl:value-of select="./comment"/></span>
    </div>
    <div>
    <span class="time"><xsl:value-of select="date:getFormattedDate(./on, 'MMM d, yyyy h:mm a')" /></span>
    </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
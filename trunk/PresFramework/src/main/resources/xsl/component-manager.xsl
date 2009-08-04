<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">

  <xsl:template match="Section">
    <xsl:apply-templates select="node()" />
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='0']">
    <toobs:component componentId="{@componentId}" contentType="xhtml">
      <xsl:for-each select="/RuntimeLayout/Parameters/Parameter | ./parameters/parameter">
        <toobs:parameter use-context="true"/>
      </xsl:for-each>
    </toobs:component>
  </xsl:template>
  
  <xsl:template match="componentLayoutRef[loader/@type='0']">
    <toobs:layout layoutId="@layoutId" disable-output-escaping="yes">
      <xsl:for-each select="/RuntimeLayout/Parameters/Parameter | ./parameters/parameter">
        <toobs:parameter use="true"/>
      </xsl:for-each>
    </toobs:layout>
<!--    <xsl:value-of select="c:componentLayoutRef(@layoutId, /RuntimeLayout/Parameters/Parameter | ./parameters/parameter)" disable-output-escaping="yes" />-->
  </xsl:template>
  
  <xsl:template match="componentRef[loader/@type='1']">
    <div id="{@componentId}Frame">
      <toobs:component componentId="{@componentId}" contentType="xhtml">
        <xsl:for-each select="/RuntimeLayout/Parameters/Parameter | ./parameters/parameter">
          <toobs:parameter use-context="true"/>
        </xsl:for-each>
      </toobs:component>
    </div>
  </xsl:template>

</xsl:stylesheet>
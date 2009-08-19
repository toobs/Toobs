<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:template match="component">
    <div id="profile-title">
      <xsl:value-of select="./objects/User/firstName"/>&#160;<xsl:value-of select="./objects/User/lastName"/>
    </div>
    <div class="title">
    Post a comment
    </div>
    <div id="post-area">
    <form method="post" action="/addPost">
    <input type="hidden" name="userId" value="{./objects/User/userId}" />
    <textarea class="inputpost" name="comment" rows="4" cols="50"></textarea>
    <input class="inputbutton" type="submit" value="Post"/>
    </form>
    </div>
  </xsl:template>

</xsl:stylesheet>
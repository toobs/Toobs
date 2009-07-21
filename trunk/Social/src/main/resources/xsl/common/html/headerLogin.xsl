<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:template match="component">
<div class="menubar-container">
  <span id="social-image-bottom-left-login"></span>
  <span id="social-image-med"></span>
  <span id="social-login-form">
    <form method="post" action="#">
      <table cellspacing="0" cellpadding="0">
        <tr>
          <td><label for="email">Email</label></td>
          <td><label for="password">Password</label></td>
        </tr>
        <tr>
          <td><input class="inputtext" type="text" name="email"/></td>
          <td><input class="inputtext" type="password" name="password"/></td>
          <td><input class="inputbutton" type="submit" value="Login"/></td>
        </tr>
        <tr>
          <td colspan="2"><input class="inputcheck" type="checkbox" name="remememberMe"/>Remember Me</td>
        </tr>
      </table>
    </form>
  </span>
  <span id="social-image-bottom-right-login"></span>
</div>
  </xsl:template>

  <xsl:template match="objects">
  </xsl:template>  

</xsl:stylesheet>
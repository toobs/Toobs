<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
  xmlns:toobs="xalan://org.toobsframework.pres.xsl.ComponentHelper"
  extension-element-prefixes="toobs"
  exclude-result-prefixes="toobs">
  <xsl:output method="html" omit-xml-declaration="yes"/>

  
  <xsl:template match="component">
<div id="sign-up-form">
  <div id="sign-up-header">Sign Up</div>
  <form method="post" action="signUp.xpost">
    <table>
      <tr><td class="sign-up-label"><label for="firstName">First Name</label></td><td class="sign-up-input"><input class="inputtext" type="text" name="firstName" vaule="" /></td></tr>
      <tr><td class="sign-up-label"><label for="lastName">Last Name</label></td><td class="sign-up-input"><input class="inputtext" type="text" name="lastName" vaule="" /></td></tr>
      <tr><td class="sign-up-label"><label for="emailName">Email</label></td><td class="sign-up-input"><input class="inputtext" type="text" name="email" vaule="" /></td></tr>
      <tr><td class="sign-up-label"><label for="password">Password</label></td><td class="sign-up-input"><input class="inputtext" type="password" name="password" vaule="" /></td></tr>
      <tr><td class="sign-up-label"></td><td class="sign-up-input"><input type="submit" class="inputbutton buttoncall" value="Sign Up" /></td></tr>
    </table>
  </form>
</div>
  </xsl:template>
  
</xsl:stylesheet>
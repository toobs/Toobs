<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:output method="html" omit-xml-declaration="yes"/>

  <xsl:template match="component">
<div class="menubar-std">
  <span id="social-image-bottom-left"></span>
  <span id="social-image-small"></span>
  <span id="social-menu-main-options">
    <a id="home-menu-entry" href="/">Home</a>
    <a id="profile-menu-entry" href="/profile">Profile</a>
    <a id="friends-menu-entry" href="/friends">Friends</a>
  </span>
  <span id="social-menu-break"></span>
  <span id="social-menu-secondary-options">
    <a id="settings-menu-entry" href="/settings">Settings</a>
    <a id="logout-menu-entry" href="/logout">Logout</a>
  </span>
  <span id="social-image-bottom-right"></span>
</div>
  </xsl:template>

  <xsl:template match="objects">
  </xsl:template>  

</xsl:stylesheet>
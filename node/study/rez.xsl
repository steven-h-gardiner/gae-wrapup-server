<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"		
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="text" indent="yes" encoding="utf-8"/>

  <xsl:param name="resolveBase">
    <xsl:value-of select="//html:link[@rel='canonical']/@href"/>
  </xsl:param>
  <xsl:param name="resolveServer">
    <xsl:value-of select="//html:link[@rel='canonical']/@href"/>
  </xsl:param>
  <xsl:param name="taskid">
    <xsl:text>TASKID</xsl:text>
  </xsl:param>
  
  <xsl:template match="/">
      <xsl:apply-templates select=".//html:link[@rel='stylesheet']/@href" mode="extract"/>
      <xsl:apply-templates select=".//html:img/@src" mode="extract"/>
      <xsl:apply-templates select=".//html:input[@type='image']/@src" mode="extract"/>
  </xsl:template>

  <xsl:template match="@*" mode="extract">
    <xsl:choose>
      <xsl:when test="preceding::html:*/@* = .">
      </xsl:when>
      <xsl:when test="true()">
	<xsl:value-of select="$resolveBase"/>
	<xsl:text>&#10;</xsl:text>
	<xsl:value-of select="."/>
	<xsl:text>&#10;</xsl:text>
	<xsl:value-of select="count(preceding::html:*)"/>
	<xsl:text>&#10;</xsl:text>
 	<xsl:value-of select="$taskid"/>
	<xsl:text>&#10;</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="string-replace-all">
    <xsl:param name="text" />
    <xsl:param name="replace" />
    <xsl:param name="by" />
    <xsl:choose>
      <xsl:when test="$text = '' or $replace = '' or not($replace)" >
        <!-- Prevent this routine from hanging -->
        <xsl:value-of select="$text" />
      </xsl:when>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text,$replace)" />
        <xsl:value-of select="$by" />
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text" select="substring-after($text,$replace)" />
          <xsl:with-param name="replace" select="$replace" />
          <xsl:with-param name="by" select="$by" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="@*" mode="resolve">
    <xsl:variable name="raw">
      <xsl:call-template name="string-replace-all">
        <xsl:with-param name="text" select="normalize-space(.)" />
        <xsl:with-param name="replace" select="'%20'" />
        <xsl:with-param name="by" select="''" />	
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="resolved">
      <xsl:choose>
	<xsl:when test="starts-with($raw, '%20http')">
	  <xsl:value-of select="$raw"/>
	</xsl:when>
	<xsl:when test="starts-with($raw, 'http')">
	  <xsl:value-of select="$raw"/>
	</xsl:when>
	<xsl:when test="starts-with($raw, '/')">
	  <xsl:value-of select="$resolveServer"/><xsl:value-of select="$raw"/>
	</xsl:when>
	<xsl:when test="true()">
	  <xsl:value-of select="$resolveBase"/><xsl:text></xsl:text><xsl:value-of select="$raw"/>
	</xsl:when>
      </xsl:choose>
    </xsl:variable>
    <!--
    <xsl:message>
	RESOLVED |<xsl:value-of select="$raw"/>| TO |<xsl:value-of select="$resolved"/>|
    </xsl:message>
    -->
    <xsl:value-of select="$resolved"/>
  </xsl:template>

  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

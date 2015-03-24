<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"
		xmlns:smartwrap="http://smartwrap.cmu.edu"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="ascii"/>

  <xsl:template match="html:div[@id='sw_selbox']" />
  <xsl:template match="html:table[@id='sw_tablebox']" />

  <xsl:param name="resolveBase">
    <xsl:value-of select="//html:link[@rel='canonical']/@href"/>
  </xsl:param>
  <xsl:param name="resolveServer">
    <xsl:value-of select="//html:link[@rel='canonical']/@href"/>
  </xsl:param>
	     
  <xsl:template match="html:*/@class[contains(., 'sw_inserted')]">
    <xsl:variable name="reclass">
      <xsl:value-of select="substring-before(., 'sw_inserted')"/>
      <xsl:value-of select="substring-after(., 'sw_inserted')"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$reclass = ''"/>
      <xsl:when test="true()">
	<xsl:attribute name="class">
	  <xsl:value-of select="$reclass"/>
	</xsl:attribute>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="attribute::smartwrap:*" />

  <xsl:template match="@*" mode="resolve">
    <xsl:variable name="raw">
      <xsl:value-of select="."/>
    </xsl:variable>
    <xsl:variable name="resolved">
      <xsl:choose>
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
    <xsl:message>
	RESOLVED |<xsl:value-of select="$raw"/>| TO |<xsl:value-of select="$resolved"/>|
    </xsl:message>
    
    <xsl:value-of select="$resolved"/>
  </xsl:template>
  
  <xsl:template match="html:link[@rel='stylesheet']/@href">
    <xsl:attribute name="href">
      <xsl:apply-templates select="." mode="resolve"/>
    </xsl:attribute>
  </xsl:template>
  <xsl:template match="html:img/@src">
    <xsl:attribute name="src">
      <xsl:apply-templates select="." mode="resolve"/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

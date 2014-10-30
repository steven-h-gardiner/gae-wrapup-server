<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"
		xmlns:smartwrap="http://smartwrap.cmu.edu"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:template match="html:div[@id='sw_selbox']" />
  <xsl:template match="html:table[@id='sw_tablebox']" />

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
  
  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

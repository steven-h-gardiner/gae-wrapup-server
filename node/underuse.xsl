<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:template match="html:*[@itemscope != '']">
    <xsl:variable name="tupleScope">
      <xsl:value-of select="@itemscope"/>
    </xsl:variable>
    <xsl:variable name="tupleSchema">
      <xsl:value-of select="@itemtype"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="not(preceding::html:*[@itemscope = $tupleScope])">
	<html:table>
	  <xsl:for-each select=".|following::html:*[@itemscope = $tupleScope]">
	    <html:tr>
	      <xsl:apply-templates select="." mode="row"/>
	    </html:tr>
	  </xsl:for-each>
	</html:table>
      </xsl:when>
      <xsl:when test="true()">
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="html:*" mode="row">
    <xsl:for-each select=".//html:*[@itemprop != '']">
      <html:td>
	<xsl:copy-of select="."/>
      </html:td>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

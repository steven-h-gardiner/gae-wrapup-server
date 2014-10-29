<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>
 
  <xsl:template match="html:table|html:tr">
    <html:div>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*|html:*"/>
    </html:div>
  </xsl:template>
  <xsl:template match="html:td|html:th">
    <html:span>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*|html:*|text()"/>
    </html:span>
  </xsl:template>

  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

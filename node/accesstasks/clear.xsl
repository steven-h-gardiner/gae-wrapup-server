<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="utf-8"/>

  <xsl:template match="@itemprop" priority="100"/>
  <xsl:template match="@itemscope" priority="100"/>

  <xsl:template match="html:*[html:div[@class='s']//html:span[@class='st']]">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="itemscope">true</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
  <xsl:template match="html:*[html:div[@class='s']//html:span[@class='st']]/html:div[@class='s']//html:*[@class='st']">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="itemprop">Snippet</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
  <xsl:template match="html:div[@class='s'][.//html:span/@class='st']//html:cite[@class='_Rm']">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="itemprop">URL</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
  <xsl:template match="html:*[html:div[@class='s']//html:span[@class='st']]//html:h3[@class='r']">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="itemprop">Title</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
  
  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

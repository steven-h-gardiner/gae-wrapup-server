<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
                xmlns:mixer="http://cmu.edu/mixer"
		xmlns:exslt="http://exslt.org/common"
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
	<xsl:variable name="columns">
	  <xsl:element name="columns">
	  <xsl:for-each select=".|following::html:*[@itemscope = $tupleScope]">
	    <xsl:for-each select=".//html:*[@itemprop != '']">
	      <xsl:variable name="propname">
		<xsl:value-of select="@itemprop"/>
	      </xsl:variable>
	      <xsl:choose>
		<xsl:when test="preceding::html:*[@itemprop = $propname]">
		  <!--<xsl:element name="skip"/>-->
		</xsl:when>
		<xsl:when test="true()">
		  <xsl:element name="column">
		    <xsl:attribute name="itemprop">
		      <xsl:value-of select="$propname"/>
		    </xsl:attribute>
		  </xsl:element>
		</xsl:when>
	      </xsl:choose>
	    </xsl:for-each>
	  </xsl:for-each>
	  </xsl:element>
	</xsl:variable>
	<xsl:message>
	  <xsl:element name="columns"/>
	</xsl:message>
	<html:table>
          <html:tr>
            <xsl:for-each select="exslt:node-set($columns)//column">
              <html:th>
                <xsl:value-of select="@itemprop"/>
              </html:th>
            </xsl:for-each>
          </html:tr>
	  <xsl:for-each select=".|following::html:*[@itemscope = $tupleScope]">
	    <html:tr>
	      <xsl:copy-of select="@itemscope"/>
	      <xsl:copy-of select="@itemtype"/>
	      <xsl:apply-templates select="." mode="row">
		<xsl:with-param name="columnGuide" select="$columns"/>
	      </xsl:apply-templates>
	    </html:tr>
	  </xsl:for-each>
	</html:table>
      </xsl:when>
      <xsl:when test="true()">
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="html:*" mode="row">
    <xsl:param name="columnGuide"/>
    <xsl:variable name="row" select="."/>
    <xsl:choose>
      <xsl:when test="not(exslt:node-set($columnGuide)//column)">
	<xsl:for-each select=".//html:*[@itemprop != '']">
	  <html:td>
	    <xsl:copy-of select="."/>
	  </html:td>
	</xsl:for-each>
      </xsl:when>
      <xsl:when test="true()">
	<xsl:for-each select="exslt:node-set($columnGuide)//column">
	  <xsl:variable name="itemprop">
	    <xsl:value-of select="./@itemprop"/>
	  </xsl:variable>
	  <xsl:choose>
	    <xsl:when test="$row//html:*[@itemprop = $itemprop]">
	      <html:td mixer:meta="injected">
		<xsl:apply-templates select="$row//html:*[@itemprop = $itemprop]" mode="cell"/>
	      </html:td>
	    </xsl:when>
	    <xsl:when test="true()">
	      <html:td>
		<xsl:comment>empty cell</xsl:comment>
	      </html:td>
	    </xsl:when>
	  </xsl:choose>
	</xsl:for-each>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="html:td" mode="cell" priority="1">
    <html:div mixer:meta="rewritten">
      <xsl:apply-templates select="@*" mode="cell"/>
      <xsl:apply-templates select="node()" mode="cell"/>
    </html:div>
  </xsl:template>
  <xsl:template match="html:*|@*|node()" mode="cell" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="cell"/>
      <xsl:apply-templates select="node()" mode="cell"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
		xmlns:html="http://www.w3.org/1999/xhtml"
		xmlns:smartwrap="http://smartwrap.cmu.edu"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="ascii"/>

  <xsl:template match="html:div[@id='sw_selbox']" />
  <xsl:template match="html:table[@id='sw_tablebox']" />
  <xsl:template match="html:div[@id='rhs']" />
  <xsl:template match="html:div[@class='rightContainer']" >
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:attribute name="style">width: inherit</xsl:attribute>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

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

  <xsl:template match="html:link[@rel='stylesheet'][contains(@href, 'global-typography')]" priority="100"/>
  
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

  <xsl:template match="html:head">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
      <html:script src="http://code.jquery.com/jquery-2.1.3.min.js">
      </html:script>
      <html:script>
	<xsl:text>
<![CDATA[
	 jQuery(document).on('ready', function() {
	   var kl = {};
	   kl.keystrokes = [];
	   kl.url = location.href;
	   jQuery(document).on('keydown', function(evt) {
	     if (evt.which < 20) { return; }
	     kl.keystrokes.push({key:evt.key,which:evt.which,code:evt.code});
	   });
	   jQuery(document).on('keydown', function(evt) {
	     if ((evt.key && (evt.key === 'Insert')) || (evt.which === 45)) {
	       jQuery(document).data('insertpressed', true);
	     }
	   });
	   jQuery(document).on('keyup', function(evt) {
	     if ((evt.key && (evt.key === 'Insert')) || (evt.which === 45)) {
	       jQuery(document).removeData('insertpressed');
	     }
	   });
	   jQuery(document).on('keydown', function(evt) {
	     if ((evt.which <= 40) && (evt.which >= 33)) {
	       if (evt.ctrlKey && evt.altKey) {
		 jQuery(document).trigger('tablecmd', [{message:'tablecmd',key:(evt.key || evt.which)}]);
	       }
	       if (jQuery(document).data('insertpressed')) {
		 jQuery(document).trigger('tablecmd', [{message:'tablecmd',key:(evt.key || evt.which)}]);
	       }
	     }
	   });
	   jQuery(document).on('tablecmd', function(evt, detail) {
	       console.log('hi: ' + JSON.stringify(kl, null, 2));
	       jQuery.ajax({url:'/access/log',
		     data: { taskurl: location.href,
		       eventname: 'tablekey',
		       key: evt.key || detail.key 
		       }		  
	       });
	   });
	   setInterval(function() { jQuery(document).trigger('keyevent'); }, 1000);
	   jQuery(document).on('keyevent', function(evt) {
	       if (kl.keystrokes.length === 0) { return; }
	       jQuery.ajax({url:'/access/log',
		     data: { taskurl: location.href,
		       eventname: 'keyevent',
		       keystrokes: kl.keystrokes
		       }		  
	       });
	       kl.keystrokes = [];
	   });
	 });
]]>      
	</xsl:text>
      </html:script>
    </xsl:copy>	
  </xsl:template>

  <xsl:template match="html:iframe" />
  <xsl:template match="html:link[contains(@href, '/cache/minify/000000')]" />
  <xsl:template match="html:link[contains(@href, 'ui.racingpost.com/release/v131')]" />

  <xsl:template match="html:script[contains(@src, 'maps.googleapis.com')]" />

  <xsl:template match="html:script[contains(., 'agent-list-container')]" />

  <xsl:template match="html:table[contains(@class, 'fruit_nutritional_desktop')][1]" />
  <xsl:template match="html:table[contains(@class, 'fruit_nutritional_desktop')][3]" />
  <xsl:template match="html:table[contains(@class, 'fruit_nutritional_mobile')]" />

  <xsl:template match="html:body[@background='Images/greystripes.gif']//html:div[@id='menu']" />
  <xsl:template match="html:body[@background='Images/greystripes.gif']//html:table//html:td/html:div/html:table[position() > 1]" />
  
  <xsl:template match="html:div[@id='sidebarleft']" />
  <xsl:template match="html:link[contains(@href,'wp-jazz/style.php')]" />

  <xsl:template match="html:div[@class='filter-panel']/html:div[@class='suggested-filters']" />

  <xsl:template match="html:div[@id='divContentNav']/html:div[@id='divNavCommon']" />
  <xsl:template match="html:div[@id='divProductSort1']" />

  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

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

  <xsl:param name="disableLinks" select="'css'"/>
  
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
  <xsl:template match="html:input[@type='image']/@src">
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
      <html:style>
	#parktrailsdropdown-block-form { color: red; }
      </html:style>
      <xsl:choose>
	<xsl:when test="$disableLinks = 'css'">
	  <html:style>
	    a { pointer-events: none; }
	  </html:style>
	</xsl:when>
      </xsl:choose>
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

  <xsl:template match="html:form[@action='/parks-and-trails']" />
  <xsl:template match="html:form[@id='parktrailsdropdown-block-form']" />

  <xsl:template match="html:form[@id='headerSearchBoxForm']" />

  <xsl:template match="html:nav[@id='nav']" />

  <xsl:template match="html:map[@name='rage_image_map']" />
  
  <xsl:template match="html:form[@action='/shows/that-70s-show/season-8/']" />
  <xsl:template match="html:link[contains(@href,'CACHE/css/9447')]" />

  <xsl:template match="html:a/@draggable" />

  <xsl:template match="html:div[@class='_bento']//html:div[@class='controls']" />
  
  <xsl:template match="html:a[@href!='']" priority="-10">
    <xsl:choose>
      <xsl:when test="$disableLinks='relabel'">
	<xsl:element name="html:span">
	  <xsl:apply-templates select="@*"/>
	  <xsl:apply-templates select="node()"/>
	</xsl:element>
      </xsl:when>
      <xsl:when test="true()">
	<xsl:copy>
	  <xsl:apply-templates select="@*"/>
	  <xsl:apply-templates select="node()"/>
	</xsl:copy>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="/|*|@*|text()|node()" priority="-100">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>	
  </xsl:template>
</xsl:stylesheet>

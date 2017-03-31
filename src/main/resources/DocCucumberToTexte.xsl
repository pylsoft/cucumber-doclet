<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="text" encoding="utf-8" indent="yes"/>

	<xsl:variable name='newline'><xsl:text>&#10;</xsl:text></xsl:variable>
	<xsl:variable name='tab'><xsl:text>&#09;</xsl:text></xsl:variable>

	<xsl:template match="/">
		<xsl:apply-templates select="JAVADOC" />
	</xsl:template>

	<xsl:template match="JAVADOC">
Liste des phrases exécutables utilisées par le projet Prestation
----------------------------------------------------------------
<xsl:apply-templates select="//FONCTION" />

Liste des phrases obsolètes à ne plus utiliser
----------------------------------------------
<xsl:apply-templates select="//FONCTION[@Deprecated]/ANNOTATION" />

	</xsl:template>
	
	<xsl:template match="FONCTION">
<xsl:apply-templates select="TAG[@nom='example']" />
	</xsl:template>
	
	<xsl:template match="TAG"> 
<xsl:value-of select="$newline" />
<xsl:apply-templates select="../COMMENTAIRE//LIGNE" mode="commentaire"/>
<xsl:apply-templates select="LIGNE" mode="tag"/>
	</xsl:template>

	<xsl:template match="LIGNE" mode="tag">
		<xsl:if test="contains(.,'|')" >
<xsl:value-of select="$tab" />
		</xsl:if> 
<xsl:value-of select="concat(normalize-space(.),$newline)" />
	</xsl:template>
	
	<xsl:template match="LIGNE" mode="commentaire">
<xsl:value-of select="concat('# ', normalize-space(.), $newline)" />
	</xsl:template>

	<xsl:template match="ANNOTATION">
<xsl:value-of select="concat(PHRASE,$newline)" />
	</xsl:template>
	
</xsl:stylesheet>
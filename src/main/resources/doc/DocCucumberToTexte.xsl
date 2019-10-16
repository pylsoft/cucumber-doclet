<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" encoding="utf-8" indent="yes"/>

    <xsl:variable name='newline'>
        <xsl:text>&#10;</xsl:text>
    </xsl:variable>
    <xsl:variable name='tab'>
        <xsl:text>&#09;</xsl:text>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates select="JAVADOC"/>
    </xsl:template>

    <xsl:template match="JAVADOC">
        Doclet Cucumber Version : <xsl:value-of select="@docletVersion"/>
        Date de Génération : <xsl:value-of select="@date"/>
        Projet : <xsl:value-of select="@projet"/>

        Liste des annotations
        ---------------------
        @ARealiser # TA à réaliser par le développeur
        @EnRealisation # TA en cours de réalisation par le développeur
        @ARelire # TA à relire par un fonctionnel
        @ARecetter # TA à recetter par un fonctionnel

        Liste des phrases exécutables de type SOIT = état existant, données fixes ou présentes en base
        ----------------------------------------------------------------------------------------------
        <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION/@nom = 'Soit']"/>

        Liste des phrases exécutables de type ETANT DONNE : Données passées en paramètre
        --------------------------------------------------------------------------------
        <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION/@nom = 'Etantdonné']"/>

        Liste des phrases exécutables de type QUAND : Action, appel du service
        ----------------------------------------------------------------------
        <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION/@nom = 'Quand']"/>

        Liste des phrases exécutables de type ALORS : Résultat attendu
        --------------------------------------------------------------
        <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION/@nom = 'Alors']"/>

        Liste des phrases obsolètes à ne plus utiliser
        ----------------------------------------------
        <xsl:apply-templates select="//FONCTION[@Deprecated]/ANNOTATION" mode="deprecated"/>

    </xsl:template>

    <xsl:template match="FONCTION">
        <xsl:apply-templates select="TAG[@nom='example']"/>
    </xsl:template>

    <xsl:template match="TAG">
        <xsl:value-of select="$newline"/>
        <xsl:apply-templates select="../COMMENTAIRE//LIGNE" mode="commentaire"/>
        <xsl:apply-templates select="LIGNE" mode="tag"/>
    </xsl:template>

    <xsl:template match="LIGNE" mode="tag">
        <xsl:if test="contains(.,'|')">
            <xsl:value-of select="$tab"/>
        </xsl:if>
        <xsl:value-of select="concat(.,$newline)"/>
    </xsl:template>

    <xsl:template match="LIGNE" mode="commentaire">
        <xsl:value-of select="concat('# ', normalize-space(.), $newline)"/>
    </xsl:template>

    <xsl:template match="ANNOTATION" mode="deprecated">
        <xsl:value-of select="concat(@nom,' ',PHRASE,$newline)"/>
    </xsl:template>

</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" version="5.0" encoding="utf-8" indent="yes" omit-xml-declaration="yes" doctype-system="about:legacy-compat"/>

    <xsl:variable name="guillemet">"</xsl:variable>
    <xsl:variable name="apostrophe">'</xsl:variable>
    <xsl:variable name="doubleApostrophe">''</xsl:variable>

    <xsl:variable name="SOIT">Soit</xsl:variable>
    <xsl:variable name="ETANTDONNE">Etantdonné</xsl:variable>
    <xsl:variable name="ALORS">Alors</xsl:variable>
    <xsl:variable name="QUAND">Quand</xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates select="JAVADOC"/>
    </xsl:template>

    <xsl:template match="JAVADOC">
        <html lang="fr">
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Liste des phrases exécutables Cucumber du projet PrestationBack</title>
                <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css"/>
                <link rel="stylesheet" href="/resources/demos/style.css"/>
                <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
                <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
                <script>
                    $( function() {
                    function Annotation () {
                    this.id = "";
                    this.commentaires = new Array();
                    this.exemples = new Array();
                    this.classe = "";
                    this.fonction = "";
                    this.params = new Array();
                    this.libelle = "";
                    this.phrases = new Array();
                    this.deprecated = false;
                    }
                    function Param () {
                    this.libelle = "";
                    this.type = "";
                    }

                    var annotations = new Array();

                    <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$SOIT]]" mode="script">
                        <xsl:with-param name="typeAnnotation" select="$SOIT"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[contains(@nom,$ETANTDONNE)]]" mode="script">
                        <xsl:with-param name="typeAnnotation" select="$ETANTDONNE"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$QUAND]]" mode="script">
                        <xsl:with-param name="typeAnnotation" select="$QUAND"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$ALORS]]" mode="script">
                        <xsl:with-param name="typeAnnotation" select="$ALORS"/>
                    </xsl:apply-templates>

                    function afficherPhrase(result, annotation) {
                    result.empty();
                    newH3 = document.createElement( "h3" );
                    if (annotation.deprecated) {
                    newS = document.createElement( "s" );
                    newS.append(annotation.phrases[0]);
                    newH3.append(newS);
                    } else {
                    newH3.append(annotation.phrases[0]);
                    }
                    result.append(newH3);

                    newP = document.createElement( "p" );
                    annotation.commentaires.forEach(function(commentaire) {
                    newI = document.createElement( "i" );
                    newBR = document.createElement( "br" );
                    newI.append(commentaire);
                    newP.append(newI);
                    newP.append(newBR);
                    });
                    result.append(newP);

                    newP = document.createElement( "p" );
                    newU = document.createElement( "u" );
                    newB = document.createElement( "b" );
                    newB.append("Liste des phrases possibles :");
                    newU.append(newB);
                    newP.append(newU)
                    result.append(newP);

                    newUL = document.createElement( "ul" );
                    annotation.phrases.forEach(function(element) {
                    newLI = document.createElement( "li" );
                    newLI.append(element);
                    newUL.append(newLI);
                    });
                    result.append(newUL);

                    newP = document.createElement( "p" );
                    newU = document.createElement( "u" );
                    newB = document.createElement( "b" );
                    newB.append("Exemple :");
                    newU.append(newB);
                    newP.append(newU);
                    newButton = document.createElement( "button" );
                    newButton.id = "idBoutonCopier";
                    newButton.append("Copier");
                    newP.append(newButton);
                    result.append(newP);

                    newDIV = document.createElement( "div" );
                    newDIV.id = "divExemple";
                    newP = document.createElement( "p" );
                    annotation.exemples.forEach(function(exemple) {
                    newBR = document.createElement( "br" );
                    newP.append(exemple.replace(/ /g, "&#160;"));
                    newP.append(newBR);
                    });
                    newDIV.append(newP);
                    result.append(newDIV);

                    newTextArea = document.createElement( "textarea" );
                    newTextArea.id = "textareaExemple";
                    valeurTextArea = "";
                    annotation.exemples.forEach(function(exemple) {
                    valeurTextArea += "\n" + exemple;
                    });
                    newTextArea.append(valeurTextArea);
                    result.append(newTextArea);

                    $( "#idBoutonCopier" ).click( function( event ) {
                    $("#textareaExemple").toggle(true);
                    $("#textareaExemple").select();
                    document.execCommand( 'copy' );
                    $("#textareaExemple").toggle(false);

                    return false;
                    } );

                    }

                    $( "#tabs" ).tabs();

                    $( "#phrases" ).autocomplete({
                    source: Object.values(annotations),
                    select: function( event, ui ) {
                    afficherPhrase($( "#divDetailRecherche" ), annotations[ui.item.id]);
                    $(this).val(''); return false;
                    }
                    })

                    $( "#menu" ).menu(
                    { items: "> :not(.ui-widget-header)"},
                    { select: function( event, ui ) {
                    afficherPhrase($( "#divDetail" ), annotations[ui.item.context.id]); }
                    }
                    );

                    } );
                </script>
                <style>
                    .ui-menu { width: 50em; }
                    .ui-widget-header { padding: 0.2em; }
                    #phrases { width:80em; }
                    #tabs { height:46em;}
                    #divMenu { float:left; height: 37em; overflow-y:auto;overflow-x:hidden; }
                    #divDetail { float:left; border: 1px black solid; margin: 0 1em 0 1em; padding:0 0.5em 0 0.5em;width:60em;overflow-x:scroll; }
                    #divDetailRecherche { border: 1px black solid; margin: 1em 1em 0 1em; padding:0 0.5em 0 0.5em;overflow-x:hidden; }
                    #divExemple { font-family:Courier New; font-size:small; }
                    #textareaExemple { display: none; }
                    #divInfo { position:absolute; bottom:10px; right:10px; z-index:1;
                    width:17em;height:5em;padding-left:1em; padding-top:0.3em;
                    font-weight :bold; border:1px solid black;}
                </style>
            </head>
            <body>
                <div id="divInfo">
                    <b>
                        Doclet Version :
                        <xsl:value-of select="@docletVersion"/>
                        <br/>
                        Date de génération :
                        <xsl:value-of select="@date"/>
                        <br/>
                        Projet : <xsl:value-of select="@projet"/>
                    </b>
                </div>
                <div id="tabs">
                    <ul>
                        <li>
                            <a href="#tabs-1">Recherche</a>
                        </li>
                        <li>
                            <a href="#tabs-2">Liste</a>
                        </li>
                    </ul>
                    <div id="tabs-1" class="ui-widget">
                        <label for="phrases">Rechercher une phrase du projet <xsl:value-of select="@projet"/> :</label>
                        <input id="phrases"/>
                        <div id="divDetailRecherche"/>
                    </div>
                    <div id="tabs-2">
                        <h1>Liste des phrases exécutables Cucumber du projet <xsl:value-of select="@projet"/></h1>
                        <div id="divMenu">
                            <ul id="menu">
                                <li class="ui-widget-header">
                                    <div>
                                        Soit (<xsl:value-of select="RESUME/ANNOTATION[@nom=$SOIT]/@nbPhrases"/> phrases) : Prépare la base de données
                                        pour l'avant
                                    </div>
                                </li>
                                <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$SOIT]]" mode="html">
                                    <xsl:with-param name="typeAnnotation" select="$SOIT"/>
                                </xsl:apply-templates>
                                <li class="ui-widget-header">
                                    <div>
                                        Etant donné (<xsl:value-of select="RESUME/ANNOTATION[contains(@nom,$ETANTDONNE)]/@nbPhrases"/> phrases) :
                                        Prépare les
                                        données pour l'action
                                    </div>
                                </li>
                                <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[contains(@nom,$ETANTDONNE)]]" mode="html">
                                    <xsl:with-param name="typeAnnotation" select="$ETANTDONNE"/>
                                </xsl:apply-templates>
                                <li class="ui-widget-header">
                                    <div>
                                        Quand (<xsl:value-of select="RESUME/ANNOTATION[@nom=$QUAND]/@nbPhrases"/> phrases) : lance l'action
                                    </div>
                                </li>
                                <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$QUAND]]" mode="html">
                                    <xsl:with-param name="typeAnnotation" select="$QUAND"/>
                                </xsl:apply-templates>
                                <li class="ui-widget-header">
                                    <div>
                                        Alors (<xsl:value-of select="RESUME/ANNOTATION[@nom=$ALORS]/@nbPhrases"/> phrases) : Vérifie le résultat
                                    </div>
                                </li>
                                <xsl:apply-templates select="//FONCTION[not(@Deprecated) and ANNOTATION[@nom=$ALORS]]" mode="html">
                                    <xsl:with-param name="typeAnnotation" select="$ALORS"/>
                                </xsl:apply-templates>
                                <li class="ui-widget-header">
                                    <div>
                                        Deprecated (<xsl:value-of select="RESUME/ANNOTATION[@nom='Deprecated']/@nbPhrases"/> phrases) : Phrase à ne
                                        plus utiliser
                                    </div>
                                </li>
                                <xsl:apply-templates select="//FONCTION[@Deprecated]/ANNOTATION" mode="deprecated"/>

                            </ul>
                        </div>
                        <div id="divDetail"/>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="FONCTION" mode="html">
        <xsl:param name="typeAnnotation"/>
        <li>
            <xsl:attribute name="id">
                <xsl:value-of select="concat('idAnnotation-',$typeAnnotation,'-',position())"/>
            </xsl:attribute>
            <xsl:apply-templates select="ANNOTATION" mode="html"/>
        </li>
    </xsl:template>
    <xsl:template match="ANNOTATION" mode="html">
        <div>
            <b>
                <xsl:value-of select="@nom"/>
            </b> &#160;
            <i>
                <xsl:value-of select="translate(PHRASE,'\','')"/>
            </i>
        </div>
    </xsl:template>
    <xsl:template match="ANNOTATION" mode="deprecated">
        <div>
            <s>
                <b>
                    <xsl:value-of select="@nom"/>
                </b> &#160;
                <i>
                    <xsl:value-of select="translate(PHRASE,'\','')"/>
                </i>
            </s>
        </div>
    </xsl:template>

    <xsl:template match="FONCTION" mode="script">
        <xsl:param name="typeAnnotation"/>

        <xsl:apply-templates select="ANNOTATION" mode="script">
            <xsl:with-param name="idAnnotation" select="concat('idAnnotation-',$typeAnnotation,'-',position())"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="ANNOTATION" mode="script">
        <xsl:param name="idAnnotation"/>
        var annotation = new Annotation;
        annotation.classe = "<xsl:value-of select="../../@nom"/>";
        annotation.fonction = "<xsl:value-of select="../@nom"/>";

        <xsl:if test="../@Deprecated">
            annotation.deprecated = true;
        </xsl:if>

        <xsl:apply-templates select="../COMMENTAIRE" mode="script"/>
        <xsl:apply-templates select="../TAG[@nom='example']" mode="script"/>
        <xsl:apply-templates select="PARAM" mode="script"/>
        annotation.libelle = "<xsl:value-of select="concat(@nom,' ',@phrase)"/>";
        annotation.id = "<xsl:value-of select="$idAnnotation"/>";
        annotation.label = "<xsl:value-of select="concat(@nom,' ',PHRASE)"/>";
        annotations[annotation.id] = annotation;
        <xsl:apply-templates select="PHRASE" mode="script"/>
    </xsl:template>
    <xsl:template match="COMMENTAIRE" mode="script">
        <xsl:for-each select="LIGNE">
            annotation.commentaires.push("<xsl:value-of select="."/>");
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="TAG" mode="script">
        <xsl:for-each select="LIGNE">
            annotation.exemples.push("<xsl:value-of select="translate(.,$guillemet,$doubleApostrophe)"/>");
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="PARAM" mode="script">
        var param = new Param;
        param.libelle = "<xsl:value-of select="@nom"/>;
        param.type = "<xsl:value-of select="@type"/>;
        annotation.params.push(param);
    </xsl:template>
    <xsl:template match="PHRASE" mode="script">
        annotation.phrases.push("<xsl:value-of select="concat(../@nom,' ', .)"/>");
    </xsl:template>

</xsl:stylesheet>
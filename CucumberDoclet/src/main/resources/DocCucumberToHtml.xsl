<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" version="5.0" encoding="utf-8"
		indent="yes" omit-xml-declaration="yes" doctype-system="about:legacy-compat" />

	<xsl:template match="/">
		<xsl:apply-templates select="JAVADOC" />
	</xsl:template>

	<xsl:template match="JAVADOC">
		<html lang="fr">
			<head>
				<meta charset="utf-8" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<title>Liste des phrases exécutables Cucumber du projet PrestationBack</title>
				<link rel="stylesheet"
					href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" />
				<link rel="stylesheet" href="/resources/demos/style.css" />
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
						
						<xsl:apply-templates select="CLASSE"  mode="script" />
				
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
							newP.append(newU)
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
					#tabs { height:46em; }
					#divMenu { float:left; height: 37em; overflow-y:auto;overflow-x:hidden; }
					#divDetail { float:left; border: 1px black solid; margin: 0 1em 0 1em; padding:0 0.5em 0 0.5em;width:41em;overflow-x:hidden; }
					#divDetailRecherche { border: 1px black solid; margin: 1em 1em 0 1em; padding:0 0.5em 0 0.5em;overflow-x:hidden; }
					#divExemple { font-family:Courier New; font-size:small; }
				</style>
			</head>
			<body>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1">Recherche</a></li>
						<li><a href="#tabs-2">Liste</a></li>
					</ul>
					<div id="tabs-1" class="ui-widget">
						<label for="phrases">Rechercher une phrase : </label>
  						<input id="phrases" />
						<div id="divDetailRecherche" />
					</div>
					<div id="tabs-2">
						<h1>Liste des phrases exécutables Cucumber du projet PrestationBack</h1>
						<div id="divMenu">
							<ul id="menu">
								<xsl:apply-templates select="CLASSE"  mode="html" />
							</ul>
						</div>
						<div id="divDetail" />
					</div>
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="CLASSE" mode="html">
		<li class="ui-widget-header">
			<div>
				<xsl:value-of select="@nom" />
			</div>
		</li>
		<xsl:apply-templates select="FONCTION" mode="html">
			<xsl:with-param name="classePosition" select="position()" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="FONCTION" mode="html">
	<xsl:param name="classePosition" />
		<li><xsl:attribute name="id">idAnnotation-<xsl:value-of select="$classePosition" />-<xsl:value-of select="position()" /></xsl:attribute>
			<xsl:apply-templates select="ANNOTATION"  mode="html" />
		</li>
	</xsl:template>
	<xsl:template match="ANNOTATION" mode="html">
		<xsl:variable name= "nomPhrase">
			<b><xsl:value-of select="@nom" /></b>
			&#160;
			<i><xsl:value-of select="translate(PHRASE,'\','')" /></i>
		</xsl:variable>
		<div>
			<xsl:choose>
			  <xsl:when test="../@Deprecated">
			    <s><xsl:copy-of select="$nomPhrase" /></s>
			  </xsl:when>
			  <xsl:otherwise>
			    <xsl:copy-of select="$nomPhrase" />
			  </xsl:otherwise>
			</xsl:choose> 
		</div>
	</xsl:template>

	<xsl:template match="CLASSE" mode="script">
		<xsl:apply-templates select="FONCTION" mode="script">
			<xsl:with-param name="classePosition" select="position()" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="FONCTION" mode="script">
	<xsl:param name="classePosition" />
		<xsl:apply-templates select="ANNOTATION" mode="script">
			<xsl:with-param name="idAnnotation" select="concat('idAnnotation-',$classePosition,'-',position())" />
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="ANNOTATION" mode="script">
	<xsl:param name="idAnnotation" />
		var annotation = new Annotation;
		annotation.classe = "<xsl:value-of select="../../@nom" />";
		annotation.fonction = "<xsl:value-of select="../@nom" />";

	    <xsl:if test="../@Deprecated">
	    	annotation.deprecated = true;
	    </xsl:if>
		
		<xsl:apply-templates select="../COMMENTAIRE" mode="script" />		
		<xsl:apply-templates select="../TAG[@nom='example']" mode="script" />		
		<xsl:apply-templates select="PARAM" mode="script" />
		annotation.libelle = "<xsl:value-of select="concat(@nom,' ',@phrase)" />";
		annotation.id = "<xsl:value-of select="$idAnnotation" />";
		annotation.label = "<xsl:value-of select="concat(@nom,' ',PHRASE)" />";
		annotations[annotation.id] = annotation;
		<xsl:apply-templates select="PHRASE" mode="script" />		
	</xsl:template>
	<xsl:template match="COMMENTAIRE" mode="script">	
		<xsl:for-each select="LIGNE">
			annotation.commentaires.push("<xsl:value-of select="." />");
		</xsl:for-each> 
	</xsl:template>
	<xsl:template match="TAG" mode="script">
		<xsl:for-each select="LIGNE">
			annotation.exemples.push("<xsl:value-of select="." />");
		</xsl:for-each> 
	</xsl:template>
	<xsl:template match="PARAM" mode="script">
		var param = new Param;
		param.libelle = "<xsl:value-of select="@nom" />;
		param.type = "<xsl:value-of select="@type" />;
		annotation.params.push(param);
	</xsl:template>
	<xsl:template match="PHRASE" mode="script">
		annotation.phrases.push("<xsl:value-of select="concat(../@nom,' ', .)" />");
	</xsl:template>

</xsl:stylesheet>
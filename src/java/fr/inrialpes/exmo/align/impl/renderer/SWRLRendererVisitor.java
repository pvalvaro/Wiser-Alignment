/*
 * $Id: SWRLRendererVisitor.java 2116 2016-09-19 08:38:32Z euzenat $
 *
 * Copyright (C) INRIA, 2003-2004, 2007-2010, 2012-2016
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */

package fr.inrialpes.exmo.align.impl.renderer; 

import java.util.Properties;
import java.io.PrintWriter;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;
import org.semanticweb.owl.align.Relation;

import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;

import fr.inrialpes.exmo.ontowrap.LoadedOntology;
import fr.inrialpes.exmo.ontowrap.OntowrapException;

/**
 * Renders an alignment as a SWRL rule set interpreting
 *.data of the first ontology into the second one.
 *
 * @author J�r�me Euzenat
 * @version $Id: SWRLRendererVisitor.java 2116 2016-09-19 08:38:32Z euzenat $ 
 */

public class SWRLRendererVisitor extends IndentedRendererVisitor implements AlignmentVisitor {
    final static Logger logger = LoggerFactory.getLogger( SWRLRendererVisitor.class );

    Alignment alignment = null;
    LoadedOntology<Object> onto1 = null;
    LoadedOntology<Object> onto2 = null;
    Cell cell = null;
    boolean embedded = false; // if the output is XML embeded in a structure

    public SWRLRendererVisitor( PrintWriter writer ){
	super( writer );
    }

    public void init( Properties p ) {
	super.init( p );
	if ( p.getProperty( "embedded" ) != null 
	     && !p.getProperty( "embedded" ).equals("") ) embedded = true;
   };

    public void visit( Alignment align ) throws AlignmentException {
	if ( subsumedInvocableMethod( this, align, Alignment.class ) ) return;
	// default behaviour
	if ( align instanceof ObjectAlignment ) {
	    alignment = align;
	} else {
	    try {
		alignment = ObjectAlignment.toObjectAlignment( (URIAlignment)align );
	    } catch ( AlignmentException alex ) {
		throw new AlignmentException("SWRLRenderer: cannot render simple alignment. Need an ObjectAlignment", alex );
	    }
	}
	onto1 = ((ObjectAlignment)alignment).getOntologyObject1();
	onto2 = ((ObjectAlignment)alignment).getOntologyObject2();
	if ( embedded == false )
	    indentedOutput("<?xml version=\"1.0\" encoding=\""+ENC+"\"?>"+NL+NL);
	indentedOutput("<swrlx:Ontology swrlx:name=\"generatedAl\""+NL);
	String indentString = INDENT;
	setIndentString( "                " );
	increaseIndent();
	indentedOutput("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+NL);
	indentedOutput("xmlns:swrlx=\"http://www.w3.org/2003/11/swrlx#\""+NL);
	indentedOutput("xmlns:owlx=\"http://www.w3.org/2003/05/owl-xml\""+NL);
	indentedOutput("xmlns:ruleml=\"http://www.w3.org/2003/11/ruleml#\">"+NL);
	decreaseIndent();
	setIndentString( "  " );
	indentedOutput(NL);
	increaseIndent();
	indentedOutput("<!-- Generated by fr.inrialpes.exmo.impl.renderer.SWRLRendererVisitor -->"+NL);
	for ( String[] ext : align.getExtensions() ){
	    indentedOutput("<owlx:Annotation><owlx:Documentation>"+ext[1]+": "+ext[2]+"</owlx:Documentation></owlx:Annotation>"+NL);
	}
	indentedOutput(NL);
	indentedOutput("<owlx:Imports rdf:resource=\""+onto1.getURI()+"\"/>"+NL);
	indentedOutput(NL);
	for( Cell c : alignment ){
	    c.accept( this );
	}
	decreaseIndent();
	indentedOutput("</swrlx:Ontology>"+NL);
    }

    public void visit( Cell cell ) throws AlignmentException {
	if ( subsumedInvocableMethod( this, cell, Cell.class ) ) return;
	// default behaviour
	this.cell = cell;
	Object ob1 = cell.getObject1();
	Object ob2 = cell.getObject2();
	Relation rel = cell.getRelation();
	if ( RelationTransformer.isEquivalence( rel ) ) {
	    generateImplication( ob1, ob2 );
	    generateImplication( ob2, ob1 );
	} else if ( RelationTransformer.isSubsumedOrEqual( rel ) ) {
	    generateImplication( ob1, ob2 );
	} else if ( RelationTransformer.subsumesOrEqual( rel ) ) {
	    generateImplication( ob2, ob1 );
	} else if ( RelationTransformer.isDisjoint( rel ) ) {
	    generateIncompatibility( ob1, ob2 );
	} else rel.accept( this );
	// SWRL
	// isInstanceOf(), hasInstance()
    }

    public void generateImplication( Object ob1, Object ob2 ) throws AlignmentException {
	// JE: We should send warnings when dataproperties are mapped to individual properties and vice versa...
	try {
	    URI uri1 = onto1.getEntityURI( ob1 );
	    indentedOutput("<ruleml:imp>"+NL);
	    increaseIndent();
	    indentedOutput("<ruleml:_body>"+NL);
	    if ( onto1.isClass( ob1 ) ){
		increaseIndent();
		indentedOutput("<swrl:classAtom>"+NL);
		increaseIndent();
		indentedOutput("<owllx:Class owllx:name=\""+uri1+"\"/>"+NL);
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:classAtom>"+NL);
		decreaseIndent();
	    } else if ( onto1.isDataProperty( ob1 ) ){
		increaseIndent();
		indentedOutput("<swrl:datavaluedPropertyAtom swrlx:property=\""+uri1+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("<swrl:datavaluedPropertyAtom>"+NL);
		decreaseIndent();
	    } else {
		increaseIndent();
		indentedOutput("<swrl:individualPropertyAtom swrlx:property=\""+uri1+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:individualPropertyAtom>"+NL);
		decreaseIndent();
	    }
	    indentedOutput("</ruleml:_body>"+NL);
	    indentedOutput("<ruleml:_head>"+NL);
	    URI uri2 = onto2.getEntityURI( ob2 );
	    if ( onto2.isClass( ob2 ) ){
		increaseIndent();
		indentedOutput("<swrlx:classAtom>"+NL);
		increaseIndent();
		indentedOutput("<owllx:Class owllx:name=\""+uri2+"\"/>"+NL);
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:classAtom>"+NL);
		decreaseIndent();
	    } else if ( onto2.isDataProperty( ob2 )  ){
		increaseIndent();
		indentedOutput("<swrl:datavaluedPropertyAtom swrlx:property=\""+uri2+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:datavaluedPropertyAtom>"+NL);
		decreaseIndent();
	    } else {
		increaseIndent();
		indentedOutput("<swrl:individualPropertyAtom swrlx:property=\""+uri2+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:individualPropertyAtom>"+NL);
		decreaseIndent();
	    }
	    indentedOutput("</ruleml:_head>"+NL);
	    decreaseIndent();
	    indentedOutput("</ruleml:imp>"+NL);
	    outputln();
	} catch ( OntowrapException owex ) {
	    throw new AlignmentException( "Error accessing ontology", owex );
	}
    }

    public void generateIncompatibility( Object ob1, Object ob2 ) throws AlignmentException {
	try {
	    URI uri1 = onto1.getEntityURI( ob1 );
	    URI uri2 = onto2.getEntityURI( ob2 );
	    indentedOutput("<ruleml:imp>"+NL);
	    increaseIndent();
	    if ( onto1.isClass( ob1 ) && onto2.isClass( ob2 ) ){
		indentedOutput("<ruleml:_body>"+NL);
		increaseIndent();
		indentedOutput("<swrl:classAtom>"+NL);
		increaseIndent();
		indentedOutput("<owllx:Class owllx:name=\""+uri1+"\"/>"+NL);
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:classAtom>"+NL);
		indentedOutput("<swrlx:classAtom>"+NL);
		increaseIndent();
		indentedOutput("<owllx:Class owllx:name=\""+uri2+"\"/>"+NL);
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:classAtom>"+NL);
		decreaseIndent();
		indentedOutput("</ruleml:_body>"+NL);
		indentedOutput("<ruleml:_head>"+NL);
		increaseIndent();
		indentedOutput("<swrlx:classAtom>"+NL);
		increaseIndent();
		indentedOutput("<owllx:Class owllx:name=\"owl:Nothing\"/>"+NL);
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:classAtom>"+NL);
		decreaseIndent();
		indentedOutput("</ruleml:_head>"+NL);
	    } else if ( onto1.isDataProperty( ob1 ) && onto2.isDataProperty( ob2 ) ) {
		indentedOutput("<ruleml:_body>"+NL);
		increaseIndent();
		indentedOutput("<swrl:datavaluedPropertyAtom swrlx:property=\""+uri1+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("<swrl:datavaluedPropertyAtom>"+NL);
		indentedOutput("<swrl:datavaluedPropertyAtom swrlx:property=\""+uri2+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:datavaluedPropertyAtom>"+NL);
		decreaseIndent();
		indentedOutput("</ruleml:_body>"+NL);
		indentedOutput("<ruleml:_head>"+NL);
		indentedOutput("</ruleml:_head>"+NL);
	    } else if ( onto1.isObjectProperty( ob1 ) && onto2.isObjectProperty( ob2 ) ) {
		indentedOutput("<ruleml:_body>"+NL);
		increaseIndent();
		indentedOutput("<swrl:individualPropertyAtom swrlx:property=\""+uri1+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:individualPropertyAtom>"+NL);
		indentedOutput("<swrl:individualPropertyAtom swrlx:property=\""+uri2+"\"/>"+NL);
		increaseIndent();
		indentedOutput("<ruleml:var>x</ruleml:var>"+NL);
		indentedOutput("<ruleml:var>y</ruleml:var>"+NL);
		decreaseIndent();
		indentedOutput("</swrl:individualPropertyAtom>"+NL);
		decreaseIndent();
		indentedOutput("</ruleml:_body>"+NL);
		indentedOutput("<ruleml:_head>"+NL);
		indentedOutput("</ruleml:_head>"+NL);
	    } else {
		logger.warn( "Cannot generate heterogeneous rules" );
	    }
	    decreaseIndent();
	    indentedOutput("</ruleml:imp>"+NL);
	    outputln();
	} catch ( OntowrapException owex ) {
	    throw new AlignmentException( "Error accessing ontology", owex );
	}
    }

    public void visit( Relation rel ) throws AlignmentException {
	if ( subsumedInvocableMethod( this, rel, Relation.class ) ) return;
	// default behaviour
	throw new AlignmentException( "Cannot render relation "+rel );
    }

}

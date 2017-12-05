/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jena.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 *
 * @author diangazo
 */
@ManagedBean
@ViewScoped
public class WebOntology implements Serializable {

    private String selected;
    private List<String> result;

    private List<String> retorno;
    List<String> listageral;

    public List<String> getNameOntologyWeb() {
        HashMap<String, String> ontologies = new HashMap<>();

        ontologies.put("Geonames", "Geonames");
        ontologies.put("DBpedia", "DBpedia");
        ontologies.put("Schema", "Schema");

        //conversao de hashmap para arraylist
        List<String> nameOntologyWeb = new ArrayList<>();
        for (String s : ontologies.values()) {
            nameOntologyWeb.add(s);
        }
        return nameOntologyWeb;
    }

    public List<String> getOnt1() {
        HashMap<String, String> ontologies = new HashMap<>();

        ontologies.put("Teste", "Teste");
        ontologies.put("Teste1", "Teste1");
        ontologies.put("Teste1", "Teste1");

        //conversao de hashmap para arraylist
        List<String> nameOntologyWeb = new ArrayList<>();
        for (String s : ontologies.values()) {
            nameOntologyWeb.add(s);
        }
        return nameOntologyWeb;
    }

    public List<String> getOnt2() {
        HashMap<String, String> ontologies = new HashMap<>();

        ontologies.put("Teste2", "Teste2");
        ontologies.put("Teste12", "Teste12");
        ontologies.put("Teste12", "Teste12");

        //conversao de hashmap para arraylist
        List<String> nameOntologyWeb = new ArrayList<>();
        for (String s : ontologies.values()) {
            nameOntologyWeb.add(s);
        }
        return nameOntologyWeb;
    }
//consulta

    public List<String> getConsultaClaProp() {
        String query1
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "SELECT ?subject ?predicate ?object\n"
                + "WHERE {\n"
                + "  ?subject a owl:Class.\n"
                + "}";
        QueryExecution queryExecution1 = QueryExecutionFactory.sparqlService("http://localhost:3030/GeoNa/query", query1);
        ResultSet results1 = queryExecution1.execSelect();

        List<String> listaClass = new ArrayList<>();

        while (results1.hasNext()) {

            QuerySolution n = results1.next();

            listaClass.add((String.valueOf(n.getResource("subject"))).split("#")[1]);

        }
        return listaClass;
    }

    public List<String> getListaPropr() {
        String query2
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "SELECT ?subject ?predicate ?object\n"
                + "WHERE {\n"
                + "  ?predicate a owl:DatatypeProperty.\n"
                + "}";
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://localhost:3030/GeoNa/query", query2);
        ResultSet results = queryExecution.execSelect();

        List<String> listaPro = new ArrayList<>();
        while (results.hasNext()) {

            QuerySolution n = results.next();

            listaPro.add((String.valueOf(n.getResource("predicate"))).split("#")[1]);
        }

        return listaPro;
    }

    public void listener(AjaxBehaviorEvent event) {
        System.out.println("listener");
//        result = "called by " + event.getComponent().getClass().getName();
        if (selected.equals("Geonames")) {

            result = getConsultaClaProp();
            retorno = getListaPropr();
        } else {
            if (selected.equals("Schema")) {
                result = getOnt1();
                retorno = getOnt2();
            } else {
                if (selected.equals("DBpedia")) {
                    result = getOnt2();
                    retorno = getOnt1();
                } else {
                    result = null;
                    retorno = null;
                }
            }
        }
    }

    //retorno do valor setado
    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public List<String> getResult2() {
        return retorno;
    }

    public List<String> getResult() {
        return result;
    }
}

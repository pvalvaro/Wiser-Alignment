/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jena.query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author diangazo
 */
@ManagedBean
public class beanteste {

    List<cont> listas;
    List<cont> listas2;

    public List<cont> getCont() throws FileNotFoundException {
        InputStream in = new FileInputStream("/home/diangazo/NetBeansProjects/testes/ResultadoAlinhamento1.rdf");
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null);
        String QueryIn = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "SELECT ?subject ?predicate ?object\n"
                + "WHERE {\n"
                + "  ?subject ?predicate ?object\n"
                + "  FILTER ((?predicate = <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#entity1>) || \n"
                + "(?predicate = <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#entity2>) ||\n"
                + "(?predicate = <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#measure>))\n"
                + "}\n"
                + "ORDER BY ASC (?subject)";
        Query query = QueryFactory.create(QueryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        int a = 0;
        List<String> lista = new ArrayList<>();
        List<String> lista2 = new ArrayList<>();
        listas = new ArrayList<>();
        while (results.hasNext()) {
            cont r = new cont();
            cont rr = new cont();
            QuerySolution m = results.nextSolution();
            lista.add(String.valueOf(m.getResource("predicate")));
            if ((lista.get(a).equals("http://knowledgeweb.semanticweb.org/heterogeneity/alignment#entity1"))
                    || (lista.get(a).equals("http://knowledgeweb.semanticweb.org/heterogeneity/alignment#entity2"))) {
                r.setEntity1(String.valueOf(m.getResource("object")));

            } else {
                if ((lista.get(a).equals("http://knowledgeweb.semanticweb.org/heterogeneity/alignment#measure"))) {
                    lista2.add((String.valueOf(m.getLiteral("object"))));

                }
                for (int c = 0; c < lista2.size(); c++) {
                    //  System.out.println(lista4.get(c));
                    for (String retval : lista2.get(c).split(" |#|@|_|\\\\|\\/|\\^|http://www.w3.org/2001/XMLSchema#float|\\*")) {
                        r.setMeasure(retval);
                    }
                }
            }

            listas.add(r);
            a += 1;
        }
        qe.close();
        return listas;

    }

}
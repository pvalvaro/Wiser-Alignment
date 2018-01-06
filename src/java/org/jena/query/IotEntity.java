/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jena.query;

/**
 *
 * @author diangazo
 */
import java.io.File;
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
import org.apache.jena.rdf.model.Resource;

// ontologia
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyRenameException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

//Classes importadas
import alignment.AlignmentResult;

//outros metodos
@ManagedBean
@ViewScoped
public class IotEntity implements Serializable {

    private String selected;
    private List<String> result;

    private List<String> retorno;

    List<String> selectedValue;
    List<String> selectedValue_pro;

    //AlignmentResult OntologySelected = new AlignmentResult();
    public List<String> getNameOntologyIoT() {
        HashMap<String, String> IoTontology = new HashMap<>();

        IoTontology.put("Fiesta-IoT", "Fiesta-IoT");
        IoTontology.put("M3-lite", "M3-lite");
        IoTontology.put("SSN", "SSN");

        //conversao de hashmap para arraylist
        List<String> nameOntologyWeb = new ArrayList<>();
        for (String s : IoTontology.values()) {
            nameOntologyWeb.add(s);
        }
        return nameOntologyWeb;
    }

    public List<String> getOnt4() {
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

    public List<String> getOnt5() {
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

    public List<String> getVaria() {
        String Query_Class
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "SELECT ?class \n"
                + "WHERE {\n"
                + "  ?class a owl:Class.\n"
                + "}";
        QueryExecution queryExecutionClass = QueryExecutionFactory.sparqlService("http://localhost:3030/Fiesta-IoT/query", Query_Class);
        ResultSet Result_Class = queryExecutionClass.execSelect();

        List<String> ListaClasse = new ArrayList();

        while (Result_Class.hasNext()) {

            QuerySolution m = Result_Class.next();

            ListaClasse.add((String.valueOf(m.getResource("class"))).split("#")[1]);
        }
        return ListaClasse;
    }

    public List<String> getListProp() {

        String Query_prop
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "SELECT DISTINCT ?obj \n"
                + "WHERE {\n"
                + "  ?obj a owl:ObjectProperty.\n"
                + "}";
        QueryExecution queryExecutionProp = QueryExecutionFactory.sparqlService("http://localhost:3030/Fiesta-IoT/query", Query_prop);
        ResultSet Result_Prop = queryExecutionProp.execSelect();

        List<String> propList = new ArrayList();

        while (Result_Prop.hasNext()) {

            QuerySolution m = Result_Prop.next();

            propList.add((String.valueOf(m.getResource("obj"))).split("#")[1]);
        }
        return propList;
    }

    public void listenerIoT(AjaxBehaviorEvent event) {
        System.out.println("listener");

//        result = "called by " + event.getComponent().getClass().getName();
        if (selected.equals("Fiesta-IoT")) {

            retorno = getVaria();
            result = getListProp();
        } else {
            if (selected.equals("SSN")) {
                result = getOnt4();
                retorno = getOnt5();
            } else {
                if (selected.equals("M3-lite")) {
                    result = getOnt5();
                    retorno = getOnt4();
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

    public List<String> getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(List<String> selectedValue) {
        this.selectedValue = selectedValue;
    }

    //onde estao os valores setados no selectmenymenu 1
    public List<String> getSelectedValueString() {
        return selectedValue;
    }

    public String checkSelectedValue() {
        return "success";
    }

    //propriedades
    public List<String> getSelectedValue_pro() {
        return selectedValue_pro;
    }

    public void setSelectedValue_pro(List<String> selectedValue_pro) {
        this.selectedValue_pro = selectedValue_pro;
    }

    public List<String> getSelectedValueString2() {
        return selectedValue_pro;
    }

    public void save() {

        try {

            //Consulta que retorna o URI da ontologia Fiesta-IoT
            String Query_URI_Ontology
                    = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                    + "prefix owl: <http://www.w3.org/2002/07/owl#>\n"
                    + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                    + "\n"
                    + "SELECT DISTINCT ?uri \n"
                    + "WHERE {\n"
                    + "  ?uri a owl:Ontology.\n"
                    + "}";
            QueryExecution queryExecutionUri = QueryExecutionFactory.sparqlService("http://localhost:3030/Fiesta-IoT/query", Query_URI_Ontology);
            ResultSet Result_URI_Ontology = queryExecutionUri.execSelect();

            String URIOnto = "";
            while (Result_URI_Ontology.hasNext()) {

                QuerySolution n = Result_URI_Ontology.next();

                Resource temp = n.getResource("uri");
                URIOnto = temp.getURI();
            }

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            //
            IRI ontologyIRI = IRI.create(URIOnto);
            //
            OWLOntology ont = manager.createOntology(ontologyIRI);

            OWLDataFactory factory = manager.getOWLDataFactory();

            for (int i = 0; i < selectedValue.size(); i++) {
                //Inserting class
                OWLClass tes = factory.getOWLClass(IRI.create(ontologyIRI + selectedValue.get(i)));

                OWLAxiom decla = factory.getOWLDeclarationAxiom(tes);

                AddAxiom addAxiom1 = new AddAxiom(ont, decla);

                manager.applyChange(addAxiom1);
                //Inserting First Property
                for (int a = 0; a < selectedValue_pro.size(); a++) {
                    OWLObjectProperty prop = factory.getOWLObjectProperty(IRI.create(ontologyIRI + selectedValue_pro.get(a)));

                    OWLAxiom propriedades1 = factory.getOWLDeclarationAxiom(prop);

                    AddAxiom addAxiom2 = new AddAxiom(ont, propriedades1);

                    manager.applyChange(addAxiom2);

                    File file = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/MyOnto20.owl");
                    OWLOntologyFormat format = manager.getOntologyFormat(ont);
                    OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
                    if (format.isPrefixOWLOntologyFormat()) {
                        owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
                    }
                    // Save the ontology to the location where we loaded it from, in the default ontology format
                    manager.saveOntology(ont, owlxmlFormat, IRI.create(file.toURI()));
                }
            }
            //   System.out.println("Vocabulario criado");

        } catch (OWLOntologyCreationException | OWLOntologyRenameException | OWLOntologyStorageException
                | UnknownOWLOntologyException e) {
        }
        reset();
    }

    public void reset() {
        selected = null;
        selectedValue = null;
        selectedValue_pro = null;
        result = null;
        retorno = null;
    }

}

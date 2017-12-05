package org.jena.query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
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

/**
 *
 * @author diangazo
 */
@ManagedBean
public class Pessoa {

    List<String> ListaClasse;
    List<String> Listapro;
    List<String> selectedValue;
    List<String> selectedValue_pro;

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

        ListaClasse = new ArrayList();

        while (Result_Class.hasNext()) {

            QuerySolution m = Result_Class.next();

            ListaClasse.add((String.valueOf(m.getResource("class"))).split("#")[1]);
        }

        return ListaClasse;

    }

    //propriedades
    public List<String> getListaPro() {
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

        Listapro = new ArrayList();

        while (Result_Prop.hasNext()) {

            QuerySolution m = Result_Prop.next();

            Listapro.add((String.valueOf(m.getResource("obj"))).split("#")[1]);
        }
        return Listapro;
    }

    //classes
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
        checkSelectedValue();
        getSelectedValueString();
        getSelectedValueString2();

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

                    File file = new File("/home/diangazo/NetBeansProjects/ZT/MyOnto4161.owl");
                    OWLOntologyFormat format = manager.getOntologyFormat(ont);
                    OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
                    if (format.isPrefixOWLOntologyFormat()) {
                        owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
                    }
                    // Save the ontology to the location where we loaded it from, in the default ontology format
                    manager.saveOntology(ont, owlxmlFormat, IRI.create(file.toURI()));
                }
            }
            System.out.println("Vocabulario criado");

        } catch (OWLOntologyCreationException | OWLOntologyRenameException | OWLOntologyStorageException
                | UnknownOWLOntologyException e) {
        }
        reset("");
    }

    public void reset(String attribute) {
        selectedValue = null;
        selectedValue_pro = null;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext()
                .getSession(false);
        session.removeAttribute(attribute);
    }

}

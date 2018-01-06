/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author diangazo
 */
@ManagedBean
@ViewScoped
public class Ontology implements Serializable {

    private String selectedIotOntology;
    private String selectedWebOntology;

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

    public void save() {
        //        result = "called by " + event.getComponent().getClass().getName();
        if (selectedIotOntology.equals("Fiesta-IoT")) {
            System.out.println("fiesta");
        } else {
            if (selectedIotOntology.equals("SSN")) {
                System.out.println("ssn");
            } else {
                if (selectedIotOntology.equals("M3-lite")) {
                    System.out.println("M3-Lite");
                } else {
                    System.out.println("nenhum");
                }
            }
        }

        if (selectedWebOntology.equals("Geonames")) {
            System.out.println("Geonames");
        } else {
            if (selectedWebOntology.equals("DBpedia")) {
                System.out.println("DBpedia");
            } else {
                if (selectedWebOntology.equals("Schema")) {
                    System.out.println("Schema");
                } else {
                    System.out.println("nenhum2");
                }
            }
        }

    }

    //retorno do valor setado
    public String getSelectedIotOntology() {
        return selectedIotOntology;
    }

    public void setSelectedIotOntology(String selectedIotOntology) {
        this.selectedIotOntology = selectedIotOntology;
    }

    public String getSelectedWebOntology() {
        return selectedWebOntology;
    }

    public void setSelectedWebOntology(String selectedWebOntology) {
        this.selectedWebOntology = selectedWebOntology;
    }
}

package alignment;

/*
 * $Id: MyAlign.java 851 2008-09-27 08:58:49Z euzenat $
 *
 * Copyright (C)  INRIA 2003-2005, 2008
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
// Align API
import fr.inrialpes.exmo.align.impl.BasicAlignment;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentProcess;
import org.semanticweb.owl.align.AlignmentVisitor;
import org.semanticweb.owl.align.AlignmentException;

// Align API Implementation
import fr.inrialpes.exmo.align.impl.method.StringDistAlignment;
import fr.inrialpes.exmo.align.impl.renderer.RDFRendererVisitor;

// Java classes
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * Compile with: javac -cp .:../../lib/procalign.jar MyAlign.java
 *
 * Run with: java -cp .:../../lib/procalign.jar MyAlign
 */
@ManagedBean
public class MyAlign {

    private String selectedIotOntology;
    private String selectedWebOntology;

    List<EntityResultAlignment> listas;

    URI file3 = null;
    URI file2 = null;

//    Ontologies selected [IoT and Web]
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
//  Alignment Ontologies

    public void alinhar() throws URISyntaxException {

        Properties params = new Properties();

        if (selectedIotOntology.equals("Fiesta-IoT")) {
            System.out.println("fiesta");

            file3 = new URI("file:/home/diangazo/NetBeansProjects/Wiser-Alignment/MyOnto20.owl");

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

            file2 = new URI("file:/home/diangazo/NetBeansProjects/Wiser-Alignment/wgs84.owl");

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

        try {
            //My ontologies
            URI uri1 = file3;
            URI uri2 = file2;

            //Inrialpes Ontology
//            URI uri1 = new URI("file:myOnto.owl");
//            URI uri2 = new URI("file:edu.mit.visus.bibtex.owl");
            AlignmentProcess A1 = new StringDistAlignment();
            params.setProperty("stringFunction", "smoaDistance");
            A1.init(uri1, uri2);
            A1.align((Alignment) null, params);

            AlignmentProcess A2 = new StringDistAlignment();
            A2.init(uri1, uri2);

            params = new Properties();

            params.setProperty("stringFunction", "ngramDistance");
            A2.align((Alignment) null, params);

            A1.cut(0.5);
            A2.cut(0.7);

            BasicAlignment A1A2 = (BasicAlignment) (A1.clone());
            A1A2.ingest(A2);

            File file1 = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento2.rdf");
            file1.delete();
            // Save alignment to file
            Alignment result = (BasicAlignment) ((BasicAlignment) A1).clone();
            File file = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento2.rdf");
            FileOutputStream fop = new FileOutputStream(file);
            //Display it as RDF
            try (PrintWriter writer = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(fop, "UTF-8")),
                    true)) {
                //Display it as RDF
                AlignmentVisitor renderer = new RDFRendererVisitor(writer);
                result.render(renderer);
                writer.flush();
                writer.close();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MyAlign.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (AlignmentException e) {
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyAlign.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public void linhamento() throws FileNotFoundException, IOException {
//        //Create file object
//        File file = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento1.rdf");
//
//        //Create FileInputStream object
//        FileInputStream fileInputStream = new FileInputStream(file);
//
//        //Read first byte
//        int i = fileInputStream.read();
//        while (i != -1) {
//            //Converting byte to char and printing it
//            System.out.print((char) i);
//            i = fileInputStream.read();
//        }
//
//        //Close FileInputStream
//        fileInputStream.close();
//
//    }
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

//    Alignment Result
    public void ResultPrint(List<String> item) throws FileNotFoundException {
        InputStream in = new FileInputStream("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento2.rdf");
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
            EntityResultAlignment r = new EntityResultAlignment();

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
                    for (String measure : lista2.get(c).split(" |#|@|_|\\\\|\\/|\\^|http://www.w3.org/2001/XMLSchema#float|\\*")) {
                        r.setMeasure(measure);
                    }
                }
            }

            listas.add(r);
            a += 1;
        }
        qe.close();
//        return listas;

    }

    public List getListas() {
        return listas;
    }

    public void setListas(List<EntityResultAlignment> listas) {
        this.listas = listas;
    }
}

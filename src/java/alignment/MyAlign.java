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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;

/**
 * Compile with: javac -cp .:../../lib/procalign.jar MyAlign.java
 *
 * Run with: java -cp .:../../lib/procalign.jar MyAlign
 */
@ManagedBean
public class MyAlign {

    public void alinhar() {

        Properties params = new Properties();

        try {
            //My ontologies
            URI uri1 = new URI("file:/home/diangazo/NetBeansProjects/Wiser-Alignment/MyOnto20.owl");
            URI uri2 = new URI("file:/home/diangazo/NetBeansProjects/Wiser-Alignment/wgs84.owl");

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

            // Access alignment Cells
//            Iterator<Cell> iterator = A1.iterator();
//            while (iterator.hasNext()) {
//                Cell cell = iterator.next();
//                String cellStr = cell.toString();
//                String semantics = cell.getSemantics();
//                String object1 = cell.getObject1().toString();
//                String object2 = cell.getObject2().toString();
//                String relation = cell.getRelation().toString();
//                String strength = "" + cell.getStrength();
//            }
            File file1 = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento1.rdf");
            file1.delete();
            // Save alignment to file
            Alignment result = (BasicAlignment) ((BasicAlignment) A1).clone();
            File file = new File("/home/diangazo/NetBeansProjects/Wiser-Alignment/ResultadoAlinhamento1.rdf");
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
        } catch (URISyntaxException ex) {
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
}
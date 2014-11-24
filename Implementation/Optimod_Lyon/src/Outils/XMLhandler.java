/**
 * 
 */
package Outils;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

/**
 * @author Slifer
 *
 */
public class XMLhandler {
	
	public File selectXML()
	{
    	JFileChooser jFileChooserXML = new JFileChooser();
    	FileFilterApp filter = new FileFilterApp();
    	File fichierData = null;
    	
        filter.addExtension("xml");
        filter.setDescription("Fichier XML");
        jFileChooserXML.setFileFilter(filter);
        jFileChooserXML.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal;
        	returnVal = jFileChooserXML.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) 
               fichierData = new File(jFileChooserXML.getSelectedFile().getAbsolutePath());
        return fichierData;
	}
	
		
		public boolean checkXML (String input, String modeleXSD) throws SAXException, IOException {
	        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	        File schemaLocation = new File(modeleXSD);
	        Schema schema = factory.newSchema(schemaLocation);
	        Validator validator = schema.newValidator();
	        Source source = new StreamSource(input);
	        try {
	            validator.validate(source);
	            return true;
	        }
	        catch (SAXException ex) {
	             System.out.println(Proprietes.ERREUR_XML);
	            return false;
	        } 
		}

}

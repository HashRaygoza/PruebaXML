/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.com.hash.pruebaxml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 *
 * @author david
 */
public class PruebaXML {
    static final private Logger LOGGER = Logger.getLogger("mx.com.hash.pruebaxml.PruebaXML");
    
    public static void main(String[] args) {
        try {
            EjemploXML ejemploXML = new EjemploXML();
            Document documento = ejemploXML.crearDocumento();
            
            System.out.println(ejemploXML.convertirString(documento));
            
            ejemploXML.escribirArchivo(documento, "ejemplo.xml");            
            
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, "Error de configuracion");
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            LOGGER.log(Level.SEVERE, "Error de transformacion XML a String");
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
    }
}

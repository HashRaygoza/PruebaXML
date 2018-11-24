# Crear archivo XML desde Java

Con la popularidad de servicios SOAP y el almacenamiento de archivos en XML, es 
buena idea saber el como crearlos, mas aun con la impresionante cantidad de 
librerías con esa capacidad, cada una con sus pros, contras y enfoques, sin 
embargo no hay que olvidar que el propio Java contiene las librerías 
precisamente para crear documentos XML, llamadas Java API for XML Processing, 
JAXP que es bastante sencilla de usar, por lo que vale la pena darle un vistazo.

## Base teórica de JAXP.

La forma en que opera la librería JAXP para crear un documento XML es la
siguiente:

1. Creamos una nueva instancia DocumentBuilderFactory
2. Creamos un objeto DocumentBuilder
3. Creamos el objeto Document, este representa nuestro XML
4. Creamos objetos Element, los que representan las diferentes secciones del XML
5. Establecemos el contenido de dicho objecto Element
6. Indicamos de que elemento desciende el que acabamos de crear

Los pasos 4 y 6 se repetirían para cada elemento que deseamos agregar al XML, el
 ultimo paso puede sonar algo confuso, pero si conoce la estructura del XML 
sabra que las etiquetas usualmente van anidadas en forma jerárquica el paso 6 es
 cuando indicamos de que elemento desciende el elemento que acabamos de crear.

## Conversión de Document a texto.

Aunque seria tentador pensar que el objeto Document contiene un sencillo método 
.toString que nos regresará el contenido del archivo en texto plano, esto 
lamentablemente no es así, la labor de convertir nuestro documento XML a un 
archivo o cadena de texto recae en el la clase javax.xml.trasnform.Transformer y
su método transform.

El procedimiento para pasar de un Documento a un archivo que contenga el XML es 
el siguiente:

1. Creamos un objeto Transformer, el cual efectuará la conversión con su método transform
2. Indicamos al objeto Transformer si deseamos o no que el archivo tenga indentación, esto se logra llamando al método .setOutputProperty dos veces la primera con los argumentos OutputKeys.INDENT y “yes” y la segunda con los argumentos “{http://xml.apache.org/xslt}indent-amount" y “2”
3. Creamos un objeto File el cual representa el archivo donde deseamos almacenar el documento
4. Creamos un objeto DOMSource pasándole como argumento el objeto Document, este objeto representa la fuente que usara la clase Transformer para efectuar la transformación.
5. Creamos un objeto StreamResult pasándole como argumento el objeto File que creamos previamente, este objeto es donde se almacenara el resultado del método Transformer.transform , al pasarle como argumento el File indicamos que deseamos almacenar el resultado en ese archivo este objeto se encargara de todo lo necesario para eso
6. Llamamos al método transform del objecto Transformer que creamos previamente, pasándole como argumento el DOMSource que creamos previamente y el StreamResult, esto tomara los datos del DOMSource y pasara al formato que indique el StreamResult

Ya es un tema recurrente mencionar que esto suena demasiado a sobre ingeniera 
pero de nuevo la idea es dar la mayor flexibilidad posible y mantener una muy 
clara división de los pasos.

## Descripción de las clases.

*org.w3c.dom.Document* : Esta clase representa el documento y el nodo <XML> del documento, el nodo principal de la jerarquia.

*org.w3c.dom.Element* : Cada objeto de esta clase representa un nodo del documento XML, y el contenido de este nodo, a cada elemento se le debe establecer su elemento “padre”, el cual puede ser otro objeto Element o el propio documento XML.

*javax.xml.transform.Transformer* : Esta clase transforma un documento XML en la salida que deseemos, ya que esta es una clase abstracta no podemos instanciarla con un constructor, por lo que necesita llamar al método newInstance de un objecto TransformerFactory. El método interesante aquí es el transform, el cual efectúa la transformación, este método le pide una fuente y una salida donde se almacenara el resultado de la transformación.

*java.io.StringWriter* : Convierte un flujo de salida en una cadena de texto, se usa para convertir salidas en cadenas de texto

*javax.xml.transform.dom.DOMSource* : Este objeto actua para contener el DOM de un documento, se usa como fuente para el método transform

*javax.xml.transform.stream.StreamResult* : Contiene el resultado de la transformación del documento DOM ,se le pasa como argumento donde se almacenara la salida ya sea un StringWriter para almacenar en cadena de texto o un File para almacenar en ese archivo.

## Almacenando en un archivo.

Una vez que que tenemos los datos contenidos en nuestro objecto Document es 
necesitaremos almacenarlo, puede ser en un archivo completo o como una cadena de
texto en una base de datos, ambas opciones son posibles con las clases 
Transformer, StringWriter y StreamResult los cuales nos permitirían convertir el
objecto Document a la salida que deseemos.

## Ejemplo.

Todo esto suena complicado, por lo que se presentara un ejemplo de como hacerlo 
a continuación, el ejemplo consiste en 2 clases una con los métodos que crean el
documento, y otra con el método main que llama a esos métodos:

EjemploXML.java

```java
package mx.com.hash.pruebaxml;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author david
 */
public class EjemploXML {
    
    public Document inicializarDocumento() throws ParserConfigurationException{
        Document documento;
        // Creamos los objectos de creacion de Documentos XML
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = docFactory.newDocumentBuilder();
        
        documento = constructor.newDocument();
        
        return documento;        
    }
    
    public String convertirString(Document documento) throws TransformerConfigurationException, TransformerException {
        // Creamos el objecto transformador
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        
        // Indicamos que queremos que idente el XML con 2 espacios
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        // Creamos el escritor a cadena de texto
        StringWriter writer = new StringWriter();
        // Fuente de datos, en este caso el documento XML
        DOMSource source = new DOMSource(documento);
        // Resultado, el cual se almacenara en el objecto writer
        StreamResult result = new StreamResult(writer);
        // Efectuamos la transformacion a lo que indica el objecto resultado, writer apuntara a el resultado
        transformer.transform(source, result);
        // Convertimos el buffer de writer en cadena de texto
        String output = writer.getBuffer().toString();

        return output;
    }
    
    public void escribirArchivo(Document documento, String fileName) throws TransformerConfigurationException, TransformerException {
        // Creamos el objecto transformador
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        // Indicamos que queremos que idente el XML con 2 espacios
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        // Archivo donde almacenaremos el XML
        File archivo = new File(fileName);

        // Fuente de datos, en este caso el documento XML
        DOMSource source = new DOMSource(documento);
        // Resultado, el cual almacena en el archivo indicado
        StreamResult result = new StreamResult(archivo);
        // Transformamos de ña fuente DOM a el resultado, lo que almacena todo en el archivo
        transformer.transform(source, result);
    }
    
    public Document crearDocumento() throws ParserConfigurationException {
        Document documento = this.inicializarDocumento();
        
        // Creamos el elemento principal
        Element entrada = documento.createElement("entrada");
        // Hacemos el elemento entrada descender directo del nodo XML principal
        documento.appendChild(entrada);

        // Creamos el Elemento de titulo
        Element titulo = documento.createElement("titulo");
        // Establecemos el contenido del titulo
        titulo.setTextContent("Creacion de XML");
        // Indicamos que el elemento titulo desciende de entrada
        entrada.appendChild(titulo);

        //Creamos mas elementos
        Element autor = documento.createElement("autor");
        autor.setTextContent("hashRaygoza");
        entrada.appendChild(autor);

        //Elemento fecha
        Element fecha = documento.createElement("fecha");
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendario = Calendar.getInstance();
        Date date = new Date(calendario.getTimeInMillis());

        fecha.setTextContent(formato.format(date));
        entrada.appendChild(fecha);
        
        return documento;
    }
    
}
```

PruebaXML.java

```java
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
```

Ya que corra el programa vera el XML resultado en la pantalla y se generara un 
archivo llamado ejemplo.xml el cual contiene lo siguiente:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<entrada>
  <titulo>Creacion de XML</titulo>
  <autor>hashRaygoza</autor>
  <fecha>2018-11-24</fecha>
</entrada>
```


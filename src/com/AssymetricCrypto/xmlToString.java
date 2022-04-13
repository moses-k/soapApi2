package com.AssymetricCrypto;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class xmlToString {
	public static void main(String[] args) throws Exception {

	    String s = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:myNamespace=\"http://www.webserviceX.NET\">\r\n" + 
	    		"              <SOAP-ENV:Header/>" + 
	    		"              <Code>" + 
	    		"                  <myNamespace:GetInfoByCity>" + 
	    		"                      <myNamespace:USCity>New York</myNamespace:USCity>" + 
	    		"                  </myNamespace:GetInfoByCity>" + 
	    		"              </Code>" + 
	    		"          </SOAP-ENV:Envelope>";
	    		
	    InputStream is = new ByteArrayInputStream(s.getBytes());

	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document document = db.parse(is);

	   // Node rootElement = d.getDocumentElement();
	    NodeList nList1 = document.getElementsByTagName("Code");		 
		  //Element rootElement = (Element) document.getDocumentElement().getElementsByTagName("Code");   
		    Node node = (Node) nList1.item(0);	
	        Element encryptedElement = (Element) node;
	        String encryptedNode = nodeToString(encryptedElement);
	        
	        
	    System.out.println(encryptedNode);

	  }

	  private static String nodeToString(Node node) {
	    StringWriter sw = new StringWriter();
	    try {
	      Transformer t = TransformerFactory.newInstance().newTransformer();
	      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	      t.setOutputProperty(OutputKeys.INDENT, "yes");
	      t.transform(new DOMSource(node), new StreamResult(sw));
	    } catch (TransformerException te) {
	      System.out.println("nodeToString Transformer Exception");
	    }
	    return sw.toString();
	  }

}

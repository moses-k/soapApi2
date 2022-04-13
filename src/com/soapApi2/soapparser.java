package com.soapApi2;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class soapparser {
	private static String nbNameSpace = "nb";
	private static String nbNamespaceURI = "http://nbk-test-server.com";

	 public static void main(String args[]) throws Exception {
		  String xmlFile = "E:/SoapApi/employee.xml";
		  String encryptedFile = "E:/SoapApi/encrypted.xml";
		  String decryptedFile = "E:/SoapApi/decrypted.xml";

		  SOAPMessage soapMsg = creatSoapRequest();
		  
		  ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapMsg.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8") ;
			
			  System.out.println("soapMsg  "+ message);

			    Document document = loadXMLString(message);	
			    document.getDocumentElement().normalize();
	
			 // getFullNameFromXml(message,"Code");
			
			  List<String> output = getFullNameFromXml(message, "ValueDate");
			  String[] strarray = new String[output.size()];
			  output.toArray(strarray);
			  System.out.print("Response Array is "+Arrays.toString(strarray));
			  
		  System.out.println("Done");
		 }
	
 public static Document loadXMLString(String response) throws Exception{
	    DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(response));
	
	    return db.parse(is);
	}

	public static List<String> getFullNameFromXml(String response, String tagName) throws Exception {
		
		  System.out.println("soapMsg2  "+ response);

	    Document xmlDoc = loadXMLString(response);
	    NodeList nodeList = xmlDoc.getElementsByTagName(tagName);
	    List<String> ids = new ArrayList<String>(nodeList.getLength());
	    for(int i=0;i<nodeList.getLength(); i++) {
	        Node x = nodeList.item(i);
	        ids.add(x.getFirstChild().getNodeValue());             
	        System.out.println("node is "+ nodeList.item(i).getFirstChild().getNodeValue());
	    }
	    return ids;
	}
	
	public static SOAPMessage creatSoapRequest() { 
		
		MessageFactory messageFactory = null; SOAPMessage message = null;  SOAPPart soapPart = null;
	try {
		 messageFactory =  MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
	     message = messageFactory.createMessage();
	 // SOAP Envelope
	     soapPart = message.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		soapEnvelope.addNamespaceDeclaration(nbNameSpace, nbNamespaceURI);
		
	    SOAPBody soapBody = soapEnvelope.getBody();
		Name bodyName = soapEnvelope.createName("Code");
		SOAPBodyElement purchaseLineItems = soapBody.addBodyElement(bodyName);
		Name ValueDate = soapEnvelope.createName("ValueDate");
		SOAPElement order = purchaseLineItems.addChildElement(ValueDate);
		order.addTextNode("2020-08-31");
		Name PaymentRef = soapEnvelope.createName("PaymentRef");
		SOAPElement value = purchaseLineItems.addChildElement(PaymentRef);
		value.addTextNode("RE625367Q562342");
		Name PayType = soapEnvelope.createName("PayType");
		SOAPElement order2 = purchaseLineItems.addChildElement(PayType);
		order2.addTextNode("Debit");
		
	}catch(Exception e) {
	    e.printStackTrace();

	}
		return message;
		
	}
	

}

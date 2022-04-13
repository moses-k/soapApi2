package com.soapApi2;

import javax.xml.soap.MessageFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.EncryptionMethod;

import com.sun.org.apache.xpath.internal.XPathAPI;

import java.security.KeyPair; 
import java.security.Key;
import java.security.KeyPairGenerator;
import java.util.*;
import javax.crypto.*;
import javax.xml.crypto.dom.*;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.*;


import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import javax.xml.transform.dom.*;
import javax.xml.transform.*;
import java.io.*;
import javax.xml.transform.stream.*; 

import javax.xml.crypto.enc.*;
import javax.xml.crypto.enc.dom.*;
import javax.xml.crypto.enc.keyinfo.*;

import java.security.xml.enc


public class soaptest {
	private static String nbNameSpace = "nb";
	private static String nbNamespaceURI = "http://nbk-test-server.com";
	 private XMLEncryptionFactory fac;
	   
	 private KeyInfoFactory kfac;
	    private DocumentBuilderFactory dbf;
	    private String xmlfile;
	    private KeyPair kp;  //the key pair used to encrypt and decrypt the secret key
	    private SecretKey key; //the secret key used to encryt and decrypt the content

	
	/*
	 <SOAP-ENV:Envelope
		  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nb="http://nbk-test-server.com">
		  <SOAP-ENV:Header/>
		  <SOAP-ENV:Body>
			  <Code>
				  <ValueDate>2020-08-31</ValueDate>
				  <PaymentRef>RE625367Q562342</PaymentRef>
				  <PayType>Debit</PayType>
			  </Code>
		  </SOAP-ENV:Body>
	  </SOAP-ENV:Envelope>
	  
	   <SOAP-ENV:Envelope
		  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nb="http://nbk-test-server.com">
		  <SOAP-ENV:Header/>
		  <SOAP-ENV:Body>
			  <Code>kcvjhsdjfgsdf7w43tr23478cnfcegtfgvffdgbdvfsdgcfd</Code>
		  </SOAP-ENV:Body>
	  </SOAP-ENV:Envelope>

	 */
	    //constructor, initialize instance variables
	    public soaptest() throws Exception {
	        //java.security.Security.addProvider(new org.jcp.xml.enc.internal.dom.XMLEncRI());
	        fac = XMLEncryptionFactory.getInstance("DOM", "IBMXMLEnc");
	        xmlfile = "encrypted.xml";
	        dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        //generate the RSA key pair
	        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	        kpg.initialize(512);   
	        System.out.println("Generating the RSA key pair...");
	        kp = kpg.generateKeyPair(); 
	    }
	    
	public static void main(String arg[]) {
		try {
			SOAPMessage soapMsg = creatSoapRequest();
			System.out.println("Request SOAP Message:");
			soapMsg.writeTo(System.out);
			System.out.println("\n");
	
		}catch(Exception e) {
			
		}	
		
		try {
			soaptest sample = new soaptest();
	        //builds the XML document
	        Element envelope = sample.createXMLDocument();
	        //encrypt the Content in this document
	        sample.encrypt(envelope);
	        //decrypt the Content
	        sample.decrypt();
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	}
	
Element createXMLDocument() throws Exception {
         dbf.setNamespaceAware(true);
 DocumentBuilder db = dbf.newDocumentBuilder();
 Document doc = db.newDocument();
 Element envelope = doc.createElementNS("http://example.org/envelope", "Envelope");
 envelope.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://example.org/envelope");
 doc.appendChild(envelope);
 Element content = doc.createElementNS("http://exampl.org/content", "Content");
 content.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://example.org/content");
 content.setTextContent("This is XML encryption sample");
 envelope.appendChild(content);
 return envelope;
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
	   
        Node nodeToBeEncrypted =  document.getElementsByTagName("Code");

		// Encrypt the content of the 2nd / element
		nodeToBeEncrypted = XPathAPI.selectSingleNode(doc,
		    "//Customer[2]/CreditCard/Number");
		// Encrypt the nodes (whole elements are encrypted)
		Element elemEncryptedData = xmlencEncryptor.encrypt( (Element)nodeToBeEncrypted, false, getEncryptedDataTemplate(desKey, false), desKey  ;
		
		
		
	}catch(Exception e) {
        e.printStackTrace();

	}
		return message;
		
	}
	
private void encrypt(Element envelope) throws Exception {
    encryptKey(envelope);
    encryptContent(envelope);
}
//encrypt the content with secret key
private void encryptContent(Element envelope) throws Exception {
    NodeList nlist = envelope.getElementsByTagName("Content");
    Element elem = (Element)nlist.item(0);
    ToBeEncrypted tbeElement = new DOMToBeEncryptedXML(elem, null);
    KeyName kn = kfac.newKeyName("Alice");
    ArrayList kiTypes = new ArrayList();
    kiTypes.add(kn);
    KeyInfo ki = kfac.newKeyInfo(kiTypes); //keyinfo includes retrievalmethod and keyname
    EncryptionMethod em = fac.newEncryptionMethod(EncryptionMethod.AES128_CBC, 
                                                  new Integer(128),
                                                  null);
    EncryptedData ed = fac.newEncryptedData(
                            tbeElement,  //the data to be encrypted
                            em,          //encryption method is AES 
                            ki,          //key info
                            null,       //EncryptionProperties
                            null);       //id
    XMLEncryptContext xec = new DOMEncryptContext(key, envelope);
    System.out.println("Encrypting data...");
    ed.encrypt(xec);
    System.out.println("Write the XML document to file " + this.xmlfile);
    writeDoc(envelope, this.xmlfile);
}

//encrypt the secret key that is used to encrypt the content
public void encryptKey(Element envelope) throws Exception {
    EncryptedKey encryptedKey = createEncryptedKey(); 
    //create the encryption context with public key and xml element
    XMLEncryptContext ekxec = new DOMEncryptContext(kp.getPublic(), envelope);
    //encrypt the key
    System.out.println("Encrypting key...");
    encryptedKey.encrypt(ekxec);
 }
private EncryptedKey createEncryptedKey() throws Exception {
    //Create an AES key
    KeyGenerator kg = KeyGenerator.getInstance("AES");
    //initialize the key size
    kg.init(128);
    //generate the key
    System.out.println("Generating the AES key...");
    this.key = kg.generateKey();
    //wrap the key to ToBeEncryptedKey
    ToBeEncryptedKey tbeKey = new ToBeEncryptedKey(key);
    //create RSA EncryptionMethod
    
    Element elemEncryptedDataToDecrypt = (Element)
    		   DOMUtil.getElementsByTagNameNS(doc, XEncryption.XMLENC_NS,"EncryptedData").item(5);
    
    
    EncryptionMethod ekem = fac.newEncryptionMethod
                            (EncryptionMethod.RSA_1_5, null, null); 
    this.kfac = KeyInfoFactory.getInstance("DOM");
    KeyName ekn = kfac.newKeyName("Bob");
    KeyInfo eki = kfac.newKeyInfo(Collections.singletonList(ekn));
    EncryptedKey ek = fac.newEncryptedKey
                   (tbeKey,        //the secret key to be encrypted
                    ekem,          //the encryption method is RSA_1_5
                    eki,           //the key info, with only keyname in it
                    null,                   //encryption properties
                    null,     //a list of DataReference or KeyReference
                    null,            //the ID of EncryptedKey element, encrypted secret key ID is Alice
                    "Bob",              //the carried key name
                    null);              //the recipient
   return ek;
}



}

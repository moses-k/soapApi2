package com.soapApi2;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class testsoap2 {
	private static String nbNameSpace = "nb";
	private static String nbNamespaceURI = "http://nbk-test-server.com";
	
	public static void main(String arg[]) {
		try {
			SOAPMessage soapMsg = creatSoapRequest();
			//System.out.println("Request SOAP Message:");
			//soapMsg.writeTo(System.out);
			//System.out.println("\n");
		    SOAPBody soapBody = soapMsg.getSOAPBody();
		    
			Name bodyName = soapBody.getElementName();


			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			((ByteArrayOutputStream) bodyName).writeTo(stream);
			
			
			String bodyName1 = new String(stream.toByteArray(), "utf-8") ;
			
			System.out.println("String is " + bodyName);

//			SOAPBody element = 
//			DOMSource source = new DOMSource(element);
//			StringWriter stringResult = new StringWriter();
//			TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
//			String message = stringResult.toString();
//			
	//SOAPBody soapBody1 = soapMsg.getSOAPBody();
//			java.util.Iterator iterator =soapBody1.getChildElements(bodyName);
//			
//			
	///Reader in = new StringReader(message);
	//Document xmlDoc = builder.build(in);
//			NodeList nodelist = soapMsg.ge
			/*
			 <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nb="http://nbk-test-server.com">
			 <SOAP-ENV:Header/>
			 <SOAP-ENV:Body>
				 <Code>
				     <ValueDate>2020-08-31</ValueDate>
					 <PaymentRef>RE625367Q562342</PaymentRef>
					 <PayType>Debit</PayType>
				 </Code>
				 </SOAP-ENV:Body>
			 </SOAP-ENV:Envelope>
			 */
			
			String response = " "
					+ " <Body> "
                    +"    <Code id= '111'>"
                    + "      <ValueDate>2020-08-31</ValueDate>  "
                    + "      <PaymentRef>RE625367Q562342</PaymentRef>  "
                    + "      <PayType>Debit</PayType>  "
                    + "      <Amount>500</Amount> "
	                    +"    <Result id= '222'>"
	                    + "      <DOB>2020-08-31</DOB>  "
	                    + "      <Charges>20</Charges> "
	                    + "   </Result> "
                    + "   </Code>  " 
                    +"    <Code id= '112'>"
                    + "      <ValueDate>2020-08-31</ValueDate>  "
                    + "      <PaymentRef>RE625367Q5623243</PaymentRef>  "
                    + "      <PayType>Debit</PayType>  "
                    + "      <Amount>500</Amount> "
	                    +"    <Result>"
	                    + "      <DOB>2020-08-31</DOB>  "
	                    + "      <Charges>20</Charges> "
	                    + "   </Result> "
                    + "   </Code>  " 
                    + "</Body>";
			
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new InputSource(new StringReader(response.toString())));
			 document.getDocumentElement().normalize();

		    //NodeList nList = document.getDocumentElement().getElementsByTagName("Body");
	       // NodeList nList = document.getElementsByTagName("Body");
	        Element root = document.getDocumentElement();
	        System.out.println(root.getNodeName());
	        
	        
	        NodeList nList1 = document.getElementsByTagName("Code");
	        System.out.println("============================"  +nList1.getLength() );
	        
	        visitChildNodes(nList1);

		}catch(Exception e) {
			
			 e.printStackTrace();
		}	
		
	}
	
	 //This function is called recursively
	   private static void visitChildNodes(NodeList nList)
	   {
	      for (int temp = 0; temp < nList.getLength(); temp++)
	      {
	         Node node = nList.item(temp);
	         if (node.getNodeType() == Node.ELEMENT_NODE)
	         {
	            System.out.println(node.getNodeName() + ":" + node.getTextContent());
	            //Check all attributes
	            if (node.hasAttributes()) {
	               // get attributes names and values
	               NamedNodeMap nodeMap = node.getAttributes();
	               for (int i = 0; i < nodeMap.getLength(); i++)
	               {
	                   Node tempNode = nodeMap.item(i);
	                   System.out.println(tempNode.getNodeName()+": " + tempNode.getNodeValue());
	               }
	               if (node.hasChildNodes()) {
	                  //We got more childs; Let's visit them as well
	                  visitChildNodes(node.getChildNodes());
	               }
	           }
	         }
	      }
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

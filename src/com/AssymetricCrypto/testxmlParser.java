package com.AssymetricCrypto;

import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class testxmlParser {
	
	
	public static void main (String [] arg ) throws Exception {
		
		String xmlResponse =  readxml("E:\\Mos\\Integration\\NBK\\SampleMessages\\KonnectAccount_PreAuthResponseMessage.xml");
		//String xmlResponse =  readxml("E:\\Mos\\Integration\\NBK\\SampleMessages\\wrongCredentialsResponse.xml");
	     System.out.println("xmlResponse \n " +xmlResponse);

//	     SAXBuilder builder = new SAXBuilder();
//	        Reader in = new StringReader(xml);
//	        Document doc = null;
//	        Element root = null;
//	        Element error = null;
//	        Element status_message = null;
//	        String status_code = "";
//	        String message = "";
//	        try {
//	            doc = builder.build(in);
//	            root = doc.getRootElement();
//	            Element body = root.getChild("Body", Namespace.getNamespace("S", "http://schemas.xmlsoap.org/soap/envelope/"));
//	            Element processResponse = body.getChild("processResponse", Namespace.getNamespace("ns2", "http://ws.xxxxx.com/"));
//	            Element response = processResponse.getChild("response");
//	            error = response.getChild("error");
//	            status_message = response.getChild("message");
//	            status_code = error.getText();
//	            message = status_message.getText();
//
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
	     try {
	    	 DocumentBuilder builder1 = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		        InputSource src = new InputSource();
				src.setCharacterStream(new StringReader(xmlResponse));
				Document doc1 = builder1.parse(src);
				
				if(doc1.equals("")) {
					 System.out.println("doc1 is empty" );

					
				}
				
				doc1.getDocumentElement().normalize();
				NodeList groupList = doc1.getElementsByTagName("*");
				//int noNodes = groupList.getLength();
				String errorCode = null; String status = null;   String description = null; String code = null;
				String msgType = null; String reqType = null; String reqID = null; 
				System.out.println("count "+groupList.getLength());
				for (int i = 0; i < groupList.getLength(); i++) {
			        Node node = groupList.item(i);

			        if(node != null) {
			        	 if (node.getNodeType() == Node.ELEMENT_NODE) {
//					           
						      if(node.getNodeName().equals("soapenv:Fault") ) {
					        		 System.out.println("Error encountered");
				        		 String faultCode = doc1.getElementsByTagName("faultcode").item(0).getTextContent();
								String faultString = doc1.getElementsByTagName("faultstring").item(0).getTextContent();
	//							
								System.out.println("faultCode "+ faultCode + " faultString "+ faultString );
	//
								}else  if(node.getNodeName().equals("Header") ) {
									 msgType = doc1.getElementsByTagName("MsgType").item(0).getTextContent();
									 reqType = doc1.getElementsByTagName("ReqType").item(0).getTextContent();
									 reqID = doc1.getElementsByTagName("ReqID").item(0).getTextContent();
									 System.out.println("msgType "+ msgType + "reqType "+ reqType + "reqID "+ reqID );								
	//								
								}else  if(node.getNodeName().equals("Code") ) {
									 code = doc1.getElementsByTagName("Code").item(0).getTextContent();
									 System.out.println("code "+ code );
								}else   if(node.getNodeName().equals("Status") ) {
						           errorCode = doc1.getElementsByTagName("Status").item(0).getTextContent();
									 System.out.println("errorCode "+ errorCode );
	
								 }else  if(node.getNodeName().equals("ErrorCode") ) {
							           errorCode = doc1.getElementsByTagName("ErrorCode").item(0).getTextContent();
										 System.out.println("errorCode "+ errorCode );
	
								 }else  if(node.getNodeName().equals("Description") ) {
										description = doc1.getElementsByTagName("Description").item(0).getTextContent();
										 System.out.println("description "+ description );
	
	
								 }
//			        	
			           }
//			       
//			      
			        }
			    }
	    	 
	     }catch (Exception e) {
			 System.out.println(" exception "+e.getMessage() );
		}
	        
	       
			
			
			
			
			
			
			
			
	        
	        
//	        
//	        try { 
//	            // parse the document
//	        	DocumentBuilder builder2 = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
//	            Document doc = docBuilder.parse (new File("E:/Benji/docs/LastestSchemasNBK/LastestSchemas/NB_FNTX_Message.xsd")); 
//	            NodeList list = doc.getElementsByTagName("xs:element"); 
//	            NodeList list1 = doc.getElementsByTagName("xs:complexType "); 
//
//	            //loop to print data
//	            for(int i = 0 ; i < list.getLength(); i++)
//	            {
//	                Element first = (Element)list.item(i);
//	                if(first.hasAttributes())
//	                {
//	                    String nm = first.getAttribute("name"); 
//	                    System.out.println(nm); 
//	                    String nm1 = first.getAttribute("type"); 
//	                    System.out.println(nm1); 
//	                }
//	            }
//	        } 
//	        catch (ParserConfigurationException e) 
//	        {
//	            e.printStackTrace();
//	        }
//	        catch (SAXException e) 
//	        { 
//	            e.printStackTrace();
//	        }
//	        catch (IOException ed) 
//	        {
//	            ed.printStackTrace();
//	        }
	     
	     
	     
	     
	     
	     
	}
	
	public static String readxml(String path) throws Exception { 
		String EncryptedKey  = null;
		try {
			 //byte[] keyBytes = Files.readAllBytes(Paths.get(AESKey));
		     EncryptedKey = new String (Files.readAllBytes(Paths.get(path)));
		}catch (Exception e) {
			// TODO: handle exception
		}
	   
	    return  EncryptedKey ;
	}

}




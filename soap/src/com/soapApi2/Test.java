package com.soapApi2;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;

import org.apache.xml.security.encryption.XMLCipher;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/*
 * raw data
  <SOAP-ENV:Envelope 
	  xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nb="http://nbk-test-server.com">
	  <SOAP-ENV:Header/>
	  <SOAP-ENV:Body>
		  <nb:FinRequestMessage>
			  <nb:Header>
			  <MsgType>EFT</MsgType>
			  <ReqType>PA</ReqType>
			  <ReqID>1586179742165</ReqID>
			  <Resend>xxx</Resend>
			  <ChannelId>NOT</ChannelId>
			  </nb:Header>
			  <nb:Content>
				  <nb:Transaction>
				  <Code>
				  	<nb:FinTxnRequestMessage><nb:TransactionData><MsgType>2020-09-26</MsgType><PaymentRef>QRT367434G343737</PaymentRef><PayType>Debit</PayType><nb:Debit><AcctID>01923232737111</AcctID><AccountName>Moses Kipyegon</AccountName><BankSortCode>0325</BankSortCode><nb:Amount><amount>5,500</amount><isocurrcode>403</isocurrcode><XRate>0.26</XRate></nb:Amount><nb:Narrative><NarrativeLine1>School fees</NarrativeLine1><NarrativeLine1>School fees</NarrativeLine1></nb:Narrative><nb:ChargeDetail><ChargeType>OUR</ChargeType><ChargeAcctID>01109273462726700</ChargeAcctID><nb:ChargeAmount><amount>20</amount><isocurrcode>403</isocurrcode></nb:ChargeAmount></nb:ChargeDetail><BICcode>345644</BICcode><nb:BenAddress><Name>John Smith</Name><AddressLine1>P.O BOX 254543-433</AddressLine1><CountryCode>254</CountryCode></nb:BenAddress></nb:Debit><nb:MobileDeviceInfo><MobileNumber>254</MobileNumber><NumberPlan>MSISDN</NumberPlan><SubscriberIdentity>0733489214</SubscriberIdentity><SubscriberDirectoryNum>254733489214</SubscriberDirectoryNum></nb:MobileDeviceInfo></nb:TransactionData></nb:FinTxnRequestMessage>
				  </Code>
				  </nb:Transaction>
			  </nb:Content>
		  </nb:FinRequestMessage>
	  </SOAP-ENV:Body>
  </SOAP-ENV:Envelope>

 */


public class Test {
	private static String nbNameSpace = "nb";
	private static String nbNamespaceURI = "http://nbk-test-server.com";
	 public static void main(String args[]) throws Exception {
		  String xmlFile = "E:/SoapApi/employee.xml";
		  String encryptedFile = "E:/SoapApi/encrypted.xml";
		  String decryptedFile = "E:/SoapApi/decrypted.xml";

		  SecretKey secretKey = SecretKeyUtil.getSecretKey("AES");
		  SOAPMessage soapMsg = creatSoapRequest();
		  
		  ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapMsg.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8") ;
			
	  System.out.println("soapMsg  "+ message);

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new InputSource(new StringReader(message.toString())));

		 // Document document = XMLUtil.getDocument(xmlFile);
		  document.getDocumentElement().normalize();

		  Document encryptedDoc = XMLUtil.encryptDocument(document, secretKey, XMLCipher.AES_128);
		  XMLUtil.saveDocumentTo(encryptedDoc, encryptedFile);

		  Document decryptedDoc = XMLUtil.decryptDocument(encryptedDoc, secretKey, XMLCipher.AES_128);
		  XMLUtil.saveDocumentTo(decryptedDoc, decryptedFile);

		  System.out.println("Done");
		 }
	
	 public static Document getDocument(String xmlFile) throws Exception {
		 /* Get the instance of BuilderFactory class. */
		 DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();

		 /* Instantiate DocumentBuilder object. */
		 DocumentBuilder docBuilder = builder.newDocumentBuilder();

		 /* Get the Document object */
		 Document document = docBuilder.parse(xmlFile);
		 return document;
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
		    
	        // SOAP Body
	        Name FinRequestMessage  = soapEnvelope.createName("FinRequestMessage",nbNameSpace,nbNamespaceURI);		
			SOAPBodyElement FinRequestReqtems = soapBody.addBodyElement(FinRequestMessage);
			Name Header = soapEnvelope.createName("Header",nbNameSpace,nbNamespaceURI);
			SOAPElement headerReqtems = FinRequestReqtems.addChildElement(Header);
			Name MsgType = soapEnvelope.createName("MsgType");
			SOAPElement messageTypeReq = headerReqtems.addChildElement(MsgType);
			messageTypeReq.addTextNode("EFT");
			Name ReqType = soapEnvelope.createName("ReqType");
			SOAPElement reqTypeReq = headerReqtems.addChildElement(ReqType);
			reqTypeReq.addTextNode("PA");
			Name ReqID = soapEnvelope.createName("ReqID");
			SOAPElement reqIdReq = headerReqtems.addChildElement(ReqID);
			reqIdReq.addTextNode("1586179742165");
			Name Resend = soapEnvelope.createName("Resend");
			SOAPElement resendReq = headerReqtems.addChildElement(Resend);
			resendReq.addTextNode("xxx");
			Name ChannelId = soapEnvelope.createName("ChannelId");
			SOAPElement chanelIdReq = headerReqtems.addChildElement(ChannelId);
			chanelIdReq.addTextNode("NOT");
			
			Name Content = soapEnvelope.createName("Content",nbNameSpace,nbNamespaceURI);
			SOAPElement ContentReqItems = FinRequestReqtems.addChildElement(Content);
			
			Name Transaction = soapEnvelope.createName("Transaction",nbNameSpace,nbNamespaceURI);
			SOAPElement TransactionReqItems = ContentReqItems.addChildElement(Transaction);
			
			Name Code = soapEnvelope.createName("Code");
			SOAPElement codeReqItems = TransactionReqItems.addChildElement(Code);
			//codeReqItems.addTextNode(   )
			
			
			//code begins here
			Name FinTxnRequestMessage  = soapEnvelope.createName("FinTxnRequestMessage",nbNameSpace,nbNamespaceURI);		
			SOAPElement FinTxnRequestMessagetems = codeReqItems.addChildElement(FinTxnRequestMessage);
			
			//FinTxnRequestMessagetems.setEncodingStyle(  );
			
			///FinTxnRequestMessagetems.setAttribute("EncodingType", BASE64_ENCODING);
			//FinTxnRequestMessagetems.setTextContent(    );
			
			Name TransactionData = soapEnvelope.createName("TransactionData",nbNameSpace,nbNamespaceURI);
			SOAPElement transactionDataReqtems = FinTxnRequestMessagetems.addChildElement(TransactionData);
				
			Name ValueDate = soapEnvelope.createName("MsgType");
			SOAPElement valueDateitem = transactionDataReqtems.addChildElement(ValueDate);
			valueDateitem.addTextNode("2020-09-26");
			Name PaymentRef = soapEnvelope.createName("PaymentRef");
			SOAPElement paymentRefitem = transactionDataReqtems.addChildElement(PaymentRef);
			paymentRefitem.addTextNode("QRT367434G343737");
			Name PayType = soapEnvelope.createName("PayType");
			SOAPElement payTypeitem = transactionDataReqtems.addChildElement(PayType);
			payTypeitem.addTextNode("Debit");
			
			Name Debit = soapEnvelope.createName("Debit",nbNameSpace,nbNamespaceURI);
			SOAPElement debititem = transactionDataReqtems.addChildElement(Debit);
			
			Name AcctID = soapEnvelope.createName("AcctID");
			SOAPElement accountIditem = debititem.addChildElement(AcctID);
			accountIditem.addTextNode("01923232737111");
			Name AccountName = soapEnvelope.createName("AccountName");
			SOAPElement accountnameitem = debititem.addChildElement(AccountName);
			accountnameitem.addTextNode("Moses Kipyegon");
			Name BankSortCode = soapEnvelope.createName("BankSortCode");
			SOAPElement bankSortCodeitem = debititem.addChildElement(BankSortCode);
			bankSortCodeitem.addTextNode("0325");
			Name Amount = soapEnvelope.createName("Amount",nbNameSpace,nbNamespaceURI);
			SOAPElement amountReqitem = debititem.addChildElement(Amount);
			
			Name amount = soapEnvelope.createName("amount");
			SOAPElement amountitem = amountReqitem.addChildElement(amount);
			amountitem.addTextNode("5,500");
			Name isocurrcode = soapEnvelope.createName("isocurrcode");
			SOAPElement isocurrcodeitem = amountReqitem.addChildElement(isocurrcode);
			isocurrcodeitem.addTextNode("403");
			Name XRate = soapEnvelope.createName("XRate");
			SOAPElement xrateitem = amountReqitem.addChildElement(XRate);
			xrateitem.addTextNode("0.26");
			
			Name Narrative = soapEnvelope.createName("Narrative",nbNameSpace,nbNamespaceURI);
			SOAPElement narrativeReqitems = debititem.addChildElement(Narrative);

			Name NarrativeLine1 = soapEnvelope.createName("NarrativeLine1");
			SOAPElement narrativeline1item = narrativeReqitems.addChildElement(NarrativeLine1);
			narrativeline1item.addTextNode("School fees");
			Name NarrativeLine2 = soapEnvelope.createName("NarrativeLine1");
			SOAPElement narrativeline2item = narrativeReqitems.addChildElement(NarrativeLine2);
			narrativeline2item.addTextNode("School fees");
			
			Name ChargeDetail = soapEnvelope.createName("ChargeDetail",nbNameSpace,nbNamespaceURI);
			SOAPElement chargedetailsReqitems = debititem.addChildElement(ChargeDetail);
			
			Name ChargeType = soapEnvelope.createName("ChargeType");
			SOAPElement chargetypeitem = chargedetailsReqitems.addChildElement(ChargeType);
			chargetypeitem.addTextNode("OUR"); //OUR,  BEN,  BEN
			Name ChargeAcctID = soapEnvelope.createName("ChargeAcctID");
			SOAPElement chargeAccountIditem = chargedetailsReqitems.addChildElement(ChargeAcctID);
			chargeAccountIditem.addTextNode("01109273462726700"); // Core debit account for Noti
			
			Name ChargeAmount = soapEnvelope.createName("ChargeAmount",nbNameSpace,nbNamespaceURI);
			SOAPElement chargeamountReqitems = chargedetailsReqitems.addChildElement(ChargeAmount);
			
			Name chargeAmount = soapEnvelope.createName("amount");
			SOAPElement chargeamountIditem = chargeamountReqitems.addChildElement(chargeAmount);
			chargeamountIditem.addTextNode("20");
			Name chargeIsocurrcode = soapEnvelope.createName("isocurrcode");
			SOAPElement chargeisocurrcodeitem = chargeamountReqitems.addChildElement(chargeIsocurrcode);
			chargeisocurrcodeitem.addTextNode("403");

			Name BICcode = soapEnvelope.createName("BICcode");
			SOAPElement bicCodeitem = debititem.addChildElement(BICcode);
			bicCodeitem.addTextNode("345644");
			
			Name BenAddress = soapEnvelope.createName("BenAddress",nbNameSpace,nbNamespaceURI);
			SOAPElement benAddressReqitems = debititem.addChildElement(BenAddress);
			
			Name BenName = soapEnvelope.createName("Name");
			SOAPElement benNameitem = benAddressReqitems.addChildElement(BenName);
			benNameitem.addTextNode("John Smith");
			Name AddressLine1 = soapEnvelope.createName("AddressLine1");
			SOAPElement benAddressLine1item = benAddressReqitems.addChildElement(AddressLine1);
			benAddressLine1item.addTextNode("P.O BOX 254543-433");
			Name CountryCode = soapEnvelope.createName("CountryCode");
			SOAPElement countryCodeitem = benAddressReqitems.addChildElement(CountryCode);
			countryCodeitem.addTextNode("254");

			Name MobileDeviceInfo = soapEnvelope.createName("MobileDeviceInfo",nbNameSpace,nbNamespaceURI);
			SOAPElement mobDeviceInfoReqitems = transactionDataReqtems.addChildElement(MobileDeviceInfo);

			Name MobileNumber = soapEnvelope.createName("MobileNumber");
			SOAPElement custMobileNoitem = mobDeviceInfoReqitems.addChildElement(MobileNumber);
			custMobileNoitem.addTextNode("254");
			Name NumberPlan = soapEnvelope.createName("NumberPlan");
			SOAPElement custNumberPlanitem = mobDeviceInfoReqitems.addChildElement(NumberPlan);
			custNumberPlanitem.addTextNode("MSISDN");
			Name SubscriberIdentity = soapEnvelope.createName("SubscriberIdentity");
			SOAPElement custsubscriberIditem = mobDeviceInfoReqitems.addChildElement(SubscriberIdentity);
			custsubscriberIditem.addTextNode("0733489214");
			Name SubscriberDirectoryNum = soapEnvelope.createName("SubscriberDirectoryNum");
			SOAPElement custSubscriberDirectorNumitem = mobDeviceInfoReqitems.addChildElement(SubscriberDirectoryNum);
			custSubscriberDirectorNumitem.addTextNode("254733489214");
			//code ends here

			
		}catch(Exception e) {
		    e.printStackTrace();

		}
			return message;
			
		}
	

}

package com.AssymetricCrypto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AsymmetricCryptography {
	private static String nbNameSpace = "nb";
	private static String nbNamespaceURI = "http://nbk-test-server.com";
	
	private Cipher cipher;

	public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.cipher = Cipher.getInstance("RSA");
	}

	// https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
	public PrivateKey getPrivate(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	// https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
	public PublicKey getPublic(String filename) throws Exception {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public void encryptFile(byte[] input, File output, PrivateKey key)
		throws IOException, GeneralSecurityException {
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}

	public void decryptFile(byte[] input, File output, PublicKey key)
		throws IOException, GeneralSecurityException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		writeToFile(output, this.cipher.doFinal(input));
	}

	private void writeToFile(File output, byte[] toWrite)
			throws IllegalBlockSizeException, BadPaddingException, IOException {
		FileOutputStream fos = new FileOutputStream(output);
		fos.write(toWrite);
		fos.flush();
		fos.close();
	}

	public String encryptText(String msg, PrivateKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException {
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.encodeBase64String(cipher.doFinal(msg.getBytes("UTF-8")));
	}

	public String decryptText(String msg, PublicKey key)
			throws InvalidKeyException, UnsupportedEncodingException,
			IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
	}

	public byte[] getFileInBytes(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		byte[] fbytes = new byte[(int) f.length()];
		fis.read(fbytes);
		fis.close();
		return fbytes;
	}

	public static void main(String[] args) throws Exception {
		AsymmetricCryptography ac = new AsymmetricCryptography();
		PrivateKey privateKey = ac.getPrivate("KeyPair/privateKey");
		PublicKey publicKey = ac.getPublic("KeyPair/publicKey");
		
		SOAPMessage soapTransationMessage = createFinTxnRequestData();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapTransationMessage.writeTo(stream);
		String txnReqMessage = new String(stream.toByteArray(), "utf-8") ;
		  System.out.println("soapMsg  "+ txnReqMessage);
		//Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new InputSource(new StringReader(message.toString())));
		//document.getDocumentElement().normalize();
		
		    InputStream is = new ByteArrayInputStream(txnReqMessage.getBytes());
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder db = dbf.newDocumentBuilder();
		    Document document = db.parse(is);
			document.getDocumentElement().normalize();

		   // Node rootElement = d.getDocumentElement();
		    NodeList nList1 = document.getElementsByTagName("FinTxnRequestMessage");		 
		    Node node = (Node) nList1.item(0);	
	        Element encryptedElement = (Element) node;
	        String encryptedNode = nodeToString(encryptedElement);
			  System.out.println("Transaction Data String \n  "+ encryptedNode);

		//String msg = "Cryptography is fun!";
		String encryptedTransations = ac.encryptText(encryptedNode, privateKey);
		String decrypted_msg = ac.decryptText(encryptedTransations, publicKey);
		System.out.println("Original Message: " + txnReqMessage +
			"\nEncrypted Message: \n " + encryptedTransations
			+ "\nDecrypted Message: \n " + decrypted_msg);
		
		SOAPMessage FinRequestMessage= createFinRequestMessage(encryptedTransations);
		
		System.out.println("Original Message: " + txnReqMessage +"\nEncrypted FinRequestMessage: \n " + FinRequestMessage);

		if (new File("KeyPair/text.txt").exists()) {
			ac.encryptFile(ac.getFileInBytes(new File("KeyPair/text.txt")),
				new File("KeyPair/text_encrypted.txt"),privateKey);
			ac.decryptFile(ac.getFileInBytes(new File("KeyPair/text_encrypted.txt")),
				new File("KeyPair/text_decrypted.txt"), publicKey);
		} else {
			System.out.println("Create a file text.txt under folder KeyPair");
		}
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
	
	public static SOAPMessage createFinRequestMessage(String encryptedTransaction) { 
	      System.out.println("encryptedTransaction Transformer Exception");
			
			MessageFactory messageFactory = null; SOAPMessage finRequestMessage = null;  SOAPPart soapPart = null;
		
		try {
			 messageFactory =  MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			 finRequestMessage = messageFactory.createMessage();
		   // SOAP Envelope
		     soapPart = finRequestMessage.getSOAPPart();
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
			codeReqItems.addTextNode(encryptedTransaction);
				
		}catch(Exception e) {
		    e.printStackTrace();

		}
			return finRequestMessage;
	}
	
	public static SOAPMessage createFinTxnRequestData() { 
		
		MessageFactory messageFactory = null; SOAPMessage finTxnRequestData = null;  SOAPPart soapPart = null;
	try {
		 messageFactory =  MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		 finTxnRequestData = messageFactory.createMessage();
	 // SOAP Envelope
	     soapPart = finTxnRequestData.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		soapEnvelope.addNamespaceDeclaration(nbNameSpace, nbNamespaceURI);
		
	    SOAPBody soapBody = soapEnvelope.getBody();
	   
		//code begins here
		Name FinTxnRequestMessage  = soapEnvelope.createName("FinTxnRequestMessage");		
		SOAPElement FinTxnRequestMessagetems = soapBody.addBodyElement(FinTxnRequestMessage);
		
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
		return finTxnRequestData;
		
	}
	
	

}

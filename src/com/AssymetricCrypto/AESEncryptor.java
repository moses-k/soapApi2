package com.AssymetricCrypto;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
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

import com.soap.test;

import javax.crypto.spec.IvParameterSpec;

public class AESEncryptor {
    static SecureRandom srandom = new SecureRandom();
    private static final String ALGO = "AES";
	private static final int AES_KEYLENGTH = 128;
	private static byte[] iv = new byte[AES_KEYLENGTH / 8];
	private static final String appendix = "657326756376";
	private static final String jsonString = "GFaS64w3VyEx7OlQ";
	public static final String apiCall = "746170";
	public static final String privateKey = "E:/soap/privateKey";
	public static final String AESKey = "E:/Mos/Integration/NBK/encryption/key/encryptedAESkey.enc";
	public static final String inputFile = "E:/soap/encryptedAESkey";
	private static String nbNameSpace = "web";
	private static String typNameSpace = "typ";
	private static String attribNameSpace = "att";
	private static String nbNamespaceURI = "http://webservices.bankfusion.trapedza.com";
	private static String attNameSpaceUrl = "http://www.misys.com/bankfusion/attributes";
	private static String typNameSpaceUrl = "http://www.misys.com/nb/types";
	/*
	 * 
	 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.bankfusion.trapedza.com" xmlns:att="http://www.misys.com/bankfusion/attributes" xmlns:typ="http://www.misys.com/nb/types">

	   <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:nb="http://nbk-test-server.com">
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
                    <Code>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</Code>
                </nb:Transaction>
            </nb:Content>
        </nb:FinRequestMessage>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>


OkHttpClient client = new OkHttpClient().newBuilder()
  .build();
MediaType mediaType = MediaType.parse("text/xml");
RequestBody body = RequestBody.create(mediaType, XTw5tsLOjw5FWMst99bUL6KqUf/Y85hE90s8uplhCzN/xyM7IClyol+7j+1vwvKJHIXSFyF06yWXCPjISHGEMXE/uUgrSUw3Nce5DWROyCliHNNbdy5BhUsGtQV3dAkdz2zHm64s4GDoJGgulePxf/Ylh1i7ahtl/lKq8IPd8n1SH1EzvlE8yFVJRjjRhyzrnEoBTmHbAcUDMk8vvKAgaV1gYakLJSs2p6H5k3d+p3WhEYUd5bjI5p6/wAnvQCc4C7seSLZWSqnQzM0Wo19Mpm8YI4x7vWOQIp/o5wnBryM4MgcSvy28HggEjja2yMhY564G2UdvDJaGL2+qMLBaZNoVcHZD2mg2NhacKxkBMVbN+yzDHLvCDaD5sO9WTX4+SGiFR4/wWChxuXJCSXhkztFlshfmPBEjACi9MdcdvS3iugk+5rG/F4NH7h6497IdQhBvsmNAIhUaKKvyt5lcZcFsROhm858wNiJ7sgFQsKQo3riu3TL9CLcYXqGl5dJluj5ME4ZzAjSqmVc3sbj3Aq6shh8GiowmRK3jsBA9jU8t9tmfZdfnN+7TbY5XiDM6nw0V2mZQSKGD0pR09nJ0Be1twl+kgM958ir23QkDbEjTJ5BuGIfH829O/yuPr4Z2JI4adHpXO3PYpVy0OfvoFjHg==</typ:Code>\r\n            </typ:Content>\r\n         </web:KnaRequestMessage>\r\n      </web:NBK_KNA_AccountBalance_SRV>\r\n   </soapenv:Body>\r\n</soapenv:Envelope>");
Request request = new Request.Builder()
  .url("http://172.16.2.148:9081/bfweb/services/NBK_MNA_AccountTxnInfoService")
  .method("POST", body)
  .addHeader("SOAPAction", "http://NBK_KNA_AccountBalanceService/NBK_KNA_AccountBalanceServicePortType/NBK_KNA_AccountBalance_SRVRequest")

	 */
	
	public static RSAPublicKey readPublicKey(String path) throws Exception {
	   // String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
		String key = new String (Files.readAllBytes(Paths.get(path)));
	    String publicKeyPEM = key
	      .replace("-----BEGIN PUBLIC KEY-----", "")
	      .replaceAll(System.lineSeparator(), "")
	      .replace("-----END PUBLIC KEY-----", "");

	   // byte[] encoded = Base64.decodeBase64(publicKeyPEM);
		byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
	    
	    return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}
	
	public static RSAPrivateKey readPrivateKey(String path) throws Exception {
		   // String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
			String key = new String (Files.readAllBytes(Paths.get(path)));
		    String privateKeyPEM = key
		      .replace("-----BEGIN PRIVATE KEY-----", "")
		      .replaceAll(System.lineSeparator(), "")
		      .replace("-----END PRIVATE KEY-----", "");

		   // byte[] encoded = Base64.decodeBase64(publicKeyPEM);
			byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
		   // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		   // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			//RSAPrivateKey  RR = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			//System.out.println(" key keySpec E :: " + RR);
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			
		}
	
	public static PublicKey getPublicKey() throws Exception { 
		PublicKey pubkey  = null;
		try {
		 pubkey = readPublicKey("E:\\Mos\\Integration\\NBK\\encryption\\keypair\\noti_rsapublic.pem");
			 //byte[] keyBytes = Files.readAllBytes(Paths.get(AESKey));
		     //EncryptedKey = new String (Files.readAllBytes(Paths.get("E:\\Mos\\Integration\\NBK\\encryption\\keypair\\noti_rsapublic.pem")));
		}catch (Exception e) {
			// TODO: handle exception
		}
	   
	    return  pubkey ;
	}
	
	public static PrivateKey getPrivateKey() throws Exception { 
		PrivateKey pvtkey  = null;
		try {
			pvtkey = readPrivateKey("E:\\Mos\\Integration\\NBK\\encryption\\keypair\\noti_rsaprivate.pem");
			 //byte[] keyBytes = Files.readAllBytes(Paths.get(AESKey));
		     //EncryptedKey = new String (Files.readAllBytes(Paths.get("E:\\Mos\\Integration\\NBK\\encryption\\keypair\\noti_rsapublic.pem")));
		}catch (Exception e) {
			// TODO: handle exception
		}
	   
	    return  pvtkey ;
	}
	
	public static void generateAESKey(PrivateKey privatnyckel) throws Exception { 
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ALGO);
			kgen.init(AES_KEYLENGTH);
			SecretKey AESkey = kgen.generateKey();
			//String AESKEY = Base64.getEncoder().encodeToString(AES.getEncoded());
			//System.out.println("AESKEY is "+ AESKEY);
			 //get the raw key bytes
	        byte[] AESkeybyte =AESkey.getEncoded(); //Returns copy of the key bytes

			srandom.nextBytes(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			
			try (FileOutputStream out = new FileOutputStream(inputFile + ".enc")) {
				{
				  //encrypt AES key with RSA
				    String encryptedAESKey =  encryptAESkey(privatnyckel,AESkeybyte); 
				    out.write(encryptedAESKey.getBytes());
				}
				
		   }
		}catch (Exception e) {
			// TODO: handle exception
		}
	   
	}
	
	public static void main(String arg[]) throws Exception{
		
		try {
			
		//Creating assymmetric keys RSA ( A public Key and Private Key )
		    KeyPairGenerator gen2 = KeyPairGenerator.getInstance("RSA");
		    gen2.initialize(1024);
		    KeyPair keyPair = gen2.genKeyPair();
		   // PrivateKey privatnyckel = keyPair.getPrivate();  //For noti
		    //PublicKey publiknyckel = keyPair.getPublic();//to be given to nbk and the give us their public key
		   
		    PrivateKey privatnyckel = getPrivateKey();  //For noti
		    PublicKey  publiknyckel = getPublicKey();//to be given to nbk and the give us their public key
		  
			//System.out.println("privatnyckel is "+ privatnyckel.getEncoded());
			System.out.println("privatnyckel is "+ Base64.getEncoder().encodeToString(privatnyckel.getEncoded()));
			System.out.println("publiknyckel is "+ Base64.getEncoder().encodeToString(publiknyckel.getEncoded()));
			
			//generate AES Key
		//	generateAESKey(privatnyckel);
			
		//String data = readFileAsString(AESKey); 
	    //System.out.println("Raw data Read from AES file  is  \n"+ data); 
	   String encryptedAESKey =  getAESkey();  // base64
		System.out.println(" encryptedAESKey is :: "+encryptedAESKey );
		
		//decrypt AES key with RSA
	    String decryptedAESKey =  decryptAESkey(publiknyckel,encryptedAESKey); 
		System.out.println(" decryptedAESKey String is :: "+ decryptedAESKey );
		
//		// get the text to encrypt with AES key
//	    String inputText1 = "Hellow world data: ";
//	   
//		String encryptedData = encryptxml(inputText1, decryptedAESKey );
//		System.out.println("new data encryptedData :: "+ encryptedData);
//
//		String decryptedData = decryptxml(encryptedData, decryptedAESKey );
//		System.out.println("new data encryptedData :: "+ decryptedData);
//		
		SOAPMessage soapTransationMessage = createFinTxnRequestData();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapTransationMessage.writeTo(stream);
		String txnReqMessage = new String(stream.toByteArray(), "utf-8") ;
		  System.out.println("soapMsg String  "+ txnReqMessage);
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
			 // System.out.println("Transaction Data String \n  "+ encryptedNode);
			  
				//String msg = "Cryptography is fun!";
				String encryptedTransations = encryptxml(encryptedNode, decryptedAESKey.getBytes() );
				String decryptedData = decryptxml(encryptedTransations, decryptedAESKey.getBytes() );
				System.out.println("Original Message: " + txnReqMessage +
				  "\nEncrypted Transaction Req : \n " + encryptedTransations
				+ "\nDecrypted Transaction Req: \n " + decryptedData);
				
				SOAPMessage FinRequestMessage= createFinRequestMessage(encryptedTransations);
				ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
				FinRequestMessage.writeTo(stream2);
				String TransactionRequestMessage = new String(stream2.toByteArray(), "utf-8") ;	
				
				InputStream ins = new ByteArrayInputStream(TransactionRequestMessage.getBytes());
			    DocumentBuilderFactory dbf1 = DocumentBuilderFactory.newInstance();
			    DocumentBuilder db1 = dbf1.newDocumentBuilder();
			    Document document1 = db1.parse(ins);
				document1.getDocumentElement().normalize();
				
				Node rootElement = document1.getDocumentElement();
		        Element TxnRequestMessage = (Element) rootElement;
		        String TxnRequestMessageString = nodeToString(TxnRequestMessage);
			
				System.out.println("Original Message: " + txnReqMessage +
						"\nEncrypted FinRequestMessageString: \n " + TxnRequestMessageString);
				
				//test.postToNbk(TxnRequestMessageString);
								
		}catch(Exception e) {
			System.out.println(e.getMessage());
			
		}
		
	}
	
	//will be done by the bank
	public static String encryptAESkey(PrivateKey pvtkey,  byte[] AESkey) throws Exception {
		String encryptedAESValue=null;
		try {
			 Cipher pipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			    pipher.init(Cipher.ENCRYPT_MODE, pvtkey);
			    byte[]  encVal= pipher.doFinal(AESkey); 
			    encryptedAESValue = Base64.getEncoder().encodeToString(encVal);
				System.out.println("encryptAESkey "+ encryptedAESValue);

		} catch (Exception e) {
			///PPWalletEnvironment.setComment(1,className, " Exception in encryptAESkey "+e.getMessage());		
			System.out.println(e.getMessage());

		}
        return encryptedAESValue;
    }
	
	//publickey
	public static String decryptAESkey(PublicKey publickey,  String encryptedAESKey) throws Exception {
		String decryptedValue = null;
		try {
			System.out.println("inside decryptAESkey**** "+ encryptedAESKey);

			    Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			    c.init(Cipher.DECRYPT_MODE, publickey);
				byte[] decordedValue = Base64.getDecoder().decode(encryptedAESKey);
				byte[]  decryptedAESKey= c.doFinal(decordedValue);
				decryptedValue = new String(decryptedAESKey);
		} catch (Exception e) {
			System.err.println(e.getMessage());

		}
        return decryptedValue;
    }
	public static String encryptxml(String xmlsoap, byte[] decryptedAESKey) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(decryptedAESKey, ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(xmlsoap.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			//PPWalletEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	
	public static String decryptxml(String encryptedData, byte[] decryptedAESKey) throws Exception {
        String decryptedValue=null;
		try {
			Key key  = new SecretKeySpec(decryptedAESKey, ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			//PPWalletEnvironment.setComment(1,classname, " Exception in decryption "+e.getMessage());		
		}
        return decryptedValue;
    }
	
	public static String decryptAESkey(String Data, String k) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(k.getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			
			//PPWalletEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	
	public static String readFileAsString(String fileName)throws Exception 
	  { 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	  } 
	
	public static PrivateKey getpvtkey() throws Exception {  
		    byte[] keyBytes = Files.readAllBytes(Paths.get(privateKey));
		    PKCS8EncodedKeySpec spec =  new PKCS8EncodedKeySpec(keyBytes);
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    return kf.generatePrivate(spec);
}
	public static String getAESkey() throws Exception { 
		String EncryptedKey  = null;
		try {
			 //byte[] keyBytes = Files.readAllBytes(Paths.get(AESKey));
		     EncryptedKey = new String (Files.readAllBytes(Paths.get(AESKey)));
		     System.out.println("Reading AES key file keyBytes Transformer " +EncryptedKey);

		}catch (Exception e) {
		      System.out.println(e.getMessage());
		}
	   
	    return  EncryptedKey ;
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
	      System.out.println("encryptedTransaction");
			
			MessageFactory messageFactory = null; SOAPMessage finRequestMessage = null;  SOAPPart soapPart = null;
		
		try {
			 messageFactory =  MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			 finRequestMessage = messageFactory.createMessage();
		   // SOAP Envelope
		     soapPart = finRequestMessage.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			soapEnvelope.addNamespaceDeclaration(typNameSpace, typNameSpaceUrl);
			soapEnvelope.addNamespaceDeclaration(attribNameSpace, attNameSpaceUrl);
			soapEnvelope.addNamespaceDeclaration(nbNameSpace, nbNamespaceURI);
			
			
//			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
//			SOAPHeader header = envelope.addHeader();
//			SOAPElement el = header.addChildElement(envelope.createName("userId", "wshh", "http://mycompany.com.br/wsheaderhandlers"));
//			el.setValue(userId);
//			el = header.addChildElement(envelope.createName("ip", "wshh", "http://mycompany.com.br/wsheaderhandlers"));
//			el.setValue(ip);
			
		 // SOAP Header
		    //Generic Soap Header
			
		    SOAPHeader soapHeader = soapEnvelope.getHeader();
		    if (soapHeader == null) {
		    	soapHeader = soapEnvelope.addHeader();
            }
		    
		   // QName qNameUserCredentials = new QName("bfgenericsoapheader", "nbNameSpace");
          //  SOAPHeaderElement userCredentials = soapHeader.addHeaderElement(qNameUserCredentials);

		 //   QName genericHeader = new QName("bfgenericsoapheader", "nbNameSpace");
		    QName genericHeader = soapEnvelope.createQName("bfgenericsoapheader", nbNameSpace);
            SOAPHeaderElement genericHerderItem = soapHeader.addHeaderElement(genericHeader);
		    
		    Name authenticationDetails = soapEnvelope.createName("authentication",nbNameSpace,nbNamespaceURI);
			SOAPElement genericHeaderReqtems = genericHerderItem.addChildElement(authenticationDetails);
			Name userName = soapEnvelope.createName("userName");
			SOAPElement userNameReq = genericHeaderReqtems.addChildElement(userName);
			userNameReq.addTextNode("Mobee");
			Name password = soapEnvelope.createName("password");
			SOAPElement passwordReq = genericHeaderReqtems.addChildElement(password);
			passwordReq.addTextNode("pass123*");
			/**optional Fields**/
			Name passCode = soapEnvelope.createName("passCode");
			SOAPElement passCodeReq = genericHeaderReqtems.addChildElement(passCode);
			passCodeReq.addTextNode("");
			Name userLocator = soapEnvelope.createName("userLocator");
			SOAPElement userLocatorReq = genericHeaderReqtems.addChildElement(userLocator);
			userLocatorReq.addTextNode("");
			Name casRestletUrl = soapEnvelope.createName("casRestletUrl");
			SOAPElement casRestletUrlReq = genericHeaderReqtems.addChildElement(casRestletUrl);
			casRestletUrlReq.addTextNode("");
			Name casValidateUrl = soapEnvelope.createName("casValidateUrl");
			SOAPElement casValidateUrlReq = genericHeaderReqtems.addChildElement(casValidateUrl);
			casValidateUrlReq.addTextNode("");
			
			/**Optional fields**/
			Name BFHeaderDetails = soapEnvelope.createName("BFHeader",attribNameSpace,nbNamespaceURI);
			SOAPElement BFHeaderReqtems = genericHerderItem.addChildElement(BFHeaderDetails);
			Name correlationID = soapEnvelope.createName("correlationID");
			SOAPElement correlationIDeReq = BFHeaderReqtems.addChildElement(correlationID);
			correlationIDeReq.addTextNode("");
			Name zone = soapEnvelope.createName("zone");
			SOAPElement zoneReq = BFHeaderReqtems.addChildElement(zone);
			zoneReq.addTextNode("");
			Name autoAuthorize = soapEnvelope.createName("autoAuthorize");
			SOAPElement autoAuthorizeReq = BFHeaderReqtems.addChildElement(autoAuthorize);
			autoAuthorizeReq.addTextNode("");
			Name applicationContext = soapEnvelope.createName("applicationContext");
			SOAPElement applicationContextReq = BFHeaderReqtems.addChildElement(applicationContext);
			applicationContextReq.addTextNode("");
			Name cachedBPMUsers = soapEnvelope.createName("cachedBPMUsers");
			SOAPElement cachedBPMUsersReq = BFHeaderReqtems.addChildElement(cachedBPMUsers);
			cachedBPMUsersReq.addTextNode("");
			Name destination = soapEnvelope.createName("destination");
			SOAPElement destinationReq = BFHeaderReqtems.addChildElement(destination);
			destinationReq.addTextNode("");
			Name extension = soapEnvelope.createName("extension");
			SOAPElement extensionReq = BFHeaderReqtems.addChildElement(extension);
			/**Optional**/
		    
	        // SOAP Body
		    SOAPBody soapBody = soapEnvelope.getBody();
	        Name FinRequestMessage  = soapEnvelope.createName("FinRequestMessage",nbNameSpace,nbNamespaceURI);		
			SOAPBodyElement FinRequestReqtems = soapBody.addBodyElement(FinRequestMessage);
			Name Header = soapEnvelope.createName("Header",typNameSpace,nbNamespaceURI);
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
			
			Name Content = soapEnvelope.createName("Content",typNameSpace,nbNamespaceURI);
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
		
		Name TransactionData = soapEnvelope.createName("TransactionData");
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
		
		Name Debit = soapEnvelope.createName("Debit");
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
		Name Amount = soapEnvelope.createName("Amount");
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
		
		Name Narrative = soapEnvelope.createName("Narrative");
		SOAPElement narrativeReqitems = debititem.addChildElement(Narrative);

		Name NarrativeLine1 = soapEnvelope.createName("NarrativeLine1");
		SOAPElement narrativeline1item = narrativeReqitems.addChildElement(NarrativeLine1);
		narrativeline1item.addTextNode("School fees");
		Name NarrativeLine2 = soapEnvelope.createName("NarrativeLine1");
		SOAPElement narrativeline2item = narrativeReqitems.addChildElement(NarrativeLine2);
		narrativeline2item.addTextNode("School fees");
		
		Name ChargeDetail = soapEnvelope.createName("ChargeDetail");
		SOAPElement chargedetailsReqitems = debititem.addChildElement(ChargeDetail);
		
		Name ChargeType = soapEnvelope.createName("ChargeType");
		SOAPElement chargetypeitem = chargedetailsReqitems.addChildElement(ChargeType);
		chargetypeitem.addTextNode("OUR"); //OUR,  BEN,  BEN
		Name ChargeAcctID = soapEnvelope.createName("ChargeAcctID");
		SOAPElement chargeAccountIditem = chargedetailsReqitems.addChildElement(ChargeAcctID);
		chargeAccountIditem.addTextNode("01109273462726700"); // Core debit account for Noti
		
		Name ChargeAmount = soapEnvelope.createName("ChargeAmount");
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
		
		Name BenAddress = soapEnvelope.createName("BenAddress");
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

		Name MobileDeviceInfo = soapEnvelope.createName("MobileDeviceInfo");
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

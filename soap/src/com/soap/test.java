package com.soap;


public class test {
	
	public   static void postToNbk(String txnRequestMessageString) {
		
		        OkHttpClient client = new OkHttpClient().newBuilder().build();
				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(mediaType,txnRequestMessageString ); 
				Request request = new Request.Builder()
				  .url("http://197.155.74.150:8820/ppwallet/ws")
				  .method("POST", body)
				  .addHeader("Content-Type", "text/xml")
				  .build();
				Response response = client.newCall(request).execute();
		
		
	}
	
	

}

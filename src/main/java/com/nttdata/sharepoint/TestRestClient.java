package com.nttdata.sharepoint;


import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class TestRestClient {

	private static final String E2E_MODE_DIGEST_URI = "/solutions/e2e/mode/_api/contextinfo";
	private static final String E2E_MODE_LIST_URI = "/solutions/e2e/mode/_api/web/lists/getbytitle('Documents')/items";
	
	private static final String E2E_MODE_LIST_DOCUMENTS_URI = "/solutions/e2e/mode/_api/web/getfolderbyserverrelativeurl('/All Documents')/files";
	
	private static final String APPLICATION_JSON_ODATA_VERBOSE = "application/json;odata=verbose";
	private static final String HTTPS = "https";
	private static final String PEERS_HOST_ADDRESS = "areas.it.nttdata-emea.com";
	private static final String username = "confessorep";
	private static final String password = "Clin123???";
	
	private static final Logger logger =  LoggerFactory.getLogger(TestRestClient.class);
	
	
	static {
		BasicConfigurator.configure();
	}
	
	private static CloseableHttpClient createHttpClient() {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		return httpclient;
	}
	
	
	
	/*
	public static ClientHttpEngine createEngine() {
		return new ApacheHttpClient4Engine(createHttpClient());
	}
	
	public static String callRestEasyRetrieveDigest() {
		try {
			ClientHttpEngine engine = createEngine();
			ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
			WebTarget target = client.target(E2E_MODE_DIGEST_URI);
			Response response = target.request().accept(APPLICATION_JSON_ODATA_VERBOSE).post(Entity.text(""));
			int status = response.getStatus();
			logger.info("Response status: " + status);
			MultivaluedMap<String,Object> headers = response.getHeaders();
			Set<Entry<String,List<Object>>> entrySet = headers.entrySet();
			logger.info("================ HEADERS ==============");
			for (Entry<String, List<Object>> entry : entrySet) {
				logger.info("{}", entry);
			}
			logger.info("=======================================");
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}
	*/
	
	public static HttpClientContext createContext() {
		HttpHost targetHost = new HttpHost(PEERS_HOST_ADDRESS, 443, HTTPS);
		NTCredentials credentials = new NTCredentials(username, password, "", "");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), credentials);
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		return context;
	}
	
	
	public static void postDigestHttpClient() throws Exception {
		CloseableHttpClient httpClient = createHttpClient();
		HttpHost target = new HttpHost(PEERS_HOST_ADDRESS, 443, HTTPS);
		// Execute an expensive method next reusing the same context (and connection)
		HttpPost httppost = new HttpPost(E2E_MODE_DIGEST_URI);
		httppost.addHeader("Accept", APPLICATION_JSON_ODATA_VERBOSE);
		httppost.setEntity(new StringEntity(""));
		HttpClientContext context = createContext();
		CloseableHttpResponse response = httpClient.execute(target, httppost, context);
		try {
			Header[] allHeaders = response.getAllHeaders();
			logger.info("================ HEADERS ==============");
			for (int i = 0; i < allHeaders.length; i++) {
				logger.info("{}", allHeaders[i]);
			}
			logger.info("=======================================");
			StatusLine statusLine = response.getStatusLine();
			logger.info("Response status: {}", statusLine);
		    HttpEntity entity = response.getEntity();
		    if(entity != null) {
		    	String stringResponse = new String(IOUtils.toByteArray(entity.getContent()));
		    	logger.info("Response: {}", stringResponse);
		    	String digestForm = parseJsonDigestValue(stringResponse);
		    	logger.info("Digest form value: {}", digestForm);
		    }
		} finally {
		    response.close();
		}
	}
	
	
	public static void getListItems() throws Exception {
		CloseableHttpClient httpClient = createHttpClient();
		HttpHost target = new HttpHost(PEERS_HOST_ADDRESS, 443, HTTPS);
		// Execute an expensive method next reusing the same context (and connection)
		HttpGet httpget = new HttpGet(E2E_MODE_LIST_DOCUMENTS_URI);
		httpget.addHeader("Accept", APPLICATION_JSON_ODATA_VERBOSE);
		HttpClientContext context = createContext();
		CloseableHttpResponse response = httpClient.execute(target, httpget, context);
		try {
			Header[] allHeaders = response.getAllHeaders();
			logger.info("================ HEADERS ==============");
			for (int i = 0; i < allHeaders.length; i++) {
				logger.info("{}", allHeaders[i]);
			}
			logger.info("=======================================");
			StatusLine statusLine = response.getStatusLine();
			logger.info("Response status: {}", statusLine);
		    HttpEntity entity = response.getEntity();
		    if(entity != null) {
		    	String stringResponse = new String(IOUtils.toByteArray(entity.getContent()));
		    	logger.info("Response: {}", stringResponse);
		    	//String digestForm = parseJsonDigestValue(stringResponse);
		    	//logger.info("Digest form value: {}", digestForm);
		    }
		} finally {
		    response.close();
		}
	}
	
	
	

    /**
     * Retrieves the digest value from the incoming json String
     * 
     * @param json
     * @return
     */
    public static String parseJsonDigestValue(String json) {
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(json);
        jsonObject = (JSONObject) jsonObject.get("d");
        jsonObject = (JSONObject) jsonObject.get("GetContextWebInformation");
        return jsonObject.getString("FormDigestValue");

    }
    
    
    
	
	public static void main(String[] args) {
		try {
			postDigestHttpClient();
			getListItems();
			
			
			
			
		} catch (Exception e) {
			logger.error("Error: {}", e.getMessage());
			logger.error("Stack trace is: ", e);
		}
	}
}

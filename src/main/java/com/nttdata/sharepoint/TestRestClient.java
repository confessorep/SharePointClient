package com.nttdata.sharepoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;

public class TestRestClient {

	
	private static final String username = "";
	private static final String password = "";
	
	
	
	private static final String urlDigest = "https://areas.it.nttdata-emea.com/solutions/e2e/mode/_api/contextinfo";
	
	
	public static ClientHttpEngine createEngine() {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpHost targetHost = new HttpHost("areas.it.nttdata-emea.com/solutions/e2e/mode", 443, "https");
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
		        new UsernamePasswordCredentials("confessorep", "Clin123???"));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);
		
	    return new ApacheHttpClient4Engine(httpclient);
	}
	
	public static String callRestEasyRetrieveDigest() {
		String output = "nothing";
		try {
			ClientHttpEngine engine = createEngine();
			ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
			WebTarget target = client.target(urlDigest);
			Response response = target.request().accept("application/json;odata=verbose").post(Entity.text(""));
			int status = response.getStatus();
			System.out.println("Response status: " + status);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}
	
	public static void main(String[] args) {
		callRestEasyRetrieveDigest();
	}
}

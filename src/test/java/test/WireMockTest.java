package test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.port;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class WireMockTest {
	
	public WireMockTest() {
		baseURI = "http://localhost";
		port = 8089;
		//authentication = basic("user", "password");
	}
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089); // No-args constructor defaults to port 8080
	
	@Test
	public void validator() throws InterruptedException {
		
		    stubFor(get(urlEqualTo("/test"))
		            .willReturn(aResponse()
		                .withHeader("Content-Type", "text/plain")
		                .withBody("Hello world!")));
		
		    Thread.sleep(10000);    
		    
		//Tested rest assured	
			
			get("/test")
		.then()
			.statusCode(200);
			
			get("fakeTest")
		.then()
			.statusCode(404);
			
	}

}

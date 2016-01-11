package test;

import org.junit.BeforeClass;

import com.jayway.restassured.RestAssured;

public abstract class BasicTest {
	
	@BeforeClass
	public static void init() {
		RestAssured.baseURI = "http://localhost:8089";
	}
}

package test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.jayway.restassured.http.ContentType;

import model.LandLord;

public class LandLordTest extends BasicTest {
	
	@Test(priority=0)
	public void getLandlords() {
		
		when()
			.get("/landlords")
		.then()
			.statusCode(200)
			.body("", is(empty()));
			 
	}
	
	@DataProvider(name = "validLandLords")
	public Object[][] createData() {
	 return new Object[][] {
	   { new LandLord("Alex", "Fruz") },
	   { new LandLord("Alex", "Fruz", true) },
	 };
	}
	
	@Test(priority=1, dataProvider="validLandLords", groups="critical")
	public void postLandlord(LandLord landLord) {
		
		String id = given()
			.contentType(ContentType.JSON) 
			.body(landLord)
		.when()
			.post("/landlords")
		.then()
			.statusCode(201)
			.body("firstName", is(landLord.getFirstName()))
			.body("lastName", is(landLord.getLastName()))
			.body("trusted", is(landLord.getTrusted()))
			.body("apartments", is(empty()))
		.extract()
			.path("id");
		
		given()
			.pathParam("id", id)
		.when()
			.get("/landlords/{id}")
		.then()
			.statusCode(200)
			.body("id", is(id))
			.body("firstName", is(landLord.getFirstName()))
			.body("lastName", is(landLord.getLastName()))
			.body("trusted", is(landLord.getTrusted()))
			.body("apartments", is(empty()));
	}
	
	@Test(priority=1)
	public void postLandlordNegative01() {
		
		LandLord landLord = new LandLord("", "");
		
		given()
			.contentType(ContentType.JSON)
			.body(landLord)
		.when()
			.post("/landlords")
		.then()
			.statusCode(400)
			.body("message", is("Fields are with validation errors"))
			.body("fieldErrorDTOs[0].fieldName", is("firstName"))
			.body("fieldErrorDTOs[0].fieldError", is("First name can not be empty"))
			.body("fieldErrorDTOs[1].fieldName", is("lastName"))
			.body("fieldErrorDTOs[1].fieldError", is("Last name can not be empty"));
	}
	
	@Test(priority=2)
	public void putLandLord() {
		
		LandLord landLord = new LandLord("Alex", "Fruz", true);
		
		String id = given()
			.contentType(ContentType.JSON)
			.body(landLord)
		.when()
			.post("/landlords")
		.then()
			.statusCode(201)
		.extract()
			.path("id");
		
		LandLord landLordForUpdate = new LandLord("Alexey", "Zvolinskiy");
		
		given()
			.contentType(ContentType.JSON)
			.body(landLordForUpdate)
			.pathParam("id", id)
		.when()
			.put("/landlords/{id}")
		.then()
			.statusCode(200)
			.body("message", is("LandLord with id: "+id+" successfully updated"));
		
		given()
			.pathParam("id", id)
		.when()
			.get("/landlords/{id}")
		.then()
			.statusCode(200)
			.body("firstName", is(landLordForUpdate.getFirstName()))
			.body("lastName", is(landLordForUpdate.getLastName()))
			.body("trusted", is(false));
		
	}
	
	@Test(priority=4)
	public void deleteLandlord01() {
		
		LandLord landLord = new LandLord("Alex", "Fruz");
		
		String id = given()
			.contentType(ContentType.JSON)
			.body(landLord)
		.when()
			.post("/landlords")
		.then()
			.statusCode(201)
		.extract()
			.path("id");
		
		given()
			.pathParam("id", id)
		.when()
			.delete("/landlords/{id}")
		.then()
			.statusCode(200)
			.body("message", is("LandLord with id: "+id+" successfully deleted"));
		
		given()
			.pathParam("id", id)
		.when()
			.get("/landlords/{id}")
		.then()
			.statusCode(404)
			.body("message", is("There is no LandLord with id: "+id));
	}

}

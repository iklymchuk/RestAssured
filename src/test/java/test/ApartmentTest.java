package test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.jayway.restassured.http.ContentType;

import model.LandLord;
import model.Apartment;

public class ApartmentTest extends BasicTest {
	
	String id = null;
	String apartmentId = null;
	
	@Test(priority=0, groups="critical")
	public void postLandlord() {
		LandLord landLord = new LandLord("Sam", "Peters");
		
		id = given()
				.contentType(ContentType.JSON)
				.body(landLord)
			.when()
				.post("/landlords")
			.then()
				.statusCode(201)
			.extract()
				.path("id");
	}
		
	@Test(dependsOnMethods="postLandlord", groups="critical")
	public void postApartments() {
		Apartment apartment = new Apartment("New York, Wave str 7", 80000, 65);
		
		apartmentId = given()
			.contentType(ContentType.JSON)
			.body(apartment) 
			.pathParam("id", id)
		.when()
			.post("/landlords/{id}/apartments")
		.then()
			.statusCode(201)
			.body("address", is(apartment.getAddress()))
			.body("price", is(80000f))
			.body("square", is(apartment.getSquare()))
			.body("features", is(empty()))
			.body("active", is(apartment.isActive()))
		.extract()
			.path("id");
	}
	
	@DataProvider(name = "apartments")
	public Object[][] createData() {
	 return new Object[][] {
	   { new Apartment("New York, Wave str 7", 80000, 65) },
	   { new Apartment("New York, GreenHill str 11", 87000, 80) },
	   { new Apartment("New York, Fast ave 22", 120000, 90) },
	   { new Apartment("New York, Lotos Park 3", 90000, 95) },
	   { new Apartment("New York, Brown str 48", 100000, 100) },
	   { new Apartment("New York, Astoria sq", 95000, 105) },
	   { new Apartment("New York, North str 18", 92000, 110) },
	   { new Apartment("New York, West ave9", 150000, 115) },
	   { new Apartment("New York, Brown str 7", 10000, 120) },
	 };
	}
	
	@Test(priority=2, dataProvider="apartments", dependsOnMethods="postLandlord")
	public void postApartmentsSet(Apartment apartment) {
		
		given()
			.contentType(ContentType.JSON)
			.body(apartment)
			.pathParam("id", id)
		.when()
			.post("/landlords/{id}/apartments")
		.then()
			.statusCode(201);
	}
	
	@Test(priority=3, dependsOnMethods="postApartmentsSet")
	public void getApartments() {
		
		Apartment[] apartments = given()
			.pathParam("id", id)
			.queryParam("minSq", 90)
			.queryParam("maxSq", 110)
		.when()
			.get("/landlords/{id}/apartments")
		.then()
			.statusCode(200)
		.extract()
			.as(Apartment[].class);
		
		for (Apartment ap : apartments) {
			assertTrue(ap.getSquare() >= 90);
			assertTrue(ap.getSquare() <= 110);
		}
	}
	
	@Test(priority=20, dependsOnMethods="postApartments")
	public void updateApartment() {
		Apartment apartmentToUpdate = new Apartment("New York, High str", 100000, 100);
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.pathParam("apartmentId", apartmentId)
			.body(apartmentToUpdate)
		.when()
			.put("/landlords/{id}/apartments/{apartmentId}")
		.then()
			.statusCode(200)
			.body("message", is("Apartment with id: "+apartmentId+" successfully updated"));
		
		given()
			.pathParam("id", id)
			.pathParam("apartmentId", apartmentId)
		.when()
			.get("/landlords/{id}/apartments/{apartmentId}")
		.then()
			.body("address", is(apartmentToUpdate.getAddress()))
			.body("price", is(100000f))
			.body("square", is(apartmentToUpdate.getSquare()));
		
	}
	
	@Test(priority=25, dependsOnMethods="postApartments")
	public void deleteApartment() {
		
		given()
			.pathParam("id", id)
			.pathParam("apartmentId", apartmentId)
		.when()
			.delete("/landlords/{id}/apartments/{apartmentId}")
		.then()
			.statusCode(200)
			.body("message", equalTo("Apartments with id: "+apartmentId+" successfully deleted"));
		
		given()
			.pathParam("id", id)
			.pathParam("apartmentId", apartmentId)
		.when()
			.get("/landlords/{id}/apartments/{apartmentId}")
		.then()
			.body("message", is("There is no Apartment with id: "+apartmentId));
		
	}

}

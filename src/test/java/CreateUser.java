import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Randomgenerator;

import static io.restassured.RestAssured.given;

public class CreateUser {
    Randomgenerator email = new Randomgenerator();
    public String baseURI="https://staging.bes-learning.com/interviewpro-api/api/v1";

    public String signinEmail ="thor"+email.RandomNumberString()+"@yopmail.com";
    public String fullname = "Test Name";
    public String password = "password";

    public String userEmail;

    @Test
    @Feature("Positive Scenario")
    public void CreateAccount_POST() {
        String body = "{\"fullname\": \"" + fullname + "\", \"email\": \"" + signinEmail + "\", \"password\": \"" + password + "\"}";
        System.out.println(body);

        Response response = given()
                .contentType("application/json")
                .body(body)
                .post(baseURI + "/auth/register");

        if (response.getStatusCode() == 200) {
            // Parse the response JSON string into a JsonObject
            JsonPath jsonPath = response.jsonPath();
            userEmail = jsonPath.get("data.email");
        }
        System.out.println("------------------------------- User Email: " + userEmail);
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "CreateAccount_POST")
    @Feature("Positive Scenario")
    public void ResendEmailVerification_POST(){
        String body = "{\"email\":\"" + userEmail + "\"}";
        System.out.println(body);

        Response response = given()
                .contentType("application/json")
                .body(body)
                .post(baseURI+"/users/resend/verification/email");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "CreateAccount_POST")
    @Feature("Positive Scenario")
    public void ResetPassword_POST(){
        String body = "{\"email\":\"" + userEmail + "\"}";
        System.out.println(body);

        Response response = given()
                .contentType("application/json")
                .body(body)
                .post(baseURI+"/users/forgot-password");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;

public class Auth {
    public String authorizationToken;
    public String baseURI="https://staging.bes-learning.com/interviewpro-api/api/v1";

    public String userEmail ="vishal@frugaltestingin.com";
    public String userPassword = "Vishal@123";

    @Test
    @Feature("Positive Scenario")
    public void LoginUser_POST(){
        String body = "{\"email\":\"" + userEmail + "\",\"password\":\"" + userPassword + "\"}";
        System.out.println(body);

        Response response = given()
                .contentType("application/json")
                .body(body)
                .post(baseURI+"/auth/login");

        if (response.getStatusCode() == 200) {
            // Parse the response JSON string into a JsonObject
            JsonPath jsonPath = response.jsonPath();
            authorizationToken = jsonPath.get("data.authentication.accessToken");
        }

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST"})
    @Feature("Positive Scenario")
    public void UserDetails_GET(){
        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .get(baseURI+"/users");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST"})
    @Feature("Positive Scenario")
    public void LogoutUser_POST(){
        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .post(baseURI+"/auth/logout");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(),200);
    }
}
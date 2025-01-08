import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ChangePassword {
    public String authorizationToken;
    public String baseURI="https://staging.bes-learning.com/interviewpro-api/api/v1";

    public String userEmail ="thor7268729@yopmail.com";
    public String userPassword = "password";
    public String newPassword = "password1";

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

    @Test(dependsOnMethods = "LoginUser_POST")
    @Feature("Positive Scenario")
    public void ChangePassword_POST(){
        String body = "{\"old_password\":\"" + userPassword + "\",\"new_password\":\"" + newPassword + "\"}";
        System.out.println(body);

        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .body(body)
                .post(baseURI+"/users/password-reset");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "LoginUser_POST")
    @Feature("Positive Scenario")
    public void DeleteUser_DELETE(){
        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .delete(baseURI+"/users/delete-account");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}

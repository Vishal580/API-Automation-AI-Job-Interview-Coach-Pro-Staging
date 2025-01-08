import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class Interview {
    public String authorizationToken;
    public String baseURI="https://staging.bes-learning.com/interviewpro-api/api/v1";

    public String userEmail ="vishal@frugaltestingin.com";
    public String userPassword = "Vishal@123";

    public String interviewerId;

    //Chat with persona
    public String industry = "IT Industry";
    public String position = "Web Developer";
    public String content = "Web Developer";

    //get feedback
    public String questionId;

    //Add Answer
    public String answer = "I have 2 years of experience in Web Development.";
    public String type = "1";

    //All Question Answer List
    public String chatId;

    //Save Question or Answer [type : 1 => save question, 2 => save answer, 3 => both] | if same id and type repeat then unsaved
    public String saveChat = "2";

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

    @Test
    @Feature("Positive Scenario")
    public void InterviewList_GET(){
        Response response = given()
                .contentType("application/json")
                .get(baseURI+"/interviews");

        if(response.getStatusCode()==200){
            JsonPath jsonPath = response.jsonPath();
            interviewerId = jsonPath.get("data[0].id").toString();
        }

        System.out.println("-------------------------------------- Interviewer Id:"+ interviewerId);
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET"})
    @Feature("Positive Scenario")
    public void InterviewerDetails_GET(){

        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .queryParam("interviewId", interviewerId)
                .get(baseURI+"/interviews/chat");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET" })
    @Feature("Positive Scenario")
    public void ChatWithPersona_POST(){
        String body = "{\"interviewId\":\"" + interviewerId + "\",\"industry\":\"" + industry + "\", \"position\":\"" + position + "\",\"content\":\"" + content + "\"}";
        System.out.println(body);

        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .body(body)
                .post(baseURI+"/interviews/chat");

        if(response.getStatusCode()==200){
            JsonPath jsonPath = response.jsonPath();
            questionId = jsonPath.get("data.id").toString();
        }

        System.out.println("-------------------------------- questionId: " + questionId);
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET", "ChatWithPersona_POST"})
    @Feature("Positive Scenario")
    public void GetQuestionFeedback_POST(){

        Response response = given()
                .header("Authorization", "Bearer "+authorizationToken)
                .contentType("application/json")
                .queryParam("questionId", questionId)
                .post(baseURI+"/interviews/chat/get-feedback/"+questionId);

        System.out.println("-------------------------------- interviewChatId: " + questionId);

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET", "ChatWithPersona_POST", "GetQuestionFeedback_POST"})
    @Feature("Positive Scenario")
    public void AddAnswer_POST(){

        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("multipart/form-data")
                .multiPart("interviewChatId",questionId)
                .multiPart("answer",answer)
                .multiPart("type",type)
                .post(baseURI+"/interviews/chat/answer");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET", "ChatWithPersona_POST", "GetQuestionFeedback_POST", "AddAnswer_POST"})
    @Feature("Positive Scenario")
    public void EndInterviewChat_POST(){
        String body = "{\"interviewId\":\"" + interviewerId + "\",\"industry\":\"" + industry + "\", \"position\":\"" + position + "\"}";
        System.out.println(body);

        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("application/json")
                .body(body)
                .post(baseURI + "/interviews/chat-end");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST"})
    @Feature("Positive Scenario")
    public void AllSavedQAList_GET(){
        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("application/json")
                .get(baseURI + "/interviews/save");

        if(response.getStatusCode() == 200){
            JsonPath jsonPath = response.jsonPath();
            chatId = jsonPath.get("data.id[0]").toString();
        }
        System.out.println("------------------------------ ChatId: " + chatId);

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "InterviewList_GET", "AllSavedQAList_GET"})
    @Feature("Positive Scenario")
    public void SaveQuestionAnswer_POST(){
        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("application/json")
                .queryParam("id", chatId)
                .queryParam("type", saveChat)
                .post(baseURI + "/interviews/save");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST", "AllSavedQAList_GET"})
    @Feature("Positive Scenario")
    public void EditSavedAnswer_PUT(){
        String body = "{\"answer\":\"Updated the Answer\"}";
        System.out.println(body);

        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("application/json")
                .body(body)
                .put(baseURI + "/interviews/save/edit/" + chatId);

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = {"LoginUser_POST"})
    @Feature("Positive Scenario")
    public void RecentInterviews_GET(){
        Response response = given()
                .header("Authorization", "Bearer "+ authorizationToken)
                .contentType("application/json")
                .get(baseURI + "/interviews/recent-interviews");

        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);
    }

}

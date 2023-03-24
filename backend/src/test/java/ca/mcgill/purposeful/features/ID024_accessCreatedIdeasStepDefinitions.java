package ca.mcgill.purposeful.features;

import ca.mcgill.purposeful.dao.*;
import ca.mcgill.purposeful.dto.IdeaRequestDTO;
import ca.mcgill.purposeful.model.*;
import ca.mcgill.purposeful.util.CucumberUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step Definitions for accessing created ideas
 *
 * @author Ramin Akhavan
 */
public class ID024_accessCreatedIdeasStepDefinitions {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CucumberUtil cucumberUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private URLRepository urlRepository;

    private ResponseEntity<?> response;

    private HttpHeaders authHeader;
    private String jwtToken;
    private Map<String, String> idMap = new HashMap<String, String>();
    private String message;

    @Given("the database contains the following user account info:")
    public void theDatabaseContainsTheFollowingUserAccountInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveRegularUsersFromTable(dataTable, idMap);
    }

    @And("the database contains the following domain object info:")
    public void theDatabaseContainsTheFollowingDomainObjectInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveDomainsFromTable(dataTable, idMap);
    }

    @And("the database contains the following topic object info:")
    public void theDatabaseContainsTheFollowingTopicObjectInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveTopicsFromTable(dataTable, idMap);
    }

    @And("the database contains the following tech object info:")
    public void theDatabaseContainsTheFollowingTechObjectInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveTechsFromTable(dataTable, idMap);
    }

    @And("the database contains the following URL object info:")
    public void theDatabaseContainsTheFollowingURLObjectInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveURLsFromTable(dataTable, idMap);
    }

    @And("the database contains the following idea object info:")
    public void theDatabaseContainsTheFollowingIdeaObjectInfo(DataTable dataTable) {
        cucumberUtil.createAndSaveIdeasFromTable(dataTable, idMap);
    }

    @Given("user with email {string} and password {string} is successfully logged in")
    public void userWithEmailAndPasswordIsSuccessfullyLoggedIn(String email, String password) {
        HttpEntity<String> requestEntity =
                new HttpEntity<>(cucumberUtil.basicAuthHeader(email, password));

        // We don't save this response in the field because we don't need it later
        // In this case we are testing whether the browse ideas response is correct so we only
        // need the token
        ResponseEntity<?> response =
                client.exchange("/api/login", HttpMethod.POST, requestEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Making sure the login was successful
        jwtToken = response.getBody().toString(); // Extract the token for future requests
        assertNotNull(jwtToken); // Ensure the token is not null
    }

    @When("the user requests to access all ideas associated to them")
    public void theUserRequestsToAccessAllIdeasAssociatedToThem() {

    }

    @Then("then all ideas with ids {string} will be provided")
    public void thenAllIdeasWithIdsWillBeProvided(String arg0) {

    }

    @When("the logged out user requests to access all ideas associated to them")
    public void theLoggedOutUserRequestsToAccessAllIdeasAssociatedToThem() {

    }

    @Then("the status code {string} and error {string} will be received")
    public void theStatusCodeAndErrorWillBeReceived(String arg0, String arg1) {

    }

    @When("the user requests to access all ideas associated to them using email {string}")
    public void theUserRequestsToAccessAllIdeasAssociatedToThemUsingEmail(String arg0) {
    }
}

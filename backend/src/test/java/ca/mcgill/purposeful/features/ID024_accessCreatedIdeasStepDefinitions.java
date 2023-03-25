package ca.mcgill.purposeful.features;

import ca.mcgill.purposeful.dao.*;
import ca.mcgill.purposeful.dto.IdeaDTO;
import ca.mcgill.purposeful.dto.IdeaRequestDTO;
import ca.mcgill.purposeful.model.*;
import ca.mcgill.purposeful.service.IdeaService;
import ca.mcgill.purposeful.util.CucumberUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.Assert.assertNotEquals;
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
    private IdeaService ideaService;

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
    private String userEmail;
    ArrayList<IdeaDTO> responseIdeas;
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

    @And("user with email {string} and password {string} is successfully logged in")
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
        userEmail = email;
        assertNotNull(jwtToken); // Ensure the token is not null
    }

    @When("the user requests to access all ideas associated to them")
    public void theUserRequestsToAccessAllIdeasAssociatedToThem() {
        this.authHeader = cucumberUtil.bearerAuthHeader(jwtToken);
        this.authHeader.setContentType(MediaType.APPLICATION_JSON);
        this.authHeader.setAccessControlAllowOrigin("*");

        HttpEntity<?> requestEntity = new HttpEntity<>(this.authHeader);


        if(this.authHeader != null){
            this.response =
                    client.exchange("/api/idea/user/", HttpMethod.GET, requestEntity, ArrayList.class);

            ObjectMapper mapper = new ObjectMapper();
            responseIdeas = mapper.convertValue(response.getBody(), new TypeReference<ArrayList<IdeaDTO>>() {
            });
        } else {
            this.response =
                    client.exchange("/api/idea/user/", HttpMethod.GET, requestEntity, String.class);
        }
    }

    @Then("then all ideas with ids {string} will be provided")
    public void thenAllIdeasWithIdsWillBeProvided(String ideaIds) {
        if (ideaIds.isEmpty()){
            assertTrue(responseIdeas.isEmpty());
        }
        else {
            List<String> ideaIdsList = Arrays.asList(ideaIds.split(","));

            assertEquals(ideaIdsList.size(), responseIdeas.size());

            for (int i = 0; i < ideaIdsList.size(); i++) {
                assertEquals(
                        ideaService.getIdeaById(idMap.get(ideaIdsList.get(i))).getTitle(),
                        responseIdeas.get(i).getTitle());
            }
        }


    }

    @Then("the status code {string} unauthorized error will be received")
    public void theStatusCodeUnauthorizedErrorWillBeReceived(String status_code) {
        assertEquals(status_code, Integer.toString(this.response.getStatusCode().value()));
    }
}

package ca.mcgill.purposeful.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import ca.mcgill.purposeful.dao.*;
import ca.mcgill.purposeful.model.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * To test the idea service methods
 *
 * @author Wassim Jabbour, Adam Kazma (creating Idea tests), Ramin Akhavan-Sarraf (modifying Idea
 *     tests)
 */
@ExtendWith(MockitoExtension.class)
public class TestIdeaService {

  private static String NEW_TITLE = "new title";
  private static String NEW_PURPOSE = "new purpose";
  private static String NEW_DESCRIPTION = "new description";

  private static boolean NEW_PAY = true;
  private static boolean NEW_PRIVACY = true;
  private static boolean NEW_PROGRESS = true;

  // Mocks
  @Mock private IdeaRepository ideaRepository;

  @Mock private DomainRepository domainRepository;

  @Mock private TopicRepository topicRepository;

  @Mock private TechnologyRepository technologyRepository;

  @Mock private URLRepository urlRepository;

  @Mock private RegularUserRepository regularUserRepository;

  @Mock private CollaborationRequestRepository collaborationRequestRepository;

  // Inject mocks
  @InjectMocks private IdeaService ideaService;

  // Set the mock output of each function in the repository
  @BeforeEach
  public void setMockOutput() {
    lenient()
        .when(ideaRepository.findIdeaById(anyString()))
        .thenAnswer(MockRepository::findIdeaById);
    lenient().when(ideaRepository.save(any(Idea.class))).thenAnswer(MockRepository::save);
    lenient().when(ideaRepository.findAll()).thenAnswer(MockRepository::findAll);
    lenient()
        .when(domainRepository.findDomainById(anyString()))
        .thenAnswer(MockRepository::findDomainById);
    lenient()
        .when(topicRepository.findTopicById(anyString()))
        .thenAnswer(MockRepository::findTopicById);
    lenient()
        .when(technologyRepository.findTechnologyById(anyString()))
        .thenAnswer(MockRepository::findTechnologyById);
    lenient().when(urlRepository.findURLById(anyString())).thenAnswer(MockRepository::findURLById);
    lenient()
        .when(regularUserRepository.findRegularUserByAppUserEmail(anyString()))
        .thenAnswer(MockRepository::findRegularUserByAppUserEmail);
    lenient()
        .when(collaborationRequestRepository.findAll())
        .thenAnswer(MockRepository::findAllRequests);
  }

  /**
   * Test access all user's created ideas
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testGetCreatedIdeas_Success() {
    String testUserEmail = MockDatabase.user1.getAppUser().getEmail();

    List<Idea> createdIdeas = ideaService.getCreatedIdeas(testUserEmail);

    assertTrue(!createdIdeas.isEmpty());
    assertTrue(createdIdeas.contains(MockDatabase.idea1));
    assertTrue(createdIdeas.contains(MockDatabase.idea2));
  }

  /**
   * Test access all user's created ideas Fail case where email is empty or null
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testGetCreatedIdeas_NullEmail() {
    String testUserEmail = null;

    try {
      List<Idea> createdIdeas = ideaService.getCreatedIdeas(testUserEmail);
    } catch (Exception e) {
      assertEquals("Please enter a valid email. Email cannot be left empty", e.getMessage());
    }
  }

  /**
   * Test access all user's created ideas Empty list is returned if email is not associated to an
   * idea
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testGetCreatedIdeas_EmptyList() {
    String testUserEmail = "example2@gmail.com";

    List<Idea> createdIdeas = ideaService.getCreatedIdeas(testUserEmail);

    assertTrue(createdIdeas.isEmpty());
  }

  /**
   * Test the getIdeaById method (Success case)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeaById_Success() {

    // Try to get the idea by its ID
    Idea fetchedIdea = ideaService.getIdeaById(MockDatabase.idea1.getId());

    // Check that the idea is the same as the one in the database
    assertNotNull(fetchedIdea);
    assertEquals(MockDatabase.idea1, fetchedIdea);
  }

  /**
   * Test the getIdeaById method (Failure case)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeaById_NullId() {

    // Try to get the idea by its null ID
    try {
      Idea fetchedIdea = ideaService.getIdeaById(null);
    } catch (Exception e) {
      assertEquals("Please enter a valid UUID. UUID cannot be empty.", e.getMessage());
      return;
    }
    fail();
  }

  /**
   * Test getting ideas by all criteria (Success case 1)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeasByAllCriteria_Success1() {

    // Extract the list of domains, topics, and techs to search by in String form

    // Domains
    List<String> search_domains = new ArrayList<>();
    for (Domain domain : MockDatabase.domainGroup1) {
      search_domains.add(domain.getName());
    }

    // Topics
    List<String> search_topics = new ArrayList<>();
    for (Topic topic : MockDatabase.topicGroup1) {
      search_topics.add(topic.getName());
    }

    // Techs
    List<String> search_techs = new ArrayList<>();
    for (Technology tech : MockDatabase.techGroup1) {
      search_techs.add(tech.getName());
    }

    // Try to get the ideas by all criteria
    Iterable<Idea> fetchedIdeas =
        ideaService.getIdeasByAllCriteria(search_domains, search_topics, search_techs);

    // Check that the ideas list fetched isn't null
    assertNotNull(fetchedIdeas);

    // Check that the ideas are from most recent to oldest
    Iterator<Idea> iterator = fetchedIdeas.iterator();
    assertEquals(MockDatabase.idea4, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea3, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea2, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea1, iterator.next());
    assertFalse(iterator.hasNext()); // Check only the 4 ideas are in the list
  }

  /**
   * Test getting ideas by all criteria (Success case 2)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeasByAllCriteria_Success2() {

    // Extract the list of domains, topics, and techs to search by in String form
    // In this case, we only expect idea 1 to show up in the results

    // Domains
    List<String> search_domains = new ArrayList<>();
    search_domains.add(MockDatabase.domain2.getName());

    // Topics
    List<String> search_topics = new ArrayList<>();
    search_topics.add(MockDatabase.topic2.getName());

    // Techs
    List<String> search_techs = new ArrayList<>();
    search_techs.add(MockDatabase.tech2.getName());

    // Try to get the ideas by all criteria
    Iterable<Idea> fetchedIdeas =
        ideaService.getIdeasByAllCriteria(search_domains, search_topics, search_techs);
    Iterator<Idea> iterator = fetchedIdeas.iterator();

    // Check that the ideas list fetched has only 1 idea, that is idea 1
    assertNotNull(fetchedIdeas);
    assertEquals(MockDatabase.idea1, iterator.next());
    assertFalse(iterator.hasNext());
  }

  /**
   * Test getting ideas by all criteria (Success case 3)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeasByAllCriteria_Success3() {

    // Try to get the ideas by date (All criteria are null)
    Iterable<Idea> fetchedIdeas = ideaService.getIdeasByAllCriteria(null, null, null);
    Iterator<Idea> iterator = fetchedIdeas.iterator();

    // Check that the ideas list fetched has all 3 ideas
    assertNotNull(fetchedIdeas);
    assertEquals(MockDatabase.idea4, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea3, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea2, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea1, iterator.next());
    assertFalse(iterator.hasNext());
  }

  /**
   * Test getting ideas by all criteria (Failure case 1)
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testGetIdeasByAllCriteria_NoMatch() {

    // Convert topic group 3 to a list (No one has topic 4 which is in topic group
    // 3)
    List<String> search_topics = new ArrayList<>();
    for (Topic topic : MockDatabase.topicGroup3) {
      search_topics.add(topic.getName());
    }

    // Try to get the ideas by date (All criteria are null)
    try {
      Iterable<Idea> fetchedIdeas = ideaService.getIdeasByAllCriteria(null, search_topics, null);
    } catch (Exception e) {
      assertEquals(
          "No ideas match the given criteria. Please try again with different criteria.",
          e.getMessage());
      return;
    }
    fail();
  }

  /**
   * Test creation of idea
   *
   * @author Adam Kazma
   */
  @Test
  public void testCreationOfIdea() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    // Retrieve Ids of all objects
    for (Domain domain : MockDatabase.modifiableDomainGroup) {
      domainIds.add(domain.getId());
    }
    for (Topic topic : MockDatabase.modifiableTopicGroup) {
      topicIds.add(topic.getId());
    }
    for (Technology tech : MockDatabase.modifiableTechGroup) {
      techIds.add(tech.getId());
    }
    for (URL url : MockDatabase.modifiableImgUrlGroup) {
      imgUrlIds.add(url.getId());
    }
    // Create idea
    Idea createdIdea = null;
    try {
      createdIdea =
          ideaService.createIdea(
              NEW_TITLE,
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              MockDatabase.newIconUrl.getId(),
              MockDatabase.user1.getAppUser().getEmail());
    } catch (Exception e) {

    }

    // Test all attributes of idea
    assertEquals(NEW_TITLE, createdIdea.getTitle());
    assertEquals(NEW_PURPOSE, createdIdea.getPurpose());
    assertEquals(NEW_DESCRIPTION, createdIdea.getDescription());

    assertEquals(NEW_PAY, createdIdea.isPaid());
    assertEquals(NEW_PROGRESS, createdIdea.isInProgress());
    assertEquals(NEW_PRIVACY, createdIdea.isPrivate());

    // Check Ids of all objects of the idea
    for (Domain domain : createdIdea.getDomains()) {
      assertTrue(domainIds.contains(domain.getId()));
    }
    for (Technology tech : createdIdea.getTechs()) {
      assertTrue(techIds.contains(tech.getId()));
    }
    for (Topic topic : createdIdea.getTopics()) {
      assertTrue(topicIds.contains(topic.getId()));
    }
    for (URL url : createdIdea.getSupportingImageUrls()) {
      assertTrue(imgUrlIds.contains(url.getId()));
    }
    assertEquals(createdIdea.getIconUrl().getId(), MockDatabase.newIconUrl.getId());
  }

  /**
   * Creating an idea with empty attribute
   *
   * @author Adam Kazma
   */
  @Test
  public void testCreateIdeaWithInvalidEmptyFieldFailure() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    // Retrieve Ids of all objects
    for (Domain domain : MockDatabase.modifiableDomainGroup) {
      domainIds.add(domain.getId());
    }
    for (Topic topic : MockDatabase.modifiableTopicGroup) {
      topicIds.add(topic.getId());
    }
    for (Technology tech : MockDatabase.modifiableTechGroup) {
      techIds.add(tech.getId());
    }
    for (URL url : MockDatabase.modifiableImgUrlGroup) {
      imgUrlIds.add(url.getId());
    }

    // Create an idea with an empty title
    Idea createdIdea = null;
    String message = "";
    try {
      createdIdea =
          ideaService.createIdea(
              "",
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              MockDatabase.newIconUrl.getId(),
              MockDatabase.user1.getAppUser().getEmail());
    } catch (Exception e) {
      message = e.getMessage();
    }

    // Check error message
    assertEquals("Necessary fields have been left empty", message);
  }

  /**
   * Test non-exsting object violation
   *
   * @author Adam Kazma
   */
  @Test
  public void testCreateIdeaWithNonExistingObjectFailure() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    // Retrieve Ids of all objects
    for (Domain domain : MockDatabase.modifiableDomainGroup) {
      domainIds.add(domain.getId());
    }
    for (Topic topic : MockDatabase.modifiableTopicGroup) {
      topicIds.add(topic.getId());
    }
    for (Technology tech : MockDatabase.modifiableTechGroup) {
      techIds.add(tech.getId());
    }
    for (URL url : MockDatabase.modifiableImgUrlGroup) {
      imgUrlIds.add(url.getId());
    }

    String nonExistingId = "FakeImage";

    // Create an idea with a non-existing object
    Idea createdIdea = null;
    String message = "";
    try {
      createdIdea =
          ideaService.createIdea(
              NEW_TITLE,
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              nonExistingId,
              MockDatabase.user1.getAppUser().getEmail());
    } catch (Exception e) {
      message = e.getMessage();
    }
    // Check error message
    assertEquals("You are attempting to link your idea to an object that does not exist", message);
  }

  /**
   * Test all attributes changing
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testModifyAllAttributesOfIdea() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    Set<Domain> domains = new HashSet<>();
    Set<Topic> topics = new HashSet<>();
    Set<Technology> techs = new HashSet<>();
    List<URL> imgUrls = new ArrayList<>();

    // Retrieve Ids of all objects
    for (Domain domain : MockDatabase.modifiableDomainGroup) {
      domainIds.add(domain.getId());
    }
    for (Topic topic : MockDatabase.modifiableTopicGroup) {
      topicIds.add(topic.getId());
    }
    for (Technology tech : MockDatabase.modifiableTechGroup) {
      techIds.add(tech.getId());
    }
    for (URL url : MockDatabase.modifiableImgUrlGroup) {
      imgUrlIds.add(url.getId());
    }
    // Modify all attributes of idea
    Idea updatedIdea = null;
    try {
      updatedIdea =
          ideaService.modifyIdea(
              MockDatabase.modifiableIdea.getId(),
              NEW_TITLE,
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              MockDatabase.newIconUrl.getId());
    } catch (Exception e) {
      String message = e.getMessage();
    }

    // Test all attributes of idea
    assertEquals(NEW_TITLE, updatedIdea.getTitle());
    assertEquals(NEW_PURPOSE, updatedIdea.getPurpose());
    assertEquals(NEW_DESCRIPTION, updatedIdea.getDescription());

    assertEquals(NEW_PAY, updatedIdea.isPaid());
    assertEquals(NEW_PROGRESS, updatedIdea.isInProgress());
    assertEquals(NEW_PRIVACY, updatedIdea.isPrivate());

    // assertEquals(NEW_DATE.toString(), updatedIdea.getDate().toString());

    // Check Ids of all objects of the idea
    for (Domain domain : updatedIdea.getDomains()) {
      assertTrue(domainIds.contains(domain.getId()));
    }
    for (Technology tech : updatedIdea.getTechs()) {
      assertTrue(techIds.contains(tech.getId()));
    }
    for (Topic topic : updatedIdea.getTopics()) {
      assertTrue(topicIds.contains(topic.getId()));
    }
    for (URL url : updatedIdea.getSupportingImageUrls()) {
      assertTrue(imgUrlIds.contains(url.getId()));
    }
    assertEquals(updatedIdea.getIconUrl().getId(), MockDatabase.newIconUrl.getId());
  }

  /**
   * Test empty attribute violation
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testModifyIdeaWithInvalidEmptyFieldFailure() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    // Modify an existing idea with an empty title
    Idea updatedIdea = null;
    String message = "";
    try {
      updatedIdea =
          ideaService.modifyIdea(
              MockDatabase.modifiableIdea.getId(),
              "",
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              MockDatabase.newIconUrl.getId());
    } catch (Exception e) {
      message = e.getMessage();
    }

    // Check error message
    assertEquals("Necessary fields have been left empty", message);
  }

  /**
   * Test non-exsting object violation
   *
   * @author Ramin Akhavan
   */
  @Test
  public void testModifyIdeaWithNonExistingObjectFailure() {
    List<String> domainIds = new ArrayList<String>();
    List<String> topicIds = new ArrayList<String>();
    List<String> techIds = new ArrayList<String>();
    List<String> imgUrlIds = new ArrayList<String>();

    String fakeId = "Ram";
    domainIds.add(fakeId);

    // Modify an idea with a non-existing object
    Idea updatedIdea = null;
    String message = "";
    try {
      updatedIdea =
          ideaService.modifyIdea(
              MockDatabase.modifiableIdea.getId(),
              NEW_TITLE,
              NEW_PURPOSE,
              NEW_DESCRIPTION,
              NEW_PAY,
              NEW_PROGRESS,
              NEW_PRIVACY,
              domainIds,
              techIds,
              topicIds,
              imgUrlIds,
              MockDatabase.newIconUrl.getId());
    } catch (Exception e) {
      message = e.getMessage();
    }
    // Check error message
    assertEquals("You are attempting to link your idea to an object that does not exist", message);
  }

  /**
   * Test getting ideas by Collaboration Request (Success case 1). The user made collaboration
   * requests before
   *
   * @author Enzo Benoit-Jeannin
   */
  @Test
  public void testGetIdeasByCollaborationRequest_Success1() {

    // Try to get the ideas by all criteria
    List<Idea> fetchedIdeas =
        ideaService.getIdeasByCollaborationRequest(MockDatabase.appUser2.getEmail());

    // Check that the ideas list fetched isn't null
    assertNotNull(fetchedIdeas);

    // Check that the ideas are from most recent to oldest
    Iterator<Idea> iterator = fetchedIdeas.iterator();
    assertEquals(MockDatabase.idea4, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea3, iterator.next());
    assertFalse(iterator.hasNext()); // Check only the 2 ideas are in the list
  }

  /**
   * Test getting ideas by Collaboration Request (Success case 2). The user did not make
   * collaboration requests before
   *
   * @author Enzo Benoit-Jeannin
   */
  @Test
  public void testGetIdeasByCollaborationRequest_Success2() {

    // Try to get the ideas by all criteria
    List<Idea> fetchedIdeas =
        ideaService.getIdeasByCollaborationRequest(MockDatabase.appUser1.getEmail());

    // Check that the ideas list fetched isn't null
    assertNotNull(fetchedIdeas);

    // Check that the ideas are from most recent to oldest
    Iterator<Idea> iterator = fetchedIdeas.iterator();
    assertTrue(fetchedIdeas.isEmpty());
  }

  /**
   * Test getting ideas by Collaboration Request empty email (Failure case)
   *
   * @author Enzo Benoit-Jeannin
   */
  @Test
  public void testGetIdeasByCollaborationRequest_EmptyEmail() {

    try {
      // Try to get the ideas by all criteria using an empty email
      List<Idea> fetchedIdeas = ideaService.getIdeasByCollaborationRequest("");
    } catch (Exception e) {
      assertEquals("Email cannot be left empty! ", e.getMessage());
    }
  }

  /**
   * Test getting ideas by Collaboration Request null email (Failure case)
   *
   * @author Enzo Benoit-Jeannin
   */
  @Test
  public void testGetIdeasByCollaborationRequest_NullEmail() {
    try {
      // Try to get the ideas by all criteria using a null email
      List<Idea> fetchedIdeas = ideaService.getIdeasByCollaborationRequest(null);
    } catch (Exception e) {
      assertEquals("Email cannot be left empty! ", e.getMessage());
    }
  }

  /**
   * Test getting ideas by Collaboration Request non-existing email (Failure case)
   *
   * @author Enzo Benoit-Jeannin
   */
  @Test
  public void testGetIdeasByCollaborationRequest_NonExistingEmail() {
    try {
      // Try to get the ideas by all criteria using a non-existing email
      List<Idea> fetchedIdeas =
          ideaService.getIdeasByCollaborationRequest("nonexistingemail@email.com");
    } catch (Exception e) {
      assertEquals("This account does not exist.", e.getMessage());
    }
  }

  /**
   * This class holds all of the mock methods of the CRUD repositories
   *
   * @author Wassim Jabbour
   */
  class MockRepository {

    static Idea findIdeaById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(MockDatabase.idea1.getId())) {
        return MockDatabase.idea1;
      } else if (id.equals(MockDatabase.idea2.getId())) {
        return MockDatabase.idea2;
      } else if (id.equals(MockDatabase.modifiableIdea.getId())) {
        return MockDatabase.modifiableIdea;
      } else if (id.equals(MockDatabase.idea3.getId())) {
        return MockDatabase.idea3;
      } else if (id.equals(MockDatabase.idea4.getId())) {
        return MockDatabase.idea4;
      } else {
        return null;
      }
    }

    static Domain findDomainById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(MockDatabase.originalDomain.getId())) {
        return MockDatabase.originalDomain;
      } else if (id.equals(MockDatabase.newDomain.getId())) {
        return MockDatabase.newDomain;
      } else if (id.equals(MockDatabase.domain1.getId())) {
        return MockDatabase.domain1;
      } else if (id.equals(MockDatabase.domain2.getId())) {
        return MockDatabase.domain2;
      } else {
        return null;
      }
    }

    static Technology findTechnologyById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(MockDatabase.originalTech.getId())) {
        return MockDatabase.originalTech;
      } else if (id.equals(MockDatabase.newTech.getId())) {
        return MockDatabase.newTech;
      } else if (id.equals(MockDatabase.tech1.getId())) {
        return MockDatabase.tech1;
      } else if (id.equals(MockDatabase.tech2.getId())) {
        return MockDatabase.tech2;
      } else {
        return null;
      }
    }

    static Topic findTopicById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(MockDatabase.originalTopic.getId())) {
        return MockDatabase.originalTopic;
      } else if (id.equals(MockDatabase.newTopic.getId())) {
        return MockDatabase.newTopic;
      } else if (id.equals(MockDatabase.topic1.getId())) {
        return MockDatabase.topic1;
      } else if (id.equals(MockDatabase.topic2.getId())) {
        return MockDatabase.topic2;
      } else if (id.equals(MockDatabase.topic3.getId())) {
        return MockDatabase.topic3;
      } else if (id.equals(MockDatabase.topic4.getId())) {
        return MockDatabase.topic3;
      } else {
        return null;
      }
    }

    static URL findURLById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(MockDatabase.originalImgUrl.getId())) {
        return MockDatabase.originalImgUrl;
      } else if (id.equals(MockDatabase.newImgUrl.getId())) {
        return MockDatabase.newImgUrl;
      } else if (id.equals(MockDatabase.newImgUrl2.getId())) {
        return MockDatabase.newImgUrl2;
      } else if (id.equals(MockDatabase.newIconUrl.getId())) {
        return MockDatabase.newIconUrl;
      } else if (id.equals(MockDatabase.originalIconUrl.getId())) {
        return MockDatabase.originalIconUrl;
      } else {
        return null;
      }
    }

    static Idea save(InvocationOnMock invocation) {
      return invocation.getArgument(0);
    }

    static Iterable<Idea> findAll(InvocationOnMock invocation) {
      HashSet<Idea> ideas = new HashSet<>();
      ideas.add(MockDatabase.idea1);
      ideas.add(MockDatabase.idea2);
      ideas.add(MockDatabase.idea3);
      ideas.add(MockDatabase.idea4);
      return ideas;
    }

    static Iterable<CollaborationRequest> findAllRequests(InvocationOnMock invocation) {
      HashSet<CollaborationRequest> requests = new HashSet<>();
      requests.add(MockDatabase.collaborationRequest1);
      requests.add(MockDatabase.collaborationRequest2);
      return requests;
    }

    static RegularUser findRegularUserByAppUserEmail(InvocationOnMock invocation) {
      String email = invocation.getArgument(0);
      if (email.equals(MockDatabase.appUser1.getEmail())) {
        return MockDatabase.user1;
      } else if (email.equals(MockDatabase.appUser2.getEmail())) {
        return MockDatabase.user2;
      } else {
        return null;
      }
    }
  }

  /**
   * Test the removeIdeaById method (Success case)
   *
   * @author Athmane Benarous
   */
  @Test
  public void testRemoveIdeaById() {
    // idea var
    Idea idea = MockDatabase.idea1;

    // call service layer
    ideaService.removeIdeaById(idea.getId());

    // Verify
    verify(ideaRepository, times(1)).deleteById(idea.getId());
  }

  /** This class holds all of the mock objects of the database */
  static final class MockDatabase {

    /** Create mock objects here * */

    // Ideas
    static Idea idea1 = new Idea();

    static Idea idea2 = new Idea();

    static Idea idea3 = new Idea();

    static Idea idea4 = new Idea();

    static Idea modifiableIdea = new Idea();

    // Users
    static AppUser appUser1 = new AppUser();
    static RegularUser user1 = new RegularUser();
    static AppUser appUser2 = new AppUser();
    static RegularUser user2 = new RegularUser();

    // Collaboration requests
    static CollaborationRequest collaborationRequest1 = new CollaborationRequest();
    static CollaborationRequest collaborationRequest2 = new CollaborationRequest();

    // Domains
    static Domain domain1 = new Domain();
    static Domain domain2 = new Domain();

    static Domain originalDomain = new Domain();
    static Domain newDomain = new Domain();

    // Domain groups (A set of multiple of the above domains)
    static HashSet<Domain> domainGroup1 = new HashSet<>();
    static HashSet<Domain> domainGroup2 = new HashSet<>();

    static HashSet<Domain> originalDomainGroup = new HashSet<>();
    static HashSet<Domain> modifiableDomainGroup = new HashSet<>();

    // Topics
    static Topic topic1 = new Topic();
    static Topic topic2 = new Topic();
    static Topic topic3 = new Topic();
    static Topic topic4 = new Topic();

    static Topic originalTopic = new Topic();
    static Topic newTopic = new Topic();

    // Topic groups (A set of multiple of the above topics)
    static HashSet<Topic> topicGroup1 = new HashSet<>();
    static HashSet<Topic> topicGroup2 = new HashSet<>();
    static HashSet<Topic> topicGroup3 = new HashSet<>();

    static HashSet<Topic> originalTopicGroup = new HashSet<>();
    static HashSet<Topic> modifiableTopicGroup = new HashSet<>();

    // Techs
    static Technology tech1 = new Technology();
    static Technology tech2 = new Technology();

    static Technology originalTech = new Technology();
    static Technology newTech = new Technology();

    // Tech groups (A set of multiple of the above techs)
    static HashSet<Technology> techGroup1 = new HashSet<>();
    static HashSet<Technology> techGroup2 = new HashSet<>();

    static HashSet<Technology> originalTechGroup = new HashSet<>();
    static HashSet<Technology> modifiableTechGroup = new HashSet<>();

    static URL originalImgUrl = new URL();
    static URL newImgUrl = new URL();
    static URL newImgUrl2 = new URL();

    static List<URL> originalImgUrlGroup = new ArrayList<>();
    static List<URL> modifiableImgUrlGroup = new ArrayList<>();

    static URL originalIconUrl = new URL();
    static URL newIconUrl = new URL();

    /**
     * Initialize fields here
     *
     * @author Wassim Jabbour and Enzo Benoit-Jeannin
     */
    static {

      // Initialize users
      appUser1.setEmail("example@gmail.com");
      user1.setAppUser(appUser1);
      appUser2.setEmail("example2@gmail.com");
      user2.setAppUser(appUser2);

      // Initialize topics
      topic1.setId(UUID.randomUUID().toString());
      topic1.setName("Music");

      topic2.setId(UUID.randomUUID().toString());
      topic2.setName("Art");

      topic3.setId(UUID.randomUUID().toString());
      topic3.setName("Sports");

      topic4.setId(UUID.randomUUID().toString());
      topic4.setName("Business");

      originalTopic.setId(UUID.randomUUID().toString());
      originalTopic.setName("Algebra");

      newTopic.setId(UUID.randomUUID().toString());
      newTopic.setName("Calculus");

      // Initialize topic groups by merging topics
      topicGroup1.add(topic1);
      topicGroup1.add(topic2);
      topicGroup1.add(topic3);

      topicGroup2.add(topic1);

      topicGroup3.add(topic4);
      originalTopicGroup.add(originalTopic);
      modifiableTopicGroup.add(originalTopic);
      modifiableTopicGroup.add(newTopic);

      // Initialize domains
      domain1.setId(UUID.randomUUID().toString());
      domain1.setName("Software Engineering");

      domain2.setId(UUID.randomUUID().toString());
      domain2.setName("business");

      originalDomain.setId(UUID.randomUUID().toString());
      originalDomain.setName("Management");

      newDomain.setId(UUID.randomUUID().toString());
      newDomain.setName("Photography");

      // Initialize domain groups by merging domains
      domainGroup1.add(domain1);
      domainGroup1.add(domain2);

      domainGroup2.add(domain1);

      originalDomainGroup.add(originalDomain);
      modifiableDomainGroup.add(originalDomain);
      modifiableDomainGroup.add(newDomain);

      // Initialize techs
      tech1.setId(UUID.randomUUID().toString());
      tech1.setName("Java");

      tech2.setId(UUID.randomUUID().toString());
      tech2.setName("Python");

      originalTech.setId(UUID.randomUUID().toString());
      originalTech.setName("Pascal");

      newTech.setId(UUID.randomUUID().toString());
      newTech.setName("Cobol");

      // Initialize tech groups by merging techs
      techGroup1.add(tech1);
      techGroup1.add(tech2);

      techGroup2.add(tech1);

      originalTechGroup.add(originalTech);
      modifiableTechGroup.add(newTech);

      originalImgUrl.setId(UUID.randomUUID().toString());
      originalImgUrl.setURL("www.something.com");

      newImgUrl.setId(UUID.randomUUID().toString());
      newImgUrl.setURL("www.another.com");

      newImgUrl2.setId(UUID.randomUUID().toString());
      newImgUrl2.setURL("www.thirdone.com");

      originalImgUrlGroup.add(originalImgUrl);
      modifiableImgUrlGroup.add(newImgUrl);
      modifiableImgUrlGroup.add(newImgUrl2);

      originalIconUrl.setId(UUID.randomUUID().toString());
      originalIconUrl.setURL("www.mainicon.com");

      newIconUrl.setId(UUID.randomUUID().toString());
      newIconUrl.setURL("www.newmainicon.com");

      // Initialize ideas
      idea1.setId(UUID.randomUUID().toString());
      idea1.setDate(new Date(10000)); // 10000 seconds since 1970 (Other constructors are
      // deprecated)
      idea1.setDescription("Cool web application for playing chess");
      idea1.setDomains(domainGroup1);
      idea1.setTopics(topicGroup1);
      idea1.setTechs(techGroup1);
      idea1.setUser(user1);

      idea2.setId(UUID.randomUUID().toString());
      idea2.setDate(new Date(12000)); // 12000 seconds since 1970 (Other constructors are
      // deprecated)
      idea2.setDescription("Cool web application for generating music");
      idea2.setDomains(domainGroup2);
      idea2.setTopics(topicGroup2);
      idea2.setTechs(techGroup2);
      idea2.setUser(user1);

      idea3.setId(UUID.randomUUID().toString());
      idea3.setDate(new Date(13000)); // 13000 seconds since 1970 (Other constructors are
      // deprecated)
      idea3.setPaid(false);
      idea3.setPrivate(false);
      idea3.setInProgress(false);
      idea3.setDescription("Cool web application for playing piano");
      idea3.setDomains(domainGroup2);
      idea3.setTopics(topicGroup2);
      idea3.setTechs(techGroup2);
      idea3.setUser(user1);

      idea4.setId(UUID.randomUUID().toString());
      idea4.setDate(new Date(15000)); // 15000 seconds since 1970 (Other constructors are
      // deprecated)
      idea4.setPaid(false);
      idea4.setPrivate(false);
      idea4.setInProgress(false);
      idea4.setDescription("Cool web application for playing guitar");
      idea4.setDomains(domainGroup2);
      idea4.setTopics(topicGroup2);
      idea4.setTechs(techGroup2);
      idea4.setUser(user1);

      modifiableIdea.setId(UUID.randomUUID().toString());
      modifiableIdea.setDate(new Date(14000)); // 14000 seconds since 1970 (Other constructors are
      // deprecated)
      modifiableIdea.setPaid(false);
      modifiableIdea.setPrivate(false);
      modifiableIdea.setInProgress(false);
      modifiableIdea.setDescription("Volatile application");
      modifiableIdea.setDomains(originalDomainGroup);
      modifiableIdea.setTopics(originalTopicGroup);
      modifiableIdea.setTechs(originalTechGroup);
      modifiableIdea.setUser(user1);

      // Initialize Collaboration requests
      collaborationRequest1.setRequester(user2);
      collaborationRequest1.setIdea(idea3);
      collaborationRequest2.setRequester(user2);
      collaborationRequest2.setIdea(idea4);
    }
  }
}

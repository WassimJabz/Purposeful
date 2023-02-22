package ca.mcgill.purposeful.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.mcgill.purposeful.dao.IdeaRepository;
import ca.mcgill.purposeful.model.Domain;
import ca.mcgill.purposeful.model.Idea;
import ca.mcgill.purposeful.model.Reaction;
import ca.mcgill.purposeful.model.RegularUser;
import ca.mcgill.purposeful.model.Technology;
import ca.mcgill.purposeful.model.Topic;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
 * @author Wassim Jabbour
 */
@ExtendWith(MockitoExtension.class)
public class TestIdeaService {

  // Mocks
  @Mock
  private IdeaRepository ideaRepository;

  // Inject mocks
  @InjectMocks
  private IdeaService ideaService;

  // Set the mock output of each function in the repository
  @BeforeEach
  public void setMockOutput() {
    lenient()
        .when(ideaRepository.findIdeaById(anyString()))
        .thenAnswer(MockRepository::findIdeaById);
    lenient().when(ideaRepository.save(any(Idea.class))).thenAnswer(MockRepository::save);
    lenient().when(ideaRepository.findAll()).thenAnswer(MockRepository::findAll);
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
    assertEquals(MockDatabase.idea2, iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals(MockDatabase.idea1, iterator.next());
    assertFalse(iterator.hasNext()); // Check only the 2 ideas are in the list
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

    // Check that the ideas list fetched has only 1 idea, that is idea 1
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

    // Convert topic group 3 to a list (No one has topic 4 which is in topic group 3)
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
      return ideas;
    }
  }

  /**
   * This class holds all of the mock objects of the database *
   *
   * @author Wassim Jabbour
   */
  static final class MockDatabase {

    /**
     * Create mock objects here *
     */

    // Ideas
    static Idea idea1 = new Idea();

    static Idea idea2 = new Idea();

    // Users
    static RegularUser user1 = new RegularUser();

    // Domains
    static Domain domain1 = new Domain();
    static Domain domain2 = new Domain();

    // Domain groups (A set of multiple of the above domains)
    static HashSet<Domain> domainGroup1 = new HashSet<>();
    static HashSet<Domain> domainGroup2 = new HashSet<>();

    // Topics
    static Topic topic1 = new Topic();
    static Topic topic2 = new Topic();
    static Topic topic3 = new Topic();
    static Topic topic4 = new Topic();

    // Topic groups (A set of multiple of the above topics)
    static HashSet<Topic> topicGroup1 = new HashSet<>();
    static HashSet<Topic> topicGroup2 = new HashSet<>();
    static HashSet<Topic> topicGroup3 = new HashSet<>();

    // Techs
    static Technology tech1 = new Technology();
    static Technology tech2 = new Technology();

    // Tech groups (A set of multiple of the above techs)
    static HashSet<Technology> techGroup1 = new HashSet<>();
    static HashSet<Technology> techGroup2 = new HashSet<>();

    // Reactions
    static Reaction reaction1 = new Reaction();
    static Reaction reaction2 = new Reaction();

    /**
     * Initialize fields here
     *
     * @author Wassim Jabbour
     */
    static {

      // Initialize topics
      topic1.setId(UUID.randomUUID().toString());
      topic1.setName("Music");

      topic2.setId(UUID.randomUUID().toString());
      topic2.setName("Art");

      topic3.setId(UUID.randomUUID().toString());
      topic3.setName("Sports");

      topic4.setId(UUID.randomUUID().toString());
      topic4.setName("Business");

      // Initialize topic groups by merging topics
      topicGroup1.add(topic1);
      topicGroup1.add(topic2);
      topicGroup1.add(topic3);

      topicGroup2.add(topic1);

      topicGroup3.add(topic4);

      // Initialize domains
      domain1.setId(UUID.randomUUID().toString());
      domain1.setName("Software Engineering");

      domain2.setId(UUID.randomUUID().toString());
      domain2.setName("business");

      // Initialize domain groups by merging domains
      domainGroup1.add(domain1);
      domainGroup1.add(domain2);

      domainGroup2.add(domain1);

      // Initialize techs
      tech1.setId(UUID.randomUUID().toString());
      tech1.setName("Java");

      tech2.setId(UUID.randomUUID().toString());
      tech2.setName("Python");

      // Initialize tech groups by merging techs
      techGroup1.add(tech1);
      techGroup1.add(tech2);

      techGroup2.add(tech1);

      // Initialize ideas
      idea1.setId(UUID.randomUUID().toString());
      idea1.setDate(
          new Date(10000)); // 10000 seconds since 1970 (Other constructors are deprecated)
      idea1.setDescription("Cool web application for playing chess");
      idea1.setDomains(domainGroup1);
      idea1.setTopics(topicGroup1);
      idea1.setTechs(techGroup1);
      idea1.setUser(user1);

      idea2.setId(UUID.randomUUID().toString());
      idea2.setDate(
          new Date(12000)); // 12000 seconds since 1970 (Other constructors are deprecated)
      idea2.setDescription("Cool web application for generating music");
      idea2.setDomains(domainGroup2);
      idea2.setTopics(topicGroup2);
      idea2.setTechs(techGroup2);
      idea2.setUser(user1);
    }
  }
}

package ca.mcgill.purposeful.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import ca.mcgill.purposeful.dao.IdeaRepository;
import ca.mcgill.purposeful.dao.ReactionRepository;
import ca.mcgill.purposeful.dao.RegularUserRepository;
import ca.mcgill.purposeful.model.*;
import ca.mcgill.purposeful.model.Reaction.ReactionType;
import java.util.Date;
import java.util.HashSet;
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
 * @author Athmane Benarous
 */
@ExtendWith(MockitoExtension.class)
public class TestReactionService {

  // Mocks
  @Mock private ReactionRepository reactionRepository;
  @Mock private IdeaRepository ideaRepository;
  @Mock private RegularUserRepository regularUserRepository;
  @Mock private IdeaService ideaService;

  // Inject mocks
  @InjectMocks private ReactionService reactionService;

  // Set the mock output of each function in the repository
  @BeforeEach
  public void setMockOutput() {
    lenient()
        .when(reactionRepository.findReactionById(anyString()))
        .thenAnswer(ReactionMockRepository::findReactionById);

    lenient()
        .when(reactionRepository.findReactionByIdea_IdAndRegularUser_Id(anyString(), anyString()))
        .thenAnswer(ReactionMockRepository::findReactionByIdeaAndRegularUser);

    lenient()
        .when(reactionRepository.save(any(Reaction.class)))
        .thenAnswer(ReactionMockRepository::save);

    lenient()
        .when(ideaRepository.findIdeaById(anyString()))
        .thenAnswer(ReactionMockRepository::findIdeaById);

    lenient()
        .when(regularUserRepository.findRegularUserById(anyString()))
        .thenAnswer(ReactionMockRepository::findRegularUserById);
  }

  /**
   * Test the react method (Delete reaction case)
   *
   * @author Athmane Benarous
   */
  @Test
  public void testReact_Delete() {
    // target reaction
    Reaction targetReaction = ReactionMockDatabase.reaction1;

    // Call service layer
    Reaction reaction =
        reactionService.react(
            targetReaction.getDate(),
            targetReaction.getReactionType(),
            targetReaction.getIdea().getId(),
            targetReaction.getRegularUser().getId());

    // Verify
    assertNull(reaction);
    verify(reactionRepository, times(1)).deleteReactionById(ReactionMockDatabase.reaction1.getId());
  }

  /**
   * Test the react method (Create reaction case)
   *
   * @author Athmane Benarous
   */
  @Test
  public void testReact_Create() {
    // new reaction
    Reaction newReaction = new Reaction();

    newReaction.setId(UUID.randomUUID().toString());
    newReaction.setDate(new Date(12000));
    newReaction.setReactionType(ReactionType.HighFive);
    newReaction.setIdea(ReactionMockDatabase.idea2);
    newReaction.setRegularUser(ReactionMockDatabase.user1);

    // Call service layer
    Reaction reaction =
        reactionService.react(
            newReaction.getDate(),
            newReaction.getReactionType(),
            newReaction.getIdea().getId(),
            newReaction.getRegularUser().getId());

    // Verify
    assertNotNull(reaction);
    verify(reactionRepository, times(1)).save(reaction);
  }

  /**
   * Test the react method (Invalid idea case)
   *
   * @author Athmane Benarous
   */
  @Test
  public void testReact_Failure() {
    // new reaction
    Reaction newReaction = new Reaction();

    newReaction.setId(UUID.randomUUID().toString());
    newReaction.setDate(new Date(12000));
    newReaction.setReactionType(ReactionType.HighFive);
    newReaction.setIdea(ReactionMockDatabase.idea2);
    newReaction.setRegularUser(ReactionMockDatabase.user1);

    // Call service layer
    Reaction reaction =
        reactionService.react(
            newReaction.getDate(),
            newReaction.getReactionType(),
            newReaction.getIdea().getId(),
            newReaction.getRegularUser().getId());

    // Verify
    assertNotNull(reaction);
    verify(reactionRepository, times(1)).save(reaction);
  }

  /**
   * This class holds all of the mock methods of the CRUD repositories
   *
   * @author Wassim Jabbour, Athmane Benarous
   */
  class ReactionMockRepository {

    static Idea findIdeaById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(TestReactionService.ReactionMockDatabase.idea1.getId())) {
        return TestReactionService.ReactionMockDatabase.idea1;
      } else if (id.equals(TestReactionService.ReactionMockDatabase.idea2.getId())) {
        return TestReactionService.ReactionMockDatabase.idea2;
      } else {
        return null;
      }
    }

    static Reaction findReactionById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(TestReactionService.ReactionMockDatabase.reaction1.getId())) {
        return TestReactionService.ReactionMockDatabase.reaction1;
      } else {
        return null;
      }
    }

    static Reaction findReactionByIdeaAndRegularUser(InvocationOnMock invocation) {
      String idea_id = invocation.getArgument(0);
      String user_id = invocation.getArgument(1);
      if (user_id.equals(ReactionMockDatabase.user1.getId())) {
        if (idea_id.equals(TestReactionService.ReactionMockDatabase.idea1.getId())) {
          return ReactionMockDatabase.reaction1;
        } else {
          return null;
        }
      } else {
        return null;
      }
    }

    static Reaction save(InvocationOnMock invocation) {
      return invocation.getArgument(0);
    }

    static RegularUser findRegularUserById(InvocationOnMock invocation) {
      String id = invocation.getArgument(0);
      if (id.equals(TestReactionService.ReactionMockDatabase.user1.getId())) {
        return TestReactionService.ReactionMockDatabase.user1;
      } else {
        return null;
      }
    }
  }

  /**
   * This class holds all of the mock objects of the database *
   *
   * @author Wassim Jabbour, Athmane Benarous
   */
  static final class ReactionMockDatabase {

    /** Create mock objects here * */

    // Ideas
    static Idea idea1 = new Idea();

    static Idea idea2 = new Idea();

    // Users
    static RegularUser user1 = new RegularUser();
    static AppUser appUser1 = new AppUser();

    // Domains
    static Domain domain1 = new Domain();
    static Domain domain2 = new Domain();

    // Domain groups (A set of multiple of the above domains)
    static HashSet<Domain> domainGroup1 = new HashSet<>();
    static HashSet<Domain> domainGroup2 = new HashSet<>();

    // Topics
    static Topic topic1 = new Topic();
    static Topic topic2 = new Topic();

    // Topic groups (A set of multiple of the above topics)
    static HashSet<Topic> topicGroup1 = new HashSet<>();
    static HashSet<Topic> topicGroup2 = new HashSet<>();

    // Techs
    static Technology tech1 = new Technology();
    static Technology tech2 = new Technology();

    // Tech groups (A set of multiple of the above techs)
    static HashSet<Technology> techGroup1 = new HashSet<>();
    static HashSet<Technology> techGroup2 = new HashSet<>();

    // Reactions
    static Reaction reaction1 = new Reaction();

    /**
     * Initialize fields here
     *
     * @author Wassim Jabbour, Athmane Benarous
     */
    static {

      // Initialize topics
      topic1.setId(UUID.randomUUID().toString());
      topic1.setName("Music");

      topic2.setId(UUID.randomUUID().toString());
      topic2.setName("Art");

      // Initialize topic groups by merging topics
      topicGroup1.add(topic1);
      topicGroup1.add(topic2);

      topicGroup2.add(topic1);

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

      // Initialize regular user
      user1.setId(UUID.randomUUID().toString());
      user1.setAppUser(appUser1);
      user1.setInterests(topicGroup1);
      user1.setDomains(domainGroup1);
      user1.setVerifiedCompany(false);

      // Initialize reaction
      reaction1.setId(UUID.randomUUID().toString());
      reaction1.setDate(new Date(10000));
      reaction1.setReactionType(ReactionType.HighFive);
      reaction1.setIdea(idea1);
      reaction1.setRegularUser(user1);
    }
  }
}

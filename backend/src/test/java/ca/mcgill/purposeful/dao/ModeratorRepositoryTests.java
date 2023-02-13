package ca.mcgill.purposeful.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.purposeful.configuration.Authority;
import ca.mcgill.purposeful.model.AppUser;
import ca.mcgill.purposeful.model.Moderator;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Moderator Repository testing class which initiates an moderator and an AppUser repository, executes the tests, then clears each instance from the database.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ModeratorRepositoryTests {

  // the repository we are testing
  @Autowired
  private ModeratorRepository moderatorRepository;

  // also create an appUser
  @Autowired
  private AppUserRepository appUserRepository;
  /**
   * Clear the database before all tests
   * @author Athmane Benarous
   */
  @AfterEach
  public void clearDatabase() {
    moderatorRepository.deleteAll();
    appUserRepository.deleteAll();
  }

  /**
   * Moderator testing method which creates, populates the attributes, sets associations, and saves each moderator and appUser object and identifier.
   * It can then test to make sure each object reached from the moderator found in the repository is not null and that each initially saved Id corresponds to the one
   * reached from the repository.
   */
  @Test
  public void testPersistAndLoadModerator() {

// MANDATORY CLASS TESTS

    // create the appUser and fill its properties
    AppUser appUser = new AppUser() ;

    Set<Authority> authorities = new HashSet<Authority>();
    authorities.add(Authority.Owner);

    appUser.setEmail("stewie.griffin@mcgill.ca");
    appUser.setUsername("stewieGriffin456");
    appUser.setPassword("verySecurePassword456");
    appUser.setAuthorities(authorities);

    // save the appUser
    appUserRepository.save(appUser);

    // create the moderator
    Moderator moderator = new Moderator() ;
    moderator.setAppUser(appUser);

    // save the moderator
    moderatorRepository.save(moderator);

    // get the id of moderator and appUser then save into variables
    String moderatorId = moderator.getId();
    String appUserId = appUser.getId();

    // set moderator and appUser to null
    moderator = null;
    appUser = null;

    // get the moderator back from the database using the Id
    moderator = moderatorRepository.findModeratorById(moderatorId);

    // make sure moderator and appUser are not null
    assertNotNull(moderator);
    assertNotNull(moderator.getAppUser());

    // make sure the created moderatorId and appUserId match those in the database
    assertEquals(moderatorId, moderator.getId());
    assertEquals(appUserId, moderator.getAppUser().getId());
  }
}
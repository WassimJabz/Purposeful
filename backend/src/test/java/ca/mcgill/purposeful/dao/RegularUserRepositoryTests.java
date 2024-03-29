package ca.mcgill.purposeful.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.purposeful.configuration.Authority;
import ca.mcgill.purposeful.model.AppUser;
import ca.mcgill.purposeful.model.RegularUser;
import ca.mcgill.purposeful.util.DatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test the persistence layer for the RegularUserRepository. Testing reading and writing of objects,
 * attributes and references to the database.
 *
 * @author Siger Ma
 */
@SpringBootTest
public class RegularUserRepositoryTests {

  @Autowired private RegularUserRepository regularUserRepository;

  @Autowired private AppUserRepository appUserRepository;

  /** Clear the database before all tests */
  @BeforeAll
  public static void clearDatabaseBefore(@Autowired DatabaseUtil util) {
    util.clearDatabase();
  }

  /** Clear the database after each test */
  @AfterEach
  public void clearDatabaseAfter(@Autowired DatabaseUtil util) {
    util.clearDatabase();
  }

  @Test
  public void testPersistRegularUser() {
    // Create a mew AppUser
    AppUser appUser = new AppUser();
    appUser.setEmail("regular.user@email.com");
    appUser.setFirstname("Rob");
    appUser.setLastname("Sab");
    appUser.setPassword("password");
    appUser.getAuthorities().add(Authority.User);
    appUserRepository.save(appUser);

    // Create a new RegularUser
    RegularUser regularUser = new RegularUser();
    regularUser.setVerifiedCompany(false);
    regularUser.setAppUser(appUser);
    regularUserRepository.save(regularUser);

    // Read the RegularUser from the database
    String regularUserId = regularUser.getId();
    String appUserId = appUser.getId();
    regularUser = null;
    appUser = null;
    regularUser = regularUserRepository.findRegularUserById(regularUserId);
    appUser = appUserRepository.findAppUserById(appUserId);

    // Check that the RegularUser was persisted
    assertNotNull(regularUser);
    assertNotNull(appUser);
    assertEquals(regularUserId, regularUser.getId());
    assertEquals(appUserId, appUser.getId());
    assertEquals(false, regularUser.isVerifiedCompany());
    assertEquals(appUserId, regularUser.getAppUser().getId());
  }
}

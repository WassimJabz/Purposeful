package ca.mcgill.purposeful.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.purposeful.model.Technology;
import ca.mcgill.purposeful.util.DatabaseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TechnologyRepositoryTests {

  @Autowired private TechnologyRepository technologyRepository;

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

  /**
   * Creates a technology, saves it in the database and then retrieves it by id and checks that
   * works
   *
   * @author Wassim Jabbour
   */
  @Test
  public void testPersistAndLoadUrlById() {

    // Initialize the name
    String str = "Spring boot";

    // Create a technology
    Technology technology = new Technology();
    technology.setName(str);

    // Save the technology and extract the id
    technologyRepository.save(technology);
    String id = technology.getId();

    // Set the technology back to null
    technology = null;

    // Check it can be retrieved
    technology = technologyRepository.findTechnologyById(id);
    assertNotNull(technology);
    assertEquals(str, technology.getName());
    assertEquals(id, technology.getId());
  }
}

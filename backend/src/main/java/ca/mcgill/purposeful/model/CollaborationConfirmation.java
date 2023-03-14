package ca.mcgill.purposeful.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

/**
 * The CollaborationConfirmation class, the model for regular user's collaboration confirmation to a
 * request in the database
 */
@Entity
public class CollaborationConfirmation {

  // ------------------------
  // CollaborationConfirmation Attributes
  // ------------------------

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  @Column(nullable = true)
  private String additionalContact;

  @Column(nullable = true)
  private String message;

  // ------------------------
  // CollaborationConfirmation Constructor
  // ------------------------

  public CollaborationConfirmation() {}

  // ------------------------
  // Getter/Setter Methods
  // ------------------------

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAdditionalContact() {
    return additionalContact;
  }

  public void setAdditionalContact(String additionalContact) {
    this.additionalContact = additionalContact;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
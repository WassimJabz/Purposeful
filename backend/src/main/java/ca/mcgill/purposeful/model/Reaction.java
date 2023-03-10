package ca.mcgill.purposeful.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

/** The Reaction class, the model for all reactions in the database */
@Entity
public class Reaction {

  // ------------------------
  // Enumerations
  // ------------------------

  public enum ReactionType {
    HighFive
  }

  // ------------------------
  // Reaction Attributes
  // ------------------------

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;

  @Column(nullable = false)
  private ReactionType reactionType;

  @Column(nullable = false)
  private Date date;

  // private Time time;

  // ------------------------
  // Reaction Associations
  // ------------------------

  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "idea_id", nullable = false)
  private Idea idea;

  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private RegularUser regularUser;

  // ------------------------
  // Reaction Constructor
  // ------------------------

  public Reaction() {}

  // ------------------------
  // Getter/Setter Methods
  // ------------------------

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ReactionType getReactionType() {
    return reactionType;
  }

  public void setReactionType(ReactionType reactionType) {
    this.reactionType = reactionType;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Idea getIdea() {
    return idea;
  }

  public void setIdea(Idea idea) {
    this.idea = idea;
  }

  public RegularUser getRegularUser() {
    return regularUser;
  }

  public void setRegularUser(RegularUser regularUser) {
    this.regularUser = regularUser;
  }
}

Feature: Modify Moderator
  As a user, I want to be able to express my support for an idea by giving it a "high five," so that I can express my positive sentiment for an idea without adding to a numerical score or ranking.

    # reaction database

  Background:
    Given the database contains the following users before high fiving an idea:
      | id | firstname | lastname | email                    | password |
      | 0  | Leia      | Organa   | leia.organa@republic.org | P@ssw0rd |
    And the database contains the following domains before high fiving an idea:
      | id | name     |
      | 1  | Software |
    And the database contains the following topics before high fiving an idea:
      | id | name    |
      | 2  | Music   |
      | 3  | Biology |
    And the database contains the following techs before high fiving an idea:
      | id | name    |
      | 4  | PyTorch |
      | 5  | React   |
    And the database contains the following URLs before high fiving an idea:
      | id | url                                                                                 |
      | 6  | https://www.flaticon.com/free-icon/music_3844724                                    |
      | 7  | https://miro.medium.com/v2/resize:fit:4800/format:webp/1*IWBf4ZlgysgEl-AaUJedRQ.png |
    And the database contains the following ideas before high fiving an idea:
      | id | title            | purpose                                           | domains | topics | techs | supportingImageUrls | iconUrl | isPaid | isInProgress | isPrivate | user |
      | 8  | Music generation | Open sourced software to generate classical music | 1       | 2      | 4, 5  | 7                   | 6       | false  | false        | false     | 0    |
      | 9  | Techno boom      | Open sourced software to generate techno music    | 1       | 2      | 4     |                     | 6       | false  | false        | false     | 0    |
    And the database contains the following reactions before high fiving an idea:
      | id | date       | reactionType | idea_id | appUser_id |
      | 1  | 25-02-2023 | HighFive     | 1       | 0          |
      | 2  | 25-02-2023 | HighFive     | 3       | 0          |
    And I am logged in before high fiving an idea

    # Normal Flow

  Scenario Outline: Successfully high five an idea
    When the user with email "<user_id>" reacts with a reaction "<reactionType>" to an idea with id "<idea_id>"
    Then a new reaction of idea "<idea_id>" and user "<user_id>" shall be added to the reaction database

    Examples:
      | id | idea_id | user_id                  | reactionType |
      | 1  | 1       | leia.organa@republic.org | HighFive     |
      | 2  | 3       | leia.organa@republic.org | HighFive     |
      | 3  | 4       | leia.organa@republic.org | HighFive     |

    # Alternate Flow

  Scenario Outline: Successfully high five an idea which I already high fived to remove the high five
    When the user with email "<user_id>" reacts again with a reaction "<reactionType>" to an idea with id "<idea_id>"
    Then the reaction entry of idea "<idea_id>" and user "<user_id>" shall be removed from the reaction database

    Examples:
      | id | idea_id | user_id                  | reactionType |
      | 2  | 3       | leia.organa@republic.org | HighFive     |

    # Error Flow

  Scenario Outline: Unsuccessfully high five an idea on behalf of another user
    When the user requests to react with the reactionType "<reactionType>" to an idea with id "<idea_id>" on behalf of another regular user with id "<appUser_email>"
    Then the error message "<error>" will be thrown with status code "<Http_status>" and the reaction database will not be modified

    Examples:
      | date       | reactionType | idea_id | appUser_email         | error                                                                 | Http_status |
      | 25-02-2023 | domains      | 8       | nonexistant@email.com | You are attempting to link your idea to an object that does not exist | 400         |
      | 25-02-2023 | topics       | 9       | nonexistant@email.com | You are attempting to link your idea to an object that does not exist | 400         |
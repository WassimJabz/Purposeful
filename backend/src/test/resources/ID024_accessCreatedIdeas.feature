Feature: Access Created Ideas
  As a user who has created an idea, I want to be able to access all the ideas I have created so that I can keep track of them and their associated collaboration requests
  Background:
    Given the database contains the following user account info:
      | id | firstname | lastname | email                | password         |
      | 0  | User      | Steve    | user.steve@gmail.com | SteveIsAwesome01 |
      | 1  | User      | Bob      | user.bob@gmail.com   | BobIsAwesome01   |
      | 2  | User      | Joe      | user.joe@gmail.com   | JoeIsAwesome01   |
    And the database contains the following domain object info:
      | id | name       |
      | 3  | Software   |
      | 4  | Computer   |
      | 5  | Electrical |
    And the database contains the following topic object info:
      | id | name              |
      | 6  | Frontend Dev      |
      | 7  | Backend Dev       |
    And the database contains the following tech object info:
      | id | name   |
      | 8  | Python |
      | 9  | Java   |
      | 10 | React  |
      | 11 | C      |
    And the database contains the following URL object info:
      | id | url           |
      | 12 | something.com |
      | 13 | another.com   |
      | 14 | sayless.com   |
      | 15 | keepitup.com  |
      | 16 | interest.com  |
      | 17 | bestteam.com  |
    And the database contains the following idea object info:
      | id | title            | purpose      | description | domains | topics | techs | supportingImageUrls | iconUrl | isPaid | isInProgress | isPrivate | user |
      | 18 | Music generation | Open source  | extra info1 | 2       | 6      | 8,9   | 16                  | 17      | false  | false        | false     | 0    |
      | 19 | Techno boom      | Techno music | extra info2 | 2       | 7      | 10    | 12                  | 15      | false  | false        | false     | 0    |
      | 20 | Signal process   | Open source  | extra info3 | 2       | 6      | 9,10  | 15                  | 16      | false  | false        | false     | 1    |
      | 21 | Art blog site    | Open source  | extra info4 | 2       | 7      | 11    | 13                  | 12      | true   | false        | true      | 1    |

    # Normal Flow

    Scenario Outline: Successfully access all created ideas
        Given user with email "<email>" and password "<password>" is successfully logged in
        When the user requests to access all ideas associated to them
        Then then all ideas with ids "<idea_ids>" will be provided

        Examples:
        | email                | password            | idea_ids |
        | user.steve@gmail.com | SteveIsAwesome01    | 18,19    |
        | user.bob@gmail.com   | BobIsAwesome01      | 20,21    |
        | user.joe@gmail.com   | JoeIsAwesome01      |          |

    # Error Flow

    Scenario Outline: Unsuccessfully access created ideas if logged out
        When the user requests to access all ideas associated to them
        Then the status code "<Http_status>" and error "<message>" will be received

        Examples:
          | Http_status | message                    |
          | 401         | User is not authenticated. |
    
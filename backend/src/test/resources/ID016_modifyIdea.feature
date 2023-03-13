#noinspection CucumberUndefinedStep
Feature: Modify Idea
  As a user, I want to be able to modify the details of any idea that I have posted it so that my idea can be changed over time

  Background:
    Given the database contains the following user accounts:
      | id | firstname | lastname | email                | password         |
      | 0  | User      | Steve    | user.steve@gmail.com | SteveIsAwesome01 |
      | 1  | User      | Bob      | user.bob@gmail.com   | BobIsAwesome01   |
    And the database contains the following domain objects:
      | id | name       |
      | 2  | Software   |
      | 3  | Computer   |
      | 4  | Electrical |
    And the database contains the following topic objects:
      | id | name              |
      | 5  | Frontend Dev      |
      | 6  | Backend Dev       |
      | 7  | Embedded Software |
    And the database contains the following tech objects:
      | id | name   |
      | 8  | Python |
      | 9  | Java   |
      | 10 | React  |
      | 11 | C      |
    And the database contains the following URL objects:
      | id | url           |
      | 12 | something.com |
      | 13 | another.com   |
      | 14 | sayless.com   |
      | 15 | keepitup.com  |
      | 16 | interest.com  |
      | 17 | bestteam.com  |
    And the database contains the following idea objects:
      | id | title            | purpose      | description | domains | topics | techs | supportingImageUrls | iconUrl | isPaid | isInProgress | isPrivate | user |
      | 18 | Music generation | Open source  | extra info1 | 2       | 5      | 8,9   | 16                  | 17      | false  | false        | false     | 0    |
      | 19 | Techno boom      | Techno music | extra info2 | 2       | 6      | 10    | 12                  | 15      | false  | false        | false     | 0    |
    And I am successfully logged in as the user with email "user.steve@gmail.com" and password "SteveIsAwesome01"


	# Normal Flow

  Scenario Outline: Successfully modify an idea
    When the user with user id <user_id> requests to modify the field "<field>" to become new value "<new_value>" for idea with id "<id>"
    Then the idea with id "<id>" will have value "<new_value>" for the field "<field>"

    Examples:
      | id | field       | new_value      | user_id |
      | 18 | title       | Health App     | 0       |
      | 18 | purpose     | For customer   | 0       |
      | 19 | description | new extra info | 0       |
      | 18 | isPaid      | True           | 0       |
      | 18 | inProgress  | True           | 0       |
      | 19 | isPrivate   | True           | 0       |
      | 18 | domains     | 3              | 0       |
      | 19 | topics      | 7              | 0       |
      | 18 | techs       | 8              | 0       |
      | 19 | image URLs  | 13             | 0       |
      | 18 | icon URL    | 13             | 0       |

	# Alternate Flow

  Scenario Outline: Successfully modify the idea with an empty field that can be empty
    When the user with user id <user_id> requests to modify the field "<field>" to become empty for idea with id "<id>"
    Then the idea with id "<id>" will have empty for the field "<field>"

    Examples:
      | id | field      | user_id |
      | 18 | domains    | 0       |
      | 18 | topics     | 0       |
      | 19 | techs      | 0       |
      | 19 | image URLs | 0       |

	# Error Flow

  Scenario Outline: Unsuccessfully modify the idea with an empty field that cannot be empty
    When the user with user id <user_id> requests to modify the field "<field>" to become empty for idea with id "<id>"
    Then the idea with id "<id>" will have value "<old_value>" for the field "<field>"
    Then the error message "<error>" will be thrown with status code "<Http_status>"

    Examples:
      | id | field       | old_value        | user_id | error                                 | Http_status |
      | 18 | title       | Music generation | 0       | Necessary fields have been left empty | 400         |
      | 18 | purpose     | Open source      | 0       | Necessary fields have been left empty | 400         |
      | 19 | description | extra info2      | 0       | Necessary fields have been left empty | 400         |
      | 19 | description | extra info2      | 0       | Necessary fields have been left empty | 400         |

	# Error Flow

  Scenario Outline: (Error Flow) Unsuccessfully modify an idea with a non-existing object
    When the user with user id <user_id> requests to modify the field "<field>" to become new value "<new_value>" for idea with id "<id>"
    Then the idea with id "<id>" will have value "<old_value>" for the field "<field>"
    Then the error message "<error>" will be thrown with status code "<Http_status>"

    Examples:
      | id | field      | old_value | new_value | user_id | error                                                                 | Http_status |
      | 18 | domains    | 2         | 15        | 0       | You are attempting to link your idea to an object that does not exist | 400         |
      | 19 | topics     | 6         | 20        | 0       | You are attempting to link your idea to an object that does not exist | 400         |
      | 18 | techs      | 8,9       | 25        | 0       | You are attempting to link your idea to an object that does not exist | 400         |
      | 19 | image URLs | 12        | 1         | 0       | You are attempting to link your idea to an object that does not exist | 400         |
      | 18 | icon URL   | 17        | 2         | 0       | You are attempting to link your idea to an object that does not exist | 400         |

  # Error Flow

  Scenario Outline: (Error Flow) Unsuccessfully modify an idea with user that is not owner of the idea
    When the user with user id <user_id> requests to modify the field "<field>" to become new value "<new_value>" for idea with id "<id>"
    Then the error message "<error>" will be thrown with status code "<Http_status>"
    
    Examples: 
      | id | field       | new_value      | user_id | error                              | Http_status |
      | 18 | title       | Health App     | 1       | You are not the owner of this idea | 400         |
      | 18 | purpose     | For customer   | 1       | You are not the owner of this idea | 400         |


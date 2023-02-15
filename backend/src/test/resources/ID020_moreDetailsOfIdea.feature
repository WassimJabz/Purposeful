#Author: Wassim Jabbour

Feature: View more details for a feature
  As a user, I want to be able to view the details of an idea in a structured manner, so that I can grasp the essence of the idea and make informed decisions about its value and potential impact.

  Background:
    Given the database contains the following domains:
      | id | name     |
      | 1  | Software |

    And the database contains the following topics:
      | id | name    |
      | 1  | Music   |
      | 2  | Biology |
    And the database contains the following techs:
      | id | name    |
      | 1  | PyTorch |
    And the database contains the following URLs:
      | id | url                                                                                 |
      | 1  | https://www.flaticon.com/free-icon/music_3844724                                    |
      | 2  | https://miro.medium.com/v2/resize:fit:4800/format:webp/1*IWBf4ZlgysgEl-AaUJedRQ.png |
    And the database contains the following ideas:
      | id | title            | purpose                                           | domains | topics | techs | supportingImageUrls | iconUrl | isPaid | isInProgress | isPrivate |
      | 1  | Music generation | Open sourced software to generate classical music | 1       | 1      | 1     | 2                   | 1       | false  | false        | false     |
      | 2  | Techno boom      | Open sourced software to generate techno music    | 1       | 1      | 1     |                     | 1       | false  | false        | false     |

  # Main flow: Includes supporting images
  Scenario: View the details of an idea that includes supporting images
    Given I am logged in as a student
    When I request to view the details of idea with id 1
    Then the following information about the idea should be displayed:
      | title        | Music generation                                  |
      | purpose      | Open sourced software to generate classical music |
      | domains      | Software                                          |
      | topics       | Music                                             |
      | techs        | PyTorch                                           |
      | isPaid       | false                                             |
      | isInProgress | false                                             |
      | isPrivate    | false                                             |
    And the supporting image with the following URL should be displayed:
      | url                                                                                 |
      | https://miro.medium.com/v2/resize:fit:4800/format:webp/1*IWBf4ZlgysgEl-AaUJedRQ.png |
    And the icon with the following URL should be displayed:
      | url                                              |
      | https://www.flaticon.com/free-icon/music_3844724 |

  # Alternate flow: No supporting images
  Scenario: View the details of an idea that does not include supporting images
    Given I am logged in as a student
    When I request to view the details of idea with id 2
    Then the following information about the idea should be displayed:
      | title        | Techno boom                                    |
      | purpose      | Open sourced software to generate techno music |
      | domains      | Software                                       |
      | topics       | Music                                          |
      | techs        | PyTorch                                        |
      | isPaid       | false                                          |
      | isInProgress | false                                          |
      | isPrivate    | false                                          |
    And the icon with the following URL should be displayed:
      | url                                              |
      | https://www.flaticon.com/free-icon/music_3844724 |
    But no supporting images should be displayed

  # Error flow
  Scenario Outline: Title of your scenario outline
    Given I am logged in as a student
    When I request to view the details of idea with id 1020
    Then the user shall recieve the error message <error_message> with status <HTTP_status>

    Examples:
      | error_message                     | HTTP_status |
      | The requested idea does not exist | 400         |

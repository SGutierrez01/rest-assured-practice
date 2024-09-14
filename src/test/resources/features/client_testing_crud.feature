@Clients
Feature: Client operations

  @TestCase1
  Scenario: Update the phone number of the first client named Laura
    Given there are at least 10 registered clients and at least one of them is named "Laura"
    When I search for the first client named "Laura"
    Then I save the current phone number
    And I update the phone number of "Laura" to "555-1234"
    Then the response should have a status code of 200
    And the new phone number should be different from the original
    And validates the response with the client JSON schema
    And delete all clients registered in this scenario

  @TestCase3
  Scenario: Update and Delete a new client with name "John"
    Given I will create a new client with the name "John"
    When I retrieve the client
    And I update the client's city to "San Francisco"
    Then the response should have a status code of 200
    And the response structure matches the client JSON schema
    And I delete the client
    Then the client should no longer exist in the system

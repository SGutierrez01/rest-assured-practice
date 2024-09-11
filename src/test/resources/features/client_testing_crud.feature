@active
Feature: Client phone number update

  @smoke
  Scenario: Update the phone number of the first client named Laura
    Given there are at least 10 registered clients
    When I search for the first client named "Laura"
    Then I save the current phone number
    And I update the phone number of "Laura" to "555-1234"
    Then the response should have a status code of 200
    And the new phone number should be different from the original
    And validates the response with the client JSON schema

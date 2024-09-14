  @Resources
  Feature: Retrieve and update active resources

    @TestCase2
    Scenario: Get the list of active resources and update them to inactive
      Given there are at least 5 resources with status "active"
      When I retrieve all active resources
      Then the list of active resources should be retrieved successfully
      And I update all these resources to inactive
      And the response of put request should have a status code of 200
      And the response structure matches the resource schema
      And delete all resources registered in this scenario
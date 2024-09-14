package com.testing.api.stepDefinitions;

import com.testing.api.models.Resource;
import com.testing.api.requests.ResourceRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.List;

public class ResourceSteps {
    private static final Logger logger = LogManager.getLogger(ResourceSteps.class);

    private ResourceRequest resourceRequest = new ResourceRequest();
    private Response response;
    private List<Resource> activeResources;

    @Given("there are at least {int} resources with status {string}")
    public void thereAreAtLeastResourcesWithStatusActive(int numberOfResources, String state) {
        response = resourceRequest.getResources();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Resource> resourceList = resourceRequest.getResourcesEntity(response);
        long matchingResources = resourceList.stream()
                .filter(r -> r.isActive() == state.equalsIgnoreCase("active"))
                .count();

        // If we don't have enough resources with the requested state, create more.
        if (matchingResources < numberOfResources) {
            resourceRequest.createDefaultResources(numberOfResources - (int) matchingResources, state);
        }
    }

    @When("I retrieve all active resources")
    public void iRetrieveAllActiveResources() {
        response = resourceRequest.getResources();
        List<Resource> resources = resourceRequest.getResourcesEntity(response);

        // Filter active resources
        activeResources = resources.stream()
                .filter(Resource::isActive)
                .toList();

        Assert.assertFalse("No active resources found", activeResources.isEmpty());

        for (Resource resource : activeResources) {
            Assert.assertTrue("Resource is not active", resource.isActive());
            logger.info("Active resource: " + resource);
        }

        logger.info("Total active resources found: " + activeResources.size());
    }

    @Then("the list of active resources should be retrieved successfully")
    public void theListOfActiveResourcesShouldBeRetrievedSuccessfully() {
        Assert.assertEquals(200, response.statusCode());

        // Validate schema against resource list
        String path = "schemas/resourceListSchema.json";
        Assert.assertTrue(resourceRequest.validateSchema(response, path));
    }

    @And("I update all these resources to inactive")
    public void iUpdateAllTheseResourcesToInactive() {
        for (Resource resource : activeResources) {
            resource.setActive(false);
            response = resourceRequest.updateResource(resource, resource.getId());
            Assert.assertEquals("Failed to update resource to inactive", 200, response.statusCode());
            logger.info("Updated resource to inactive: " + resource.getId());
        }
    }

    @And("the response of put request should have a status code of {int}")
    public void theResponseOfPutRequestShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    @And("the response structure matches the resource schema")
    public void theResponseStructureMatchesTheResourceSchema() {
        String path = "schemas/resourceSchema.json";
        Assert.assertTrue(resourceRequest.validateSchema(response, path));
        logger.info("Response matches the resource schema");
    }

    @And("delete all resources registered in this scenario")
    public void deleteAllResourcesRegisteredInThisScenario() {
        logger.info("Cleaning up: Deleting all created resources...");

        Response allResourcesResponse = resourceRequest.getResources();
        if (allResourcesResponse.statusCode() == 200) {
            List<Resource> resourcesToDelete = resourceRequest.getResourcesEntity(allResourcesResponse);
            for (Resource resource : resourcesToDelete) {
                response = resourceRequest.deleteResource(resource.getId());
                if (response.statusCode() != 200) {
                    logger.warn("Failed to delete resource: " + resource.getId());
                }
            }
        }else {
            logger.error("Failed to retrieve resources for cleanup.");
        }
    }
}

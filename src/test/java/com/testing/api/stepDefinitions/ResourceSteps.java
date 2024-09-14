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
import java.util.Comparator;

public class ResourceSteps {
    private static final Logger logger = LogManager.getLogger(ResourceSteps.class);

    private final ResourceRequest resourceRequest = new ResourceRequest();
    private Response response;
    private List<Resource> activeResources;
    private Resource latestResource;


    @Given("there are at least {int} resources with status {string}")
    public void thereAreAtLeastResourcesWithStatusActive(int numberOfResources, String state) {
        response = resourceRequest.getResources();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Resource> resourceList = resourceRequest.getResourcesEntity(response);
        long matchingResources = resourceList.stream()
                .filter(r -> r.isActive() == state.equalsIgnoreCase("active"))
                .count();

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

    //Test Case 4 implementation

    @Given("there are at least {int} resources in the system")
    public void thereAreAtLeastResourcesInTheSystem(int numberOfResources) {
        response = resourceRequest.getResources();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Resource> resourceList = resourceRequest.getResourcesEntity(response);

        if (resourceList.size() < numberOfResources) {
            int resourcesToCreate = numberOfResources - resourceList.size();
            logger.info("Creating " + resourcesToCreate + " additional resources to meet the required " + numberOfResources);
            resourceRequest.createDefaultResources(resourcesToCreate, "none");
        }

        logger.info("Resources in the system: " + resourceRequest.getResourcesEntity(response).size());
    }

    @When("I retrieve the latest created resource")
    public void iRetrieveTheLatestCreatedResource() {
        response = resourceRequest.getResources();
        List<Resource> resourceList = resourceRequest.getResourcesEntity(response);

        latestResource = resourceList.stream()
                .max(Comparator.comparing(Resource::getId)) // Change this to creation date if available
                .orElseThrow(() -> new RuntimeException("No resources found"));

        logger.info("Retrieved latest resource: " + latestResource);
    }

    @And("I update all the parameters of this resource")
    public void iUpdateAllTheParametersOfThisResource() {
        latestResource.setName("Updated Name");
        latestResource.setTrademark("Updated Trademark");
        latestResource.setStock(100);
        latestResource.setPrice(99.99);
        latestResource.setDescription("Updated Description");
        latestResource.setTags("Updated,Tags");
        latestResource.setActive(true);  // Example value

        response = resourceRequest.updateResource(latestResource, latestResource.getId());
        logger.info("Updated resource response: " + response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());
    }

    @Then("the response structure matches the resource schema")
    public void theResponseStructureMatchesTheResourceSchema() {
        String schemaPath = "schemas/resourceSchema.json";
        Assert.assertTrue(resourceRequest.validateSchema(response, schemaPath));
        logger.info("Resource response matches the JSON schema");
    }

    @Then("the response body data should reflect the updated values")
    public void theResponseBodyDataShouldReflectTheUpdatedValues() {
        Resource updatedResource = resourceRequest.getResourceEntity(response);

        Assert.assertEquals("Updated Name", updatedResource.getName());
        Assert.assertEquals("Updated Trademark", updatedResource.getTrademark());
        Assert.assertEquals(100, updatedResource.getStock());
        Assert.assertEquals(99.99, updatedResource.getPrice(), 0.01);
        Assert.assertEquals("Updated Description", updatedResource.getDescription());
        Assert.assertEquals("Updated,Tags", updatedResource.getTags());
        Assert.assertTrue(updatedResource.isActive());

        logger.info("Verified all resource updates are correctly reflected in the response");
    }
}

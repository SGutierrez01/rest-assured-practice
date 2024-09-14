package com.testing.api.requests;

import com.testing.api.models.Resource;
import com.testing.api.utils.Constants;
import com.testing.api.utils.JsonFileReader;
import io.restassured.response.Response;

import java.util.List;

public class ResourceRequest extends BaseRequest{
    private String endpoint;

    /**
     * Create resource
     * @param resource model
     * @return rest-assured response
     */
    public Response createResource(Resource resource) {
        endpoint = String.format(Constants.URL, Constants.RESOURCES_PATH);
        return requestPost(endpoint, createBaseHeaders(), resource);
    }

    /**
     * Get the list of all resources
     * @return rest-assured response
     */
    public Response getResources() {
        endpoint = String.format(Constants.BASE_URL + Constants.URL, Constants.RESOURCES_PATH);
        return requestGet(endpoint, createBaseHeaders());
    }

    /**
     * Update resource by id
     * @param resource model
     * @param resourceId string
     * @return rest-assured response
     */
    public Response updateResource(Resource resource, String resourceId) {
        endpoint = String.format(Constants.BASE_URL + Constants.URL_WITH_PARAM, Constants.RESOURCES_PATH, resourceId);
        return requestPut(endpoint, createBaseHeaders(), resource);
    }

    /**
     * Delete resource by id
     * @param resourceId string
     * @return rest-assured response
     */
    public Response deleteResource(String resourceId) {
        String endpoint = String.format(Constants.BASE_URL + Constants.URL_WITH_PARAM, Constants.RESOURCES_PATH, resourceId);
        return requestDelete(endpoint, createBaseHeaders());
    }

    /**
     * Get resource entity from the response
     * @param response rest-assured response
     * @return Resource entity
     */
    public Resource getResourceEntity(Response response) {
        return response.as(Resource.class);
    }

    /**
     * Get resources entity from the response
     * @param response rest-assured response
     * @return List of resources
     */
    public List<Resource> getResourcesEntity(Response response) {
        return response.jsonPath().getList("", Resource.class);
    }

    /**
     * Create default resources
     * @param numberOfResources int
     * @param isActive String
     */
    public void createDefaultResources(int numberOfResources, String isActive) {
        JsonFileReader jsonFileReader = new JsonFileReader();
        List<Resource> resources = jsonFileReader.getResourcesByJson(Constants.DEFAULT_RESOURCES_FILE_PATH, numberOfResources, isActive);

        for (Resource resource : resources) {
            Response response = createResource(resource);
            if (response.statusCode() != 201) {
                System.out.println("Resource creation failed for client: " + resource.getName() + " with status code: " + response.statusCode());
            } else {
                System.out.println("Resource created successfully: " + resource.getName());
            }
        }
    }
}

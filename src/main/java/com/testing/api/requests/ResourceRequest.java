package com.testing.api.requests;

import com.testing.api.utils.Constants;
import io.cucumber.core.resource.Resource;
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
        return response.jsonPath().getList(".", Resource.class);
    }
}

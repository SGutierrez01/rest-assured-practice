package com.testing.api.requests;

import com.testing.api.models.Client;
import com.testing.api.utils.Constants;
import com.testing.api.utils.JsonFileReader;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.util.List;

public class ClientRequest extends BaseRequest {
    private String endpoint;

    /**
     * Create client
     * @param client model
     * @return rest-assured response
     */
    public Response createClient(Client client) {
        endpoint = String.format(Constants.URL, Constants.CLIENTS_PATH);
        return requestPost(endpoint, createBaseHeaders(), client);
    }

    /**
     * Get the list of all clients
     * @return rest-assured response
     */
    public Response getClients() {
        // Use the full URL with the base URL and clients path
        endpoint = String.format(Constants.BASE_URL + Constants.URL, Constants.CLIENTS_PATH);
        return requestGet(endpoint, createBaseHeaders());
    }

    /**
     * Update client by id
     * @param client model
     * @param clientId string
     * @return rest-assured response
     */
    public Response updateClient(Client client, String clientId) {
        // Use the full URL with the base URL and the client ID parameter
        endpoint = String.format(Constants.BASE_URL + Constants.URL_WITH_PARAM, Constants.CLIENTS_PATH, clientId);
        return requestPut(endpoint, createBaseHeaders(), client);
    }

    /**
     * Delete client by id
     * @param clientId string
     * @return rest-assured response
     */
    public Response deleteClient(String clientId) {
        String endpoint = String.format(Constants.BASE_URL + Constants.URL_WITH_PARAM, Constants.CLIENTS_PATH, clientId);
        return requestDelete(endpoint, createBaseHeaders());
    }


    /**
     * Get client entity from the response
     * @param response rest-assured response
     * @return Client entity
     */
    public Client getClientEntity(Response response) {
        return response.as(Client.class);
    }

    /**
     * Get clients entity from the response
     * @param response rest-assured response
     * @return List of clients
     */
    public List<Client> getClientsEntity(Response response) {
        return response.jsonPath().getList("", Client.class);
    }

    /**
     * Create default clients
     * @param numberOfClient amount of clients to create
     * @param requiredName name required to test
     */
    public void createDefaultClients(int numberOfClient, String requiredName) {
        JsonFileReader jsonFileReader = new JsonFileReader();
        List<Client> defaultClients = jsonFileReader.getRandomClientsWithRequiredName(Constants.DEFAULT_CLIENTS_FILE_PATH, numberOfClient, requiredName);

        for (Client client : defaultClients) {
            String endpoint = String.format(Constants.BASE_URL + Constants.URL, Constants.CLIENTS_PATH);
            Response response = requestPost(endpoint, createBaseHeaders(), client);

            if (response.statusCode() != 201) {
                System.out.println("Client creation failed for client: " + client.getName() + " with status code: " + response.statusCode());
            } else {
                System.out.println("Client created successfully: " + client.getName());
            }
        }
    }
}

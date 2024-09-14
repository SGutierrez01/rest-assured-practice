package com.testing.api.stepDefinitions;

import com.testing.api.models.Client;
import com.testing.api.requests.ClientRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClientSteps {
    private static final Logger logger = LogManager.getLogger(ClientSteps.class);

    private final ClientRequest clientRequest = new ClientRequest();
    private Response response;
    private Client client;
    private String originalPhoneNumber;
    private Client selectedClient;
    private String selectedClientId;

    /**
     * This method creates clients if there are less than 10 clients in the system
     * and verifies if the client with the given name is in the system
     *
     * @param numberOfClients int with the number of clients to create
     * @param name            String with the name of the client to verify
     */
    @Given("there are at least {int} registered clients and at least one of them is named {string}")
    public void thereAreRegisteredClientsInTheSystem(int numberOfClients, String name) {
        response = clientRequest.getClients();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Client> clientList = clientRequest.getClientsEntity(response);
        if (clientList.size() < 10) {
            clientRequest.createDefaultClients(numberOfClients - clientList.size(), name);
        } else {
            boolean found = false;
            logger.info("There are already " + clientList.size()
                    + " clients in the system" + "and need to verify if "
                    + name + " is in the system");
            for (int i = 0; i < clientList.size() && !found; i++) {
                if (clientList.get(i).getName().equalsIgnoreCase(name)) {
                    found = true;
                }
            }
            if (!found) {
                clientRequest.createDefaultClients(1, name);
            }
        }
    }

    /**
     * This method retrieves the first client with the given name
     * @param name String with the name of the client to search for
     *             and saves the phone number of the client
     *             to verify if it has changed after updating
     */
    @When("I search for the first client named {string}")
    public void searchForFirstClientNamed(String name) {
        response = clientRequest.getClients();
        List<Client> clients = clientRequest.getClientsEntity(response);

        Optional<Client> lauraClient = clients.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();

        Assert.assertTrue("Client named " + name + " not found", lauraClient.isPresent());

        client = lauraClient.get();
        originalPhoneNumber = client.getPhone();
        logger.info("Found client named " + name + " with phone number: " + originalPhoneNumber);
        if (originalPhoneNumber == null) {
            logger.warn("Original phone number is null for client " + name);
        }
    }

    /**
     * This method updates the phone number of the client
     * @param name String with the name of the client to update
     * @param newPhoneNumber String with the new phone number to update
     */
    @When("I update the phone number of {string} to {string}")
    public void updatePhoneNumber(String name, String newPhoneNumber) {
        logger.info("Attempting to update the phone number of " + name + " to: " + newPhoneNumber);
        if (Objects.equals(client.getPhone(), newPhoneNumber)){
            newPhoneNumber = newPhoneNumber + "1";
        }
        client.setPhone(newPhoneNumber);
        response = clientRequest.updateClient(client, client.getId());
        logger.info("Updated phone number to: " + newPhoneNumber);
        Assert.assertEquals("Failed to update phone number for client: " + name, 200, response.statusCode());
    }

    /**
     * This method saves the current phone number
     */
    @Then("I save the current phone number")
    public void iSaveTheCurrentPhoneNumber() {
        logger.info("Saving the current phone number: " + originalPhoneNumber);
    }

    @Then("the new phone number should be different from the original")
    public void verifyPhoneNumberHasChanged() {
        Client updatedClient = clientRequest.getClientEntity(response);
        Assert.assertNotEquals("The phone number did not change", originalPhoneNumber, updatedClient.getPhone());
        logger.info("Phone number updated successfully: " + updatedClient.getPhone());
    }

    @Then("the response should have a status code of {int}")
    public void theResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    @Then("validates the response with the client JSON schema")
    public void validatesResponseWithClientSchema() {
        logger.info(response.jsonPath().prettify());
        String path = "schemas/clientSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, path));
        logger.info("Successfully validated schema from Client object");
    }


    @And("delete all clients registered in this scenario")
    public void deleteAllClientsRegisteredInThisScenario() {
        logger.info("Cleaning up: Deleting all created clients...");

        Response AllClientsResponse = clientRequest.getClients();
        if (AllClientsResponse.statusCode() == 200) {
            List<Client> clientsToDelete = clientRequest.getClientsEntity(AllClientsResponse);
            for (Client client : clientsToDelete) {
                Response deleteResponse = clientRequest.deleteClient(client.getId());
                if (deleteResponse.statusCode() != 200) {
                    logger.error("Failed to delete client with ID: {}", client.getId());
                }
            }
        } else {
            logger.error("Failed to retrieve clients for cleanup.");
        }
    }


    // Test Case 3 implementation

    @Given("I will create a new client with the name {string}")
    public void iWillCreateANewClientWithName(String name) {
        clientRequest.createDefaultClients(1, name);

        response = clientRequest.getClients();
        Assert.assertEquals(200, response.statusCode());

        selectedClient = clientRequest.getClientsEntity(response).stream()
                .filter(client -> client.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client named " + name + " not found after creation"));

        logger.info("Created client: " + selectedClient);

        selectedClientId = selectedClient.getId();
    }

    @When("I retrieve the client")
    public void iRetrieveTheClient() {
        response = clientRequest.getClient(selectedClientId);
        logger.info("Retrieved client: " + response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());
    }

    @And("I update the client's city to {string}")
    public void iUpdateTheClientCityTo(String newCity) {
        selectedClient.setCity(newCity);
        response = clientRequest.updateClient(selectedClient, selectedClientId);
        logger.info("Updated client response: " + response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());
    }

    @Then("the response structure matches the client JSON schema")
    public void theResponseStructureMatchesTheClientJsonSchema() {
        String schemaPath = "schemas/clientSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, schemaPath));
        logger.info("Client response matches the JSON schema");
    }

    @And("I delete the client")
    public void iDeleteTheClient() {
        response = clientRequest.deleteClient(selectedClientId);
        logger.info("Deleted client response: " + response.statusCode());
        Assert.assertEquals(200, response.statusCode());
    }

    @Then("the client should no longer exist in the system")
    public void theClientShouldNoLongerExistInTheSystem() {
        response = clientRequest.getClient(selectedClientId);
        Assert.assertEquals(500, response.statusCode());
        logger.info("Verified client no longer exists: " + response.statusCode());
    }

}

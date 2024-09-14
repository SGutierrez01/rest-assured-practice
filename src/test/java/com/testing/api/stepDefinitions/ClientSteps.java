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

    @Given("there are at least {int} registered clients and at least one of them is named {string}")
    public void thereAreRegisteredClientsInTheSystem(int numberOfClients, String name) {
        response = clientRequest.getClients();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Client> clientList = clientRequest.getClientsEntity(response);
        if (clientList.size() < 10) {
            clientRequest.createDefaultClients(numberOfClients-clientList.size(), name);
        }
    }

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
}

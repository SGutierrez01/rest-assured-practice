package com.testing.api.stepDefinitions;

import com.testing.api.models.Client;
import com.testing.api.requests.ClientRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.List;
import java.util.Optional;

public class ClientSteps {
    private static final Logger logger = LogManager.getLogger(ClientSteps.class);

    private final ClientRequest clientRequest = new ClientRequest();
    private Response response;
    private Client client;
    private String originalPhoneNumber;

    @Given("there are at least 10 registered clients")
    public void thereAreRegisteredClientsInTheSystem() {
        response = clientRequest.getClients();
        logger.info(response.jsonPath().prettify());
        Assert.assertEquals(200, response.statusCode());

        List<Client> clientList = clientRequest.getClientsEntity(response);
        if (clientList.size() < 10) {
            clientRequest.createDefaultClients();
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
        originalPhoneNumber = client.getPhoneNumber();
        logger.info("Found client named " + name + " with phone number: " + originalPhoneNumber);
        if (originalPhoneNumber == null) {
            logger.warn("Original phone number is null for client " + name);
        }
    }

    @When("I update the phone number of {string} to {string}")
    public void updatePhoneNumber(String name, String newPhoneNumber) {
        logger.info("Attempting to update the phone number of " + name + " to: " + newPhoneNumber);
        client.setPhoneNumber(newPhoneNumber);
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
        Assert.assertNotEquals("The phone number did not change", originalPhoneNumber, updatedClient.getPhoneNumber());
        logger.info("Phone number updated successfully: " + updatedClient.getPhoneNumber());
    }

    @Then("the response should have a status code of {int}")
    public void theResponseShouldHaveAStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }

    @Then("validates the response with the client JSON schema")
    public void validatesResponseWithClientSchema() {
        String path = "schemas/clientSchema.json";
        Assert.assertTrue(clientRequest.validateSchema(response, path));
        logger.info("Successfully validated schema from Client object");
    }


}

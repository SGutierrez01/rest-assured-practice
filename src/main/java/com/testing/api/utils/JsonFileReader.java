package com.testing.api.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.testing.api.models.Client;
import com.testing.api.models.Resource;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class JsonFileReader {

    /**
     * Reads a JSON file and deserializes it into a list of Client objects, randomly selecting a specified number of clients,
     * ensuring at least one of them has the specified name or randomly replacing one's name if not found.
     *
     * @param jsonFileName The file path of the JSON file.
     * @param numberOfClients The number of clients to retrieve, must not exceed the number of available clients in the file.
     * @param requiredName The name that at least one of the clients must have.
     * @return List of randomly selected clients with the conditions applied.
     */
    public List<Client> getRandomClientsWithRequiredName(String jsonFileName, int numberOfClients, String requiredName) {
        Gson gson = new Gson();
        List<Client> clients;

        try (FileReader reader = new FileReader(jsonFileName)) {
            clients = gson.fromJson(reader, new TypeToken<List<Client>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (numberOfClients > clients.size()) {
            throw new IllegalArgumentException("Number of requested clients exceeds available clients.");
        }

        Collections.shuffle(clients);
        List<Client> selectedClients = clients.subList(0, numberOfClients);
        boolean nameFound = selectedClients.stream().anyMatch(client -> client.getName().equalsIgnoreCase(requiredName));

        if (!nameFound) {
            Random rand = new Random();
            Client randomClient = selectedClients.get(rand.nextInt(selectedClients.size()));
            randomClient.setName(requiredName);
        }
        return selectedClients;
    }

    /**
     * Reads a JSON file and deserializes it into a list of Resource objects, optionally filtering by active status.
     * Returns a limited number of resources based on the provided parameter.
     *
     * @param jsonFileName The file path of the JSON file.
     * @param isActive Optional filter for active status: can be "true" for active, "false" for inactive, or "any" to ignore this filter.
     * @param numberOfResources The number of resources to retrieve, should not exceed the available number of resources in the file.
     * @return List of filtered Resource objects based on the active status and limited to the specified number of resources.
     */
    public List<Resource> getResourcesByJson(String jsonFileName, int numberOfResources, String isActive) {
        Gson gson = new Gson();
        List<Resource> resources;

        try (FileReader reader = new FileReader(jsonFileName)) {
            resources = gson.fromJson(reader, new TypeToken<List<Resource>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (isActive.equals("true") || isActive.equals("false")) {
            boolean activeStatus = Boolean.parseBoolean(isActive);
            resources = resources.stream()
                    .filter(resource -> resource.isActive() == activeStatus)
                    .collect(Collectors.toList());
        }

        if (numberOfResources > resources.size()) {
            throw new IllegalArgumentException("Requested number of resources exceeds the available resources in the file.");
        }

        return resources.stream().limit(numberOfResources).collect(Collectors.toList());
    }
}

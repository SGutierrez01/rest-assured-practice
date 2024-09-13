package com.testing.api.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.testing.api.models.Client;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class JsonFileReader {

    /**
     * This method reads a JSON file and deserializes the body into a list of Client objects
     *
     * @param jsonFileName json file location path
     * @return List<Client> : list of clients
     */
    public List<Client> getAllClientsByJson(String jsonFileName) {
        List<Client> clients = null;
        try (Reader reader = new FileReader(jsonFileName)) {
            Gson gson = new Gson();
            clients = gson.fromJson(reader, new TypeToken<List<Client>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clients;
    }

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

}

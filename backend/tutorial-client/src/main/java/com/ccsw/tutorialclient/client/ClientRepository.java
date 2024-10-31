package com.ccsw.tutorialclient.client;

import org.springframework.data.repository.CrudRepository;

import com.ccsw.tutorialclient.client.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByName(String name);
}
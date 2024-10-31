package com.ccsw.tutorialclient.client;

import java.util.List;

import com.ccsw.tutorialclient.client.model.Client;
import com.ccsw.tutorialclient.client.model.ClientDto;

public interface ClientService {

    List<Client> findAll();

    Client get(Long id);

    void save(Long id, ClientDto dto) throws Exception;

    void delete(Long id) throws Exception;

    boolean existsByName(String name);
}

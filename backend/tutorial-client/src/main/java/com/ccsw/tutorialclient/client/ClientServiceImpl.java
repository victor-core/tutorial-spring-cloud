package com.ccsw.tutorialclient.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ccsw.tutorialclient.client.model.Client;
import com.ccsw.tutorialclient.client.model.ClientDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Client> findAll() {

        return (List<Client>) this.clientRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, ClientDto dto) {

        Client existingClient = clientRepository.findByName(dto.getName());

        if (existingClient != null && (id == null || !existingClient.getId().equals(id))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre del cliente ya existe");
        }

        Client client;

        if (id == null) {
            client = new Client();
        } else {
            client = this.get(id);
            if (client == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
            }
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {
        Client client = this.get(id);
        if (client == null)
            throw new Exception("Client not found");

        this.clientRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return this.clientRepository.findByName(name) != null;
    }
}

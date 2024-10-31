package com.ccsw.tutorialloan.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.ccsw.tutorialloan.client.model.ClientDto;

@FeignClient(value = "SPRING-CLOUD-EUREKA-CLIENT-CLIENT", url = "http://localhost:8080")
public interface ClientClient {

    @GetMapping(value = "/client")
    List<ClientDto> findAll();
}

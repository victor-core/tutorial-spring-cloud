package com.ccsw.tutorialloan.category;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.ccsw.tutorialloan.category.model.CategoryDto;

@FeignClient(value = "SPRING-CLOUD-EUREKA-CLIENT-CATEGORY", url = "http://localhost:8080")
public interface CategoryClient {

    @GetMapping(value = "/category")
    List<CategoryDto> findAll();
}

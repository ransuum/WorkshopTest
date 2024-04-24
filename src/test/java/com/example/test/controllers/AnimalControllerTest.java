package com.example.test.controllers;


import com.example.test.entity.Animal;
import com.example.test.enums.Category;
import com.example.test.service.FileUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AnimalController.class)
class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "animals.csv", "text/csv", "Name,Type,Sex,Weight,Cost\nFido,Dog,Male,25,30".getBytes());
        when(fileUploadService.uploadFile(any(MultipartFile.class))).thenReturn("File uploaded and processed successfully");

        mockMvc.perform(multipart("/animals/files/uploads")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded and processed successfully"));
    }

    @Test
    void testGetAnimals() throws Exception {
        List<Animal> animals = Arrays.asList(
                Animal.builder().name("Fido").type("Dog").sex("Male").weight(25).cost(30).category(Category.CATEGORY_2).build(),
                Animal.builder().name("Whiskers").type("Cat").sex("Female").weight(5).cost(15).category(Category.CATEGORY_1).build()
        );
        when(fileUploadService.getAnimalBy(null, null, null, null)).thenReturn(animals);

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Fido")))
                .andExpect(jsonPath("$[0].type", is("Dog")))
                .andExpect(jsonPath("$[0].sex", is("Male")))
                .andExpect(jsonPath("$[0].weight", is(25)))
                .andExpect(jsonPath("$[0].cost", is(30)))
                .andExpect(jsonPath("$[0].category", is("CATEGORY_2")))
                .andExpect(jsonPath("$[1].name", is("Whiskers")))
                .andExpect(jsonPath("$[1].type", is("Cat")))
                .andExpect(jsonPath("$[1].sex", is("Female")))
                .andExpect(jsonPath("$[1].weight", is(5)))
                .andExpect(jsonPath("$[1].cost", is(15)))
                .andExpect(jsonPath("$[1].category", is("CATEGORY_1")));
    }
    @Test
    void testGetAnimalsByType() throws Exception {
        List<Animal> dogs = Arrays.asList(
                Animal.builder().name("Fido").type("Dog").sex("Male").weight(25).cost(30).category(Category.CATEGORY_2).build(),
                Animal.builder().name("Buddy").type("Dog").sex("Male").weight(30).cost(40).category(Category.CATEGORY_2).build()
        );
        when(fileUploadService.getAnimalBy("Dog", null, null, null)).thenReturn(dogs);

        mockMvc.perform(get("/animals?type=Dog")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Fido")))
                .andExpect(jsonPath("$[0].type", is("Dog")))
                .andExpect(jsonPath("$[1].name", is("Buddy")))
                .andExpect(jsonPath("$[1].type", is("Dog")));
    }
}
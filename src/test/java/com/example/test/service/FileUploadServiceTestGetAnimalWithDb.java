package com.example.test.service;

import com.example.test.entity.Animal;
import com.example.test.repo.AnimalRepo;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
class FileUploadServiceTestGetAnimalWithDb {
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private AnimalRepo animalRepo;

    @BeforeEach
    void setUp() throws JAXBException {
        fileUploadService = new FileUploadService(animalRepo);
        byte[] csvFileData = "Name,Type,Sex,Weight,Cost\nFido,Dog,Male,25,30\nWhiskers,Cat,Female,5,15".getBytes(StandardCharsets.UTF_8);
        byte[] xmlFileData = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<animals>" +
                "<animal>" +
                "<name>Buddy</name>" +
                "<type>Dog</type>" +
                "<sex>Male</sex>" +
                "<weight>30</weight>" +
                "<cost>40</cost>" +
                "</animal>" +
                "<animal>" +
                "<name>Mittens</name>" +
                "<type>Cat</type>" +
                "<sex>Female</sex>" +
                "<weight>3</weight>" +
                "<cost>10</cost>" +
                "</animal>" +
                "</animals>").getBytes(StandardCharsets.UTF_8);
        animalRepo.saveAll(fileUploadService.processCSVFile(csvFileData));
        animalRepo.saveAll(fileUploadService.processXMLFile(xmlFileData));
    }

    @Test
    void testGetAnimalsByType() {
        List<Animal> dogs = fileUploadService.getAnimalBy("Dog", null, null, null);
        assertEquals(2, dogs.size());
        animalRepo.deleteAll();
    }

    @Test
    void testGetAnimalsBySex() {
        List<Animal> females = fileUploadService.getAnimalBy(null, null, "Female", null);
        assertEquals(2, females.size());
        animalRepo.deleteAll();
    }

    @Test
    void testGetAnimalsByCategory() {
        List<Animal> category2Animals = fileUploadService.getAnimalBy(null, "CATEGORY_2", null, null);
        assertEquals(2, category2Animals.size());
        animalRepo.deleteAll();
    }

    @Test
    void testSortAnimalsByType() {
        List<Animal> sortedAnimals = fileUploadService.getAnimalBy(null, null, null, "type");
        assertEquals("Cat", sortedAnimals.get(0).getType());
        assertEquals("Cat", sortedAnimals.get(1).getType());
        assertEquals("Dog", sortedAnimals.get(2).getType());
        assertEquals("Dog", sortedAnimals.get(3).getType());
        animalRepo.deleteAll();
    }
}
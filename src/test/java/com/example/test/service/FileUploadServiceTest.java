package com.example.test.service;

import com.example.test.entity.Animal;
import com.example.test.enums.Category;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {
    @Mock
    private MultipartFile csvFile;
    @Mock
    private MultipartFile xmlFile;

    private final FileUploadService fileUploadService = new FileUploadService();
    @Test
    void testUploadCSVFile() throws IOException {
        when(csvFile.getOriginalFilename()).thenReturn("animals.csv");
        when(csvFile.getBytes()).thenReturn("Name,Type,Sex,Weight,Cost\nFido,Dog,Male,25,30\nWhiskers,Cat,Female,5,15".getBytes(StandardCharsets.UTF_8));

        String result = fileUploadService.uploadFile(csvFile);
        assertEquals("File uploaded and processed successfully", result);
    }
    @Test
    void testUploadXMLFile() throws IOException {
        when(xmlFile.getOriginalFilename()).thenReturn("animals.xml");
        when(xmlFile.getBytes()).thenReturn(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
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
                "</animals>").getBytes(StandardCharsets.UTF_8));

        String result = fileUploadService.uploadFile(xmlFile);
        assertEquals("File uploaded and processed successfully", result);
    }
    @Test
    void testProcessCSVFile() {
        byte[] fileData = "Name,Type,Sex,Weight,Cost\nFido,Dog,Male,25,30\nWhiskers,Cat,Female,5,15".getBytes(StandardCharsets.UTF_8);
        List<Animal> animals = fileUploadService.processCSVFile(fileData);

        assertEquals(2, animals.size());
        assertEquals("Fido", animals.get(0).getName());
        assertEquals("Dog", animals.get(0).getType());
        assertEquals("Male", animals.get(0).getSex());
        assertEquals(25, animals.get(0).getWeight());
        assertEquals(30, animals.get(0).getCost());
        assertEquals(Category.CATEGORY_2, animals.get(0).getCategory());
    }
    @Test
    void testProcessXMLFile() throws JAXBException {
        byte[] fileData = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><animals>" +
                "<animal>" +
                "<name>Buddy</name>" +
                "<type>Dog</type>" +
                "<sex>Male</sex>" +
                "<weight>30</weight>" +
                "<cost>40</cost></animal>" +
                "<animal>" +
                "<name>Mittens</name>" +
                "<type>Cat</type>" +
                "<sex>Female</sex>" +
                "<weight>3</weight>" +
                "<cost>10</cost>" +
                "</animal>" +
                "</animals>").getBytes(StandardCharsets.UTF_8);
        List<Animal> animals = fileUploadService.processXMLFile(fileData);

        assertEquals(2, animals.size());
        assertEquals("Buddy", animals.get(0).getName());
        assertEquals("Dog", animals.get(0).getType());
        assertEquals("Male", animals.get(0).getSex());
        assertEquals(30, animals.get(0).getWeight());
        assertEquals(40, animals.get(0).getCost());
        assertEquals(Category.CATEGORY_2, animals.get(0).getCategory());
    }
    @Test
    void testProcessXMLFileOnNull() throws JAXBException {
        byte[] fileData = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><animals>" +
                "<animal>" +
                "<name>Buddy</name>" +
                "<type>Dog</type>" +
                "<sex>Male</sex>" +
                "<cost>40</cost></animal>" +
                "<animal>" +
                "<name>Mittens</name>" +
                "<type>Cat</type>" +
                "<sex>Female</sex>" +
                "<cost>10</cost>" +
                "</animal>" +
                "</animals>").getBytes(StandardCharsets.UTF_8);
        List<Animal> animals = fileUploadService.processXMLFile(fileData);
        assertEquals(0, animals.size());
    }
}
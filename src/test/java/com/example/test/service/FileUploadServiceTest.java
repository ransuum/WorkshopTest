package com.example.test.service;

import com.example.test.entity.Animal;
import com.example.test.enums.Category;
import com.example.test.repo.AnimalRepo;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class FileUploadServiceTest {
    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private AnimalRepo animalRepo;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadFile() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "hello.csv", "text/csv", "Name,Type,Sex,Weight,Cost\nFido,Dog,Male,25,30\nWhiskers,Cat,Female,5,15".getBytes());
        when(animalRepo.saveAll(fileUploadService.processCSVFile(file.getBytes()))).thenReturn(Arrays.asList(new Animal(), new Animal()));
        String result = fileUploadService.uploadFile(file);
        assertEquals("File uploaded and processed successfully", result);
    }

    @Test
    public void testGetAnimalsByType() {
        when(animalRepo.findAllByType("Dog")).thenReturn(Arrays.asList(new Animal(), new Animal()));
        List<Animal> result = fileUploadService.getAnimalBy("Dog", null, null, null);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAnimalsBySex() {
        when(animalRepo.findAllBySex("Female")).thenReturn(Arrays.asList(new Animal(), new Animal()));
        List<Animal> result = fileUploadService.getAnimalBy(null, null, "Female", null);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAnimalsByCategory() {
        when(animalRepo.findAllByCategory(Category.CATEGORY_2)).thenReturn(Arrays.asList(new Animal(), new Animal()));
        List<Animal> result = fileUploadService.getAnimalBy(null, "CATEGORY_2", null, null);
        assertEquals(2, result.size());
    }

    @Test
    public void testSortAnimalsByType() {
        when(animalRepo.findAll()).thenReturn(Arrays.asList(
                new Animal(UUID.randomUUID(),"Duke", "Dog", "Male", 30, 40, Category.CATEGORY_2),
                new Animal(UUID.randomUUID(),"Liza","Cat", "Female", 3, 10, Category.CATEGORY_1),
                new Animal(UUID.randomUUID(),"Rame","Dog", "Male", 25, 30, Category.CATEGORY_2),
                new Animal(UUID.randomUUID(),"Ron","Cat", "Female", 5, 15, Category.CATEGORY_1)
        ));
        List<Animal> result = fileUploadService.getAnimalBy(null, null, null, "type");
        assertEquals("Cat", result.get(0).getType());
        assertEquals("Cat", result.get(1).getType());
        assertEquals("Dog", result.get(2).getType());
        assertEquals("Dog", result.get(3).getType());
    }
}
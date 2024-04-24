package com.example.test.service;

import com.example.test.entity.Animal;
import com.example.test.entity.Animals;
import com.example.test.enums.Category;
import com.example.test.exception.ValueNotCorrectException;
import com.example.test.repo.AnimalRepo;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.Data;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileUploadService {

    private AnimalRepo animalRepo;
    @Autowired
    public FileUploadService(AnimalRepo animalRepo) {
        this.animalRepo = animalRepo;
    }

    public String uploadFile(MultipartFile file) {
        try {
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
                animalRepo.saveAll(processCSVFile(file.getBytes()));
            } else if (file.getOriginalFilename().endsWith(".xml")) {
                animalRepo.saveAll(processXMLFile(file.getBytes()));
            } else {
                return "Unsupported file format";
            }
            return "File uploaded and processed successfully";
        } catch (Exception e) {
            return "Error processing the file: " + e.getMessage();
        }
    }

    public List<Animal> processXMLFile(byte[] fileData) throws JAXBException {
        List<Animal> animals = new ArrayList<>();

        JAXBContext jaxbContext = JAXBContext.newInstance(Animals.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Animals animalsXML = (Animals) unmarshaller.unmarshal(new ByteArrayInputStream(fileData));

        for (Animal animal : animalsXML.getAnimalList()) {
            if (isValidAnimal(animal)) {
                animal.setCategory(checkCostForCategory(animal.getCost()));
                animals.add(animal);
            }
        }
        return animals;
    }

    private boolean isValidAnimal(Animal animal) {
        return animal.getName() != null && !animal.getName().isBlank() &&
                animal.getType() != null && !animal.getType().isBlank() &&
                animal.getSex() != null && !animal.getSex().isBlank() &&
                animal.getWeight() != null &&
                animal.getCost() != null;
    }

    public List<Animal> processCSVFile(byte[] fileData) {
        List<Animal> animals = new ArrayList<>();

        try (InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(fileData), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(inputStreamReader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
            for (CSVRecord csvRecord : csvParser) {
                if (!csvRecord.get("Name").trim().isEmpty() && !csvRecord.get("Type").trim().isEmpty()
                        && !csvRecord.get("Sex").trim().isEmpty() && !csvRecord.get("Weight").trim().isEmpty() && !csvRecord.get("Cost").trim().isEmpty()) {
                    String name = csvRecord.get("Name");
                    String type = csvRecord.get("Type");
                    String sex = csvRecord.get("Sex");
                    Integer weight = Integer.parseInt(csvRecord.get("Weight"));
                    Integer cost = Integer.parseInt(csvRecord.get("Cost"));

                    Animal animal = Animal.builder()
                            .name(name)
                            .sex(sex)
                            .type(type)
                            .weight(weight)
                            .cost(cost)
                            .category(checkCostForCategory(cost))
                            .build();
                    animals.add(animal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animals;
    }

    public List<Animal> getAnimalBy(String type, String category, String sex, String sortBy) {
        List<Animal> newAnimal = new ArrayList<>();
        if (type != null) {
            if (sortBy != null) {
                return sort(sortBy, animalRepo.findAllByType(type).stream());
            } else {
                return animalRepo.findAllByType(type);
            }
        } else if (sex != null) {
            if (sortBy != null) {
                return sort(sortBy, animalRepo.findAllBySex(sex).stream());
            } else {
                return animalRepo.findAllBySex(sex);
            }
        } else if (category != null) {
            Category category1 = Category.valueOf(category.toUpperCase());
            if (sortBy != null) {
                return sort(sortBy, animalRepo.findAllByCategory(category1).stream());
            } else {
                return animalRepo.findAllByCategory(category1);
            }
        } else if (sortBy != null) {
            return sort(sortBy, animalRepo.findAll().stream());
        } else {
            throw new ValueNotCorrectException("Incorrect value for sortBy parameter");
        }
    }

    private List<Animal> sort(String sortBy, Stream<Animal> stream) {
        switch (sortBy.toLowerCase()) {
            case "type":
                stream = stream.sorted(Comparator.comparing(Animal::getType));
                break;
            case "category":
                stream = stream.sorted(Comparator.comparing(animal -> animal.getCategory().name()));
                break;
            case "sex":
                stream = stream.sorted(Comparator.comparing(Animal::getSex));
                break;
            case "cost":
                stream = stream.sorted(Comparator.comparing(Animal::getCost));
                break;
            case "weight":
                stream = stream.sorted(Comparator.comparing(Animal::getWeight));
                break;
            default:
                throw new ValueNotCorrectException("not correct value");
        }
        return stream.toList();
    }

    private Category checkCostForCategory(Integer cost) {
        if (cost <= 20) {
            return Category.CATEGORY_1;
        } else if (cost <= 40) {
            return Category.CATEGORY_2;
        } else if (cost <= 60) {
            return Category.CATEGORY_3;
        } else {
            return Category.CATEGORY_4;
        }
    }
}

package com.example.test.repo;

import com.example.test.entity.Animal;
import com.example.test.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnimalRepo extends JpaRepository<Animal, UUID> {
    List<Animal> findAllBySex(String sex);
    List<Animal> findAllByType(String type);
    List<Animal> findAllByCategory(Category category);
}

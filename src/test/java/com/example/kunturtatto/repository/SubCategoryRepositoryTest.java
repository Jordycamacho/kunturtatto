package com.example.kunturtatto.repository;

import com.example.kunturtatto.model.Category;
import com.example.kunturtatto.model.SubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SubCategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    private Category testCategory;
    private SubCategory testSubCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Test Category")
                .image("category.jpg")
                .build();
        
        entityManager.persist(testCategory);
        entityManager.flush();

        testSubCategory = SubCategory.builder()
                .name("Test SubCategory")
                .image("subcategory.jpg")
                .category(testCategory)
                .build();
        
        entityManager.persist(testSubCategory);
        entityManager.flush();
    }

    @Test
    @DisplayName("Buscar subcategorías por ID de categoría - Debe retornar lista")
    void findByCategoryId_ShouldReturnList() {
        List<SubCategory> result = subCategoryRepository.findByCategoryId(testCategory.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test SubCategory", result.get(0).getName());
    }

    @Test
    @DisplayName("Buscar subcategorías por ID de categoría inexistente - Debe retornar lista vacía")
    void findByCategoryId_NonExistentCategory_ShouldReturnEmptyList() {
        List<SubCategory> result = subCategoryRepository.findByCategoryId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Guardar subcategoría - Debe persistir correctamente")
    void saveSubCategory_ShouldPersist() {
        SubCategory newSubCategory = SubCategory.builder()
                .name("New SubCategory")
                .image("new.jpg")
                .category(testCategory)
                .build();

        SubCategory saved = subCategoryRepository.save(newSubCategory);
        entityManager.flush();
        entityManager.clear();

        SubCategory found = entityManager.find(SubCategory.class, saved.getId());
        assertNotNull(found);
        assertEquals("New SubCategory", found.getName());
        assertEquals(testCategory.getId(), found.getCategory().getId());
    }

    @Test
    @DisplayName("Eliminar subcategoría - Debe eliminar correctamente")
    void deleteSubCategory_ShouldRemove() {
        subCategoryRepository.delete(testSubCategory);
        entityManager.flush();
        entityManager.clear();

        SubCategory found = entityManager.find(SubCategory.class, testSubCategory.getId());
        assertNull(found);
    }

    @Test
    @DisplayName("Buscar todas las subcategorías - Debe retornar todas")
    void findAll_ShouldReturnAll() {
        List<SubCategory> result = subCategoryRepository.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(testSubCategory));
    }

    @Test
    @DisplayName("Buscar por ID - Debe retornar subcategoría")
    void findById_ShouldReturnSubCategory() {
        Optional<SubCategory> result = subCategoryRepository.findById(testSubCategory.getId());

        assertTrue(result.isPresent());
        assertEquals("Test SubCategory", result.get().getName());
    }

    @Test
    @DisplayName("Buscar por ID inexistente - Debe retornar vacío")
    void findById_NonExistent_ShouldReturnEmpty() {
        Optional<SubCategory> result = subCategoryRepository.findById(999L);

        assertFalse(result.isPresent());
    }
}
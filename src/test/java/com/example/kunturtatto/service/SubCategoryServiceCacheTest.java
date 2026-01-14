package com.example.kunturtatto.service;

import com.example.kunturtatto.dto.SubCategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SubCategoryServiceCacheTest {

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Cache debería funcionar para getAllSubCategories")
    void getAllSubCategories_ShouldUseCache() {
        List<SubCategoryDto> result1 = subCategoryService.getAllSubCategories();
    
        List<SubCategoryDto> result2 = subCategoryService.getAllSubCategories();
        
        assertEquals(result1.size(), result2.size());
        
        var cache = cacheManager.getCache("subcategoriesAll");
        assertNotNull(cache);
        assertNotNull(cache.get("all"));
    }
}
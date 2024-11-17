package com.example.kunturtatto.controller;

import com.example.kunturtatto.config.TestSecurityConfig;
import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserViewController.class)
@Import(TestSecurityConfig.class)
public class UserViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoryDesignService categoryDesignService;

    @MockBean
    private IDesignService designService;

    private List<CategoryDesign> categoryDesignList;
    private List<Design> designList;

    @BeforeEach
    public void setup() {
        categoryDesignList = Arrays.asList(
            new CategoryDesign(1L, "Category1", "image1.jpg"),
            new CategoryDesign(2L, "Category2", "image2.jpg")
        );

        designList = Arrays.asList(
            new Design("Design1", "Description1", "image1.jpg", categoryDesignList.get(0)),
            new Design("Design2", "Description2", "image2.jpg", categoryDesignList.get(1))
        );

        when(categoryDesignService.findAll()).thenReturn(categoryDesignList);
        when(designService.findAll()).thenReturn(designList);
        when(designService.findByCategoryDesign(1L)).thenReturn(Arrays.asList(designList.get(0)));
    }

    @Test
    public void testShowHomePage() throws Exception {
        mockMvc.perform(get("/KunturTattoo"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/index"))
            .andExpect(model().attributeExists("categories"))
            .andExpect(model().attribute("categories", categoryDesignList));
    }

    @Test
    public void testShowDesignsWithoutCategory() throws Exception {
        mockMvc.perform(get("/KunturTattoo/diseños"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/designs"))
            .andExpect(model().attributeExists("designs"))
            .andExpect(model().attributeExists("categories"))
            .andExpect(model().attribute("designs", designList))
            .andExpect(model().attribute("categories", categoryDesignList))
            .andExpect(model().attribute("selectedCategoryId", (Object) null));
    }

    @Test
    public void testShowDesignsWithCategory() throws Exception {
        mockMvc.perform(get("/KunturTattoo/diseños").param("categoryId", "1"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/designs"))
            .andExpect(model().attributeExists("designs"))
            .andExpect(model().attributeExists("categories"))
            .andExpect(model().attribute("designs", Arrays.asList(designList.get(0))))
            .andExpect(model().attribute("categories", categoryDesignList))
            .andExpect(model().attribute("selectedCategoryId", 1L));
    }

    @Test
    public void testShowContactPage() throws Exception {
        mockMvc.perform(get("/KunturTattoo/contacto"))
            .andExpect(status().isOk())
            .andExpect(view().name("user/contact"))
            .andExpect(model().attributeExists("categories"))
            .andExpect(model().attribute("categories", categoryDesignList));
    }
}

package com.example.kunturtatto.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/* 
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.RoleEnum;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUserService;
*/
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    /* 
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private ICategoryDesignService categoryDesignService;

    @MockBean
    private IDesignService designService;
    
    private List<Design> designs;
    private List<CategoryDesign> categoryDesigns;
    private List<User> users;

    @BeforeEach
    public void SetUp(){
        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setIdCategoryDesign(1L);
        categoryDesign.setNameCategoryDesign("Tattoos");
        categoryDesign.setImage("test-image.png");

        Design design1 = new Design();
        design1.setIdDesign(1L);
        design1.setTitle("Design 1");
        design1.setDescription("Description 1");
        design1.setCategoryDesign(categoryDesign);
        design1.setImage("image1.jpg");

        Design design2 = new Design();
        design2.setIdDesign(2L);
        design2.setTitle("Design 2");
        design2.setDescription("Description 2");
        design2.setCategoryDesign(categoryDesign);
        design2.setImage("image2.jpg");

        // Crear roles utilizando RoleEnum
        Role roleUser = new Role();
        roleUser.setIdRol(1L);
        roleUser.setRoleEnum(RoleEnum.USER);

        Role roleAdmin = new Role();
        roleAdmin.setIdRol(2L);
        roleAdmin.setRoleEnum(RoleEnum.ADMIN);

        // Crear usuarios de prueba
        User user1 = new User();
        user1.setIdUser(1L);
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setEnabled(true);
        user1.setAccountNoExpired(true);
        user1.setAccountNoLocked(true);
        user1.setCredentialNoExpired(true);
        user1.setRoles(new HashSet<>(Arrays.asList(roleUser)));

        User user2 = new User();
        user2.setIdUser(2L);
        user2.setEmail("admin@example.com");
        user2.setPassword("password2");
        user2.setEnabled(true);
        user2.setAccountNoExpired(true);
        user2.setAccountNoLocked(true);
        user2.setCredentialNoExpired(true);
        user2.setRoles(new HashSet<>(Arrays.asList(roleAdmin)));

        categoryDesigns = Arrays.asList(categoryDesign);
        designs = Arrays.asList(design1,design2);
        users = Arrays.asList(user1,user2);

        when(userService.findAll()).thenReturn(users);
        when(designService.findAll()).thenReturn(designs);
        when(categoryDesignService.findAll()).thenReturn(categoryDesigns);
    }

    @Test
    public void testHome() throws Exception  {
        mockMvc.perform(get("/KunturTattoo"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/index"))
                .andExpect(model().attribute("categories", categoryDesigns));
    } 

    @Test
    public void testShowDesigns() throws Exception{
        mockMvc.perform(get("/KunturTattoo/diseños"))
        .andExpect(status().isOk())
        .andExpect(view().name("user/designs")) 
        .andExpect(model().attribute("designs", designs)) 
        .andExpect(model().attribute("categories", categoryDesigns)); 
    }

    @Test 
    public void testContact() throws Exception{
        mockMvc.perform(get("/KunturTattoo/contacto"))
        .andExpect(status().isOk())
        .andExpect(view().name("user/contact"))
        .andExpect(model().attribute("categories", categoryDesigns));
    }

    */
}

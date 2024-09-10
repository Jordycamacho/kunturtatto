package com.example.kunturtatto.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUploadFileService;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDesignService designService;

    @MockBean
    private ICategoryDesignService categoryDesignService;

    @MockBean
    private IUploadFileService uploadFileService;

    private List<Design> designs;
    private List<CategoryDesign> categoriesDesign;

    @BeforeEach
    public void setUp() {
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

        designs = Arrays.asList(design1, design2);
        categoriesDesign = Arrays.asList(categoryDesign);

        when(designService.findAll()).thenReturn(designs);
        when(categoryDesignService.findAll()).thenReturn(categoriesDesign);
    }

    /*Test Design */
    @Test
    public void testShowDesign() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/showDesign"))
                .andExpect(model().attribute("designs", designs))
                .andExpect(model().attribute("categories", categoriesDesign));
                
    }

    @Test
    public void testCreateDesign() throws Exception {
        mockMvc.perform(get("/admin/diseños/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/design/createDesign"))
                .andExpect(model().attribute("design", designs))
                .andExpect(model().attribute("categoriesDesign", categoriesDesign));
    }

    @Test
    public void testsaveDesign()throws Exception{
        Long categoryDesignId = 1L;
        String title = "Nes Design";
        String description = "Nes Design Description";
        String image = "newImage.jpg";
        MockMultipartFile file = new MockMultipartFile("image", "newImage.jpg", image, "image content".getBytes());

        when(categoryDesignService.findById(categoryDesignId)).thenReturn(Optional.of(categoriesDesign.get(0)));

        when(uploadFileService.saveImages(file)).thenReturn(image);

         mockMvc.perform(multipart("/admin/diseños/crear/guardar")
                .file(file)
                .param("title", title)
                .param("description", description)
                .param("categoryDesign", String.valueOf(categoryDesignId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attribute("message", "Diseño guardado exitosamente"));

        // Verifica que se haya llamado al servicio de guardado de diseño
        verify(designService).save(any(Design.class));
    }

    @Test
    public void testUpdateDesign() throws Exception {
        Long designId = 1L;
        Design design = designs.get(0);// Use the first design from the list
        Optional<Design> optionalDesign = Optional.of(design);

        // Simula respuesta del servicio para encontrar el diseño por ID
        when(designService.findById(designId)).thenReturn(optionalDesign);

        // Realiza la llamada GET y verifica la respuesta
        mockMvc.perform(get("/admin/diseños/editar/{id}", designId))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/design/editDesign"))
                .andExpect(model().attribute("design", design))
                .andExpect(model().attribute("categoriesDesign", categoriesDesign));
    }

    @Test
    public void testDeleteDesign() throws Exception {
        Long designId = 1L;
        Design design = designs.get(0);
        Optional<Design> optionalDesign = Optional.of(design);

        when(designService.findById(designId)).thenReturn(optionalDesign);

        doNothing().when(uploadFileService).deleteImage(design.getImage());

        mockMvc.perform(post("/admin/diseños/eliminar/{id}", designId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(designService).delete(designId);
        verify(uploadFileService).deleteImage(design.getImage());
    }
    
    @Test
    public void testShowCategoryDesign() throws Exception{
        mockMvc.perform(get("/admin/categorias"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categoryDesign/showCategory"))
                .andExpect(model().attribute("categories", categoriesDesign));

    }

    @Test
    public void testCreateCategoryDesign() throws Exception{
        mockMvc.perform(get("/admin/categorias/crear"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/categoryDesign/createCategory"))
        .andExpect(model().attribute("categoriesDesign",categoriesDesign));
    }

    @Test
    public void testSaveCategory() throws Exception{
        String nameCategoryDesign = "New Category";
        MockMultipartFile file = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "Test Image Content".getBytes()
        );

         // Simula el comportamiento del servicio de carga de archivos
         when(uploadFileService.saveImages(any(MultipartFile.class))).thenReturn("test.jpg");


        mockMvc.perform(multipart("/admin/categorias/crear/guardar")
        .file(file)
        .param("nameCategoryDesign", nameCategoryDesign))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/categorias"))
        .andExpect(flash().attribute("message", "Categoría guardada exitosamente"));

        verify(categoryDesignService).save(any(CategoryDesign.class));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Long categoryId = 1L;

        CategoryDesign category = new CategoryDesign();
        category.setIdCategoryDesign(categoryId);
        category.setImage("someImage.jpg");
        category.setNameCategoryDesign("someCategory");

        when(categoryDesignService.findById(categoryId)).thenReturn(Optional.of(category));
        
        doNothing().when(uploadFileService).deleteImage("someImage.jpg");
        doNothing().when(categoryDesignService).deleteById(categoryId);

        mockMvc.perform(post("/admin/categorias/eliminar/{id}", categoryId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categorias"));
        
        verify(uploadFileService).deleteImage("someImage.jpg");
        verify(categoryDesignService).deleteById(categoryId);
    }

}
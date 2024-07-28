package com.example.kunturtatto.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.kunturtatto.model.CategoryDesign;
import com.example.kunturtatto.model.Design;
import com.example.kunturtatto.service.ICategoryDesignService;
import com.example.kunturtatto.service.IDesignService;
import com.example.kunturtatto.service.IUploadFileService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IDesignService designService;

    @Autowired
    private ICategoryDesignService categoryDesignService;

    @Autowired
    private IUploadFileService uploadFileService;

    /*Create Designs */
    @GetMapping("")
    public String showDesign(Model model) {

        List<Design> designs = designService.findAll();

        model.addAttribute("designs", designs);

        return "admin/design/showDesign";
    }

    @GetMapping("/diseños/crear")
    public String createDesign(Model model) {

        model.addAttribute("design", designService.findAll());
        model.addAttribute("categoriesDesign", categoryDesignService.findAll());

        return "admin/design/createDesign";
    }

    @GetMapping("/diseños/editar/{id}")
    public String editDesign(@PathVariable Long id, Model model) {

        Design design = new Design();
        Optional<Design> optionalDesign = designService.findById(id);

        if (optionalDesign.isPresent()) {

            design = optionalDesign.get();

            model.addAttribute("design", design);
            model.addAttribute("categoriesDesign", categoryDesignService.findAll());
        } else {
            model.addAttribute("error", "No se encontró el diseño");
        }

        return "/admin/design/editDesign";
    }

    @PostMapping("/diseños/crear/guardar")
    public String saveDesign(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Design design = new Design();
        design.setTitle(title);
        design.setDescription(description);
        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId).orElse(null);
        if (categoryDesign != null) {
            design.setCategoryDesign(categoryDesign);
        }

        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);

                design.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
                return "redirect:/admin/diseños";
            }
        } else {
            design.setImage("default.jpg");
        }

        designService.save(design);
        redirectAttributes.addFlashAttribute("message", "Diseño guardado exitosamente");
        return "redirect:/admin";
    }

    @PostMapping("/diseños/editar/guardar")
    public String updateDesign(
            @RequestParam("idDesign") Long idDesign,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryDesign") Long categoryDesignId,
            @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Optional<Design> optionalDesign = designService.findById(idDesign);
        if (!optionalDesign.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Diseño no encontrado");
            return "redirect:/admin";
        }

        Design design = optionalDesign.get();
        design.setTitle(title);
        design.setDescription(description);
        CategoryDesign categoryDesign = categoryDesignService.findById(categoryDesignId).orElse(null);
        if (categoryDesign != null) {
            design.setCategoryDesign(categoryDesign);
        }

        if (!file.isEmpty()) {
            try {
                String imageName = uploadFileService.saveImages(file);
                design.setImage(imageName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Error al guardar la imagen");
                return "redirect:/admin/diseños";
            }
        }

        designService.update(design);
        redirectAttributes.addFlashAttribute("message", "Diseño actualizado con éxito");
        return "redirect:/admin";
    }

    @PostMapping("/diseños/eliminar/{id}")
    public String Delete(@PathVariable long id) {

        Design design = new Design();
        design = designService.findById(id).get();

        if (!design.getImage().equals("default.jpg")) {
            uploadFileService.deleteImage(design.getImage());
        }

        designService.delete(id);

        return "redirect:/admin";
    }


    /*Create Categories */
    @GetMapping("/categorias")
    public String showCategory(Model model){
        List <CategoryDesign> categoryDesigns = categoryDesignService.findAll();
        model.addAttribute("categoryDesigns", categoryDesigns);
        return "admin/categoryDesign/showCategory";
    }

    @GetMapping("/categorias/crear")
    public String createCategory(Model model) {

        model.addAttribute("categoriesDesign", categoryDesignService.findAll());

        return "admin/categoryDesign/createCategory";
    }

    @PostMapping("categorias/crear/guardar")
    public String saveCategory(@RequestParam("nameCategoryDesign") String nameCategoryDesign, RedirectAttributes redirectAttributes) {

        CategoryDesign categoryDesign = new CategoryDesign();
        categoryDesign.setNameCategoryDesign(nameCategoryDesign);
        categoryDesignService.save(categoryDesign);

        redirectAttributes.addFlashAttribute("message", "Categoría guardada exitosamente");

        return "redirect:/admin/categorias";
    }

    @PostMapping("categorias/eliminar/{id}")
    public String deleteCategoty(@PathVariable Long id) {
        
        categoryDesignService.deleteById(id);
        
        return "redirect:/admin/categorias";
    }
    
}

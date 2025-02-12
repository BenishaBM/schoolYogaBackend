package com.annular.SchoolYogaBackends.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.service.CategoryService;
import com.annular.SchoolYogaBackends.webModel.CategoryWebModel;

@RestController
@RequestMapping("/category")
public class CategoryController {

	public static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

	@Autowired
	CategoryService categoryService;

	@PostMapping("saveCategory")
	public ResponseEntity<?> saveCategory(@RequestBody CategoryWebModel categoryWebModel) {
		try {
			logger.info("saveCategory controller start");
			return categoryService.saveCategory(categoryWebModel);
		} catch (Exception e) {
			logger.error("saveCategory Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	@GetMapping("getAllCategory")
	public ResponseEntity<?> getAllCategory() {
		try {
			logger.info("getAllCategory controller start");
			return categoryService.getAllCategory();
		} catch (Exception e) {
			logger.error("getAllCategory Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@PostMapping("updateCategory")
	public ResponseEntity<?> updateCategory(@RequestBody CategoryWebModel categoryWebModel) {
		try {
			logger.info("updateCategory controller start");
			return categoryService.updateCategory(categoryWebModel);
		} catch (Exception e) {
			logger.error("updateCategory Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@DeleteMapping("deleteCategory")
	public ResponseEntity<?>deleteCategory(@RequestParam("categoryId") Integer categoryId) {
		try {
			logger.info("deleteCategory controller start");
			return categoryService.deleteCategory(categoryId);
		} catch (Exception e) {
			logger.error("deleteCategory Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}
	
	@GetMapping("getCategoryById")
	public ResponseEntity<?>getCategoryById(@RequestParam("categoryId") Integer categoryId) {
		try {
			logger.info("getCategoryById controller start");
			return categoryService.getCategoryById(categoryId);
		} catch (Exception e) {
			logger.error("getCategoryById Method Exception {}" + e);
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new Response(-1, "Fail", e.getMessage()));
		}

	}

}

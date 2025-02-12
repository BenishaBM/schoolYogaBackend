package com.annular.SchoolYogaBackends.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.model.Category;
import com.annular.SchoolYogaBackends.repository.CategoryRepository;
import com.annular.SchoolYogaBackends.service.CategoryService;
import com.annular.SchoolYogaBackends.webModel.CategoryWebModel;

@Service
public class CategoryServiceImpl implements CategoryService{
	
	public static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	

	@Autowired
	CategoryRepository categoryRepository;
	
	@Override
	public ResponseEntity<?> saveCategory(CategoryWebModel categoryWebModel) {
	    try {
	        logger.info("Saving category: {}", categoryWebModel);

	        // Validate categoryName
	        if (categoryWebModel.getCategoryName() == null || categoryWebModel.getCategoryName().trim().isEmpty()) {
	            return ResponseEntity.badRequest().body(new Response(-1, "Category name cannot be empty", null));
	        }

	        String categoryName = categoryWebModel.getCategoryName().trim();

	        // Check if category already exists
	        if (categoryRepository.existsByCategoryName(categoryName)) {
	            return ResponseEntity.badRequest().body(new Response(-1, "Category already exists", null));
	        }

	        // Convert WebModel to Entity
	        Category category = Category.builder()
	                .categoryName(categoryName)
	                .categoryIsActive(categoryWebModel.getCategoryIsActive())
	                .categorycreatedBy(categoryWebModel.getCategorycreatedBy())
	                .build();

	        // Save to database
	        categoryRepository.save(category);

	        logger.info("Category saved successfully with ID: {}", category.getCategoryId());
	        return ResponseEntity.ok(new Response(1, "Category saved successfully", category));

	    } catch (Exception e) {
	        logger.error("Error saving category: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}

	@Override
	public ResponseEntity<?> getAllCategory() {
	    try {
	        logger.info("Fetching all categories");

	        // Fetch all categories from the database
	        List<Category> categories = categoryRepository.findByCategoryIsActiveTrue();

	        // Check if categories list is empty
	        if (categories.isEmpty()) {
	            return ResponseEntity.ok(new Response(1, "No categories found", categories));
	        }

	        logger.info("Categories retrieved successfully");
	        return ResponseEntity.ok(new Response(1, "Categories retrieved successfully", categories));

	    } catch (Exception e) {
	        logger.error("Error retrieving categories: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "Fail", e.getMessage()));
	    }
	}
	@Override
	public ResponseEntity<?> updateCategory(CategoryWebModel categoryWebModel) {
	    if (categoryWebModel == null || categoryWebModel.getCategoryId() == null) {
	        return ResponseEntity.badRequest().body(new Response(0, "fail", "Invalid category data"));
	    }

	    Optional<Category> existingCategoryOpt = categoryRepository.findById(categoryWebModel.getCategoryId());

	    if (!existingCategoryOpt.isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(0, "fail", "Category not found"));
	    }

	    // Check if categoryName already exists for another category
	    Optional<Category> duplicateCategory = categoryRepository.findByCategoryName(categoryWebModel.getCategoryName());
	    if (duplicateCategory.isPresent() && !duplicateCategory.get().getCategoryId().equals(categoryWebModel.getCategoryId())) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(0, "fail", "Category name already exists"));
	    }

	    Category existingCategory = existingCategoryOpt.get();
	    existingCategory.setCategoryName(categoryWebModel.getCategoryName());

	    categoryRepository.save(existingCategory);

	    return ResponseEntity.ok(new Response(1, "success", "Category updated successfully"));
	}

	@Override
	public ResponseEntity<?> deleteCategory(Integer categoryId) {
		   try {
		        logger.info("deleteCategory controller start");

		        if (categoryId == null) {
		            return ResponseEntity.badRequest().body(new Response(0, "fail", "Category ID is required"));
		        }

		        Optional<Category> existingCategoryOpt = categoryRepository.findById(categoryId);
		        if (!existingCategoryOpt.isPresent()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(0, "fail", "Category not found"));
		        }

		        Category existingCategory = existingCategoryOpt.get();
		        existingCategory.setCategoryIsActive(false); // Soft delete by setting inactive
		        categoryRepository.save(existingCategory);

		        return ResponseEntity.ok(new Response(1, "success", "Category soft deleted successfully"));

		    } catch (Exception e) {
		        logger.error("deleteCategory Method Exception: {}", e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new Response(-1, "Fail", "Internal Server Error"));
		    }
	}

	@Override
	public ResponseEntity<?> getCategoryById(Integer categoryId) {
	    try {
	        logger.info("getCategoryById controller start");

	        // Validate the input
	        if (categoryId == null) {
	            return ResponseEntity.badRequest()
	                    .body(new Response(0, "fail", "Category ID is required"));
	        }

	        // Retrieve the category from the repository
	        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

	        // Check if category exists and is active (soft delete check)
	        if (!categoryOpt.isPresent() || !categoryOpt.get().getCategoryIsActive()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(new Response(0, "fail", "Category not found"));
	        }

	        Category category = categoryOpt.get();

	        // Optionally, you could map the Category entity to a CategoryWebModel before returning
	        return ResponseEntity.ok(new Response(1, "success", category));

	    } catch (Exception e) {
	        logger.error("getCategoryById Method Exception: {}", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new Response(-1, "fail", "Internal server error"));
	    }
	}


}

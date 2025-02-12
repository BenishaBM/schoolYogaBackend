package com.annular.SchoolYogaBackends.service;

import org.springframework.http.ResponseEntity;

import com.annular.SchoolYogaBackends.webModel.CategoryWebModel;

public interface CategoryService {

	ResponseEntity<?> saveCategory(CategoryWebModel categoryWebModel);

	ResponseEntity<?> getAllCategory();

	ResponseEntity<?> updateCategory(CategoryWebModel categoryWebModel);

	ResponseEntity<?> deleteCategory(Integer categoryId);

	ResponseEntity<?> getCategoryById(Integer categoryId);

}

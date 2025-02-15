package com.annular.SchoolYogaBackends.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.service.YogaService;
import com.annular.SchoolYogaBackends.util.Utility;
import com.annular.SchoolYogaBackends.webModel.YogaWebModel;

@RestController
@RequestMapping("/admin/yoga")
public class YogaController {
	
	@Autowired
	YogaService yogaService;
	
	public static final Logger logger = LoggerFactory.getLogger(YogaController.class);
	
    @RequestMapping(path = "/saveYoga", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response saveYoga(@ModelAttribute YogaWebModel inputFileData) {
        try {
            logger.info("savePost Inputs :- {}", inputFileData);
            YogaWebModel outputFileData = yogaService.saveYogaWithFiles(inputFileData);
            if (outputFileData != null) return new Response(1, "YogaPost saved successfully...", outputFileData);
        } catch (Exception e) {
            logger.error("Error at YogaPost() -> {}", e.getMessage());
            return new Response(-1, "Error occurred while saving YogaPost with files -> {}", e.getMessage());
        }
        return new Response(-1, "Error occurred while saving YogaPost with files...", null);
    }

    @GetMapping("/getAllYogaPosts")
    public Response getAllYogaPosts() {
        try {
            List<YogaWebModel> postWebModelList = yogaService.getAllUsersPosts();
            if (!Utility.isNullOrEmptyList(postWebModelList)) return new Response(1, "Success", postWebModelList);
        } catch (Exception e) {
            logger.error("Error at getAllYogaPosts() -> {}", e.getMessage());
            e.printStackTrace();
            return new Response(0, "fail", "A Yoga entry with this day and classDetailsId already exists.");
        }
        return new Response(-1, "Files were not found....", null);
    }
    
    @GetMapping("/getYogaById")
    public Response getPostsByPostId(@RequestParam("id") Integer id) {
        try {
        	YogaWebModel output = yogaService.getPostByYogaId(id);
            if (output != null) return new Response(1, "Post(s) found successfully...", output);
            else return new Response(-1, "No Post(s) available...", null);
        } catch (Exception e) {
            logger.error("Error at getPostsByPostId() -> {}", e.getMessage());
        }
        return new Response(-1, "Post files were not found...", null);
    }
    
    @PostMapping("/deleteYogaPostById")
    public ResponseEntity<?> deleteYogaPostById(@RequestBody YogaWebModel yogaWebModel) {
        try {
            boolean isDeleted = yogaService.deleteYogaPostById(yogaWebModel);
            if (isDeleted) {
                return ResponseEntity.ok(new Response(1, "Success","Post deleted successfully."));
            } else {
                return ResponseEntity.badRequest().body(new Response(-1, "fail","Failed to delete post"));
            }
        } catch (Exception e) {
            logger.error("deletePostByUserId Method Exception -> {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Response(-1, "Fail", e.getMessage()));
        }
    }
    
    @RequestMapping(path = "/updateYogaPost", method = RequestMethod.PUT, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response updateYogaPost(@ModelAttribute YogaWebModel yogaWebModel) {
        try {
            logger.info("updateYogaPost Inputs :- {}", yogaWebModel);

            // Validate input data
            if (yogaWebModel == null || yogaWebModel.getId() == null) {
                return new Response(-1, "Invalid input data. Yoga ID is required.", null);
            }

            YogaWebModel updatedPost = yogaService.updateYogaWithFiles(yogaWebModel);
            if (updatedPost != null) {
                return new Response(1, "Yoga post updated successfully.", updatedPost);
            } else {
                return new Response(-1, "Failed to update Yoga post.", null);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Validation error at updateYogaPost() -> {}", e.getMessage());
            return new Response(-1, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("Error at updateYogaPost() -> {}", e.getMessage(), e);
            return new Response(-1, "An unexpected error occurred while updating Yoga post.", null);
        }
    }

}

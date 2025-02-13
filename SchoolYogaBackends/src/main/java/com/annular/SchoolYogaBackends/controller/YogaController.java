package com.annular.SchoolYogaBackends.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
            return new Response(-1, "Error at getting user posts....", e.getMessage());
        }
        return new Response(-1, "Files were not found....", null);
    }
}

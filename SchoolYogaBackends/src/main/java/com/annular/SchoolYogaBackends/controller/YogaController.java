package com.annular.SchoolYogaBackends.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.annular.SchoolYogaBackends.Response;
import com.annular.SchoolYogaBackends.service.YogaService;
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

}

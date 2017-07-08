package com.romif.securityalarm.web.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by Roman_Konovalov on 3/1/2017.
 */
@Controller
public class ViewController {

    @Inject
    private Environment env;

    @Value("index.html")
    private Resource indexHtmlResource;

    private String indexHtmlContent;

    @PostConstruct
    public void init() throws IOException {
        indexHtmlContent = IOUtils.toString(indexHtmlResource.getInputStream(), "UTF-8");
    }

    @RequestMapping(value = {
        "/my-devices",
        "/my-alarms",
        "/status",
        "/map",
        "/settings",
        "/password",
        "/"
    },
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> index() {
        return new ResponseEntity<>(indexHtmlContent, HttpStatus.OK);
    }
}

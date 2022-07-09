package com.ydles.search.controller;

import com.ydles.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Scz
 * @date 2022/4/13 20:07
 */
@RestController
@RequestMapping("/esManager")
public class ESManagerController {

    @Autowired
    ESManagerService esManagerService;


}

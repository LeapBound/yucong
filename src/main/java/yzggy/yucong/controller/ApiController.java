package yzggy.yucong.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/ocr")
    public String ocr() {
        return "<h1>ok</h1>";
    }
}
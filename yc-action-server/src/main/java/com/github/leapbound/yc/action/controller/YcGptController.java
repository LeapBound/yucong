package com.github.leapbound.yc.action.controller;

import com.github.leapbound.yc.action.model.vo.ResponseVo;
import com.github.leapbound.yc.action.service.YcGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fred
 * @date 2024-11-28 15:57
 */
@RestController
@RequestMapping("/gpt")
@RequiredArgsConstructor
public class YcGptController {

    private final YcGptService gptService;

    @PostMapping("/method/save")
    public ResponseVo<Void> saveFunctionMethod() {
        this.gptService.completions(null, null);
        return null;
    }
}

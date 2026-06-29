package com.example.governance.controller;

import com.example.governance.model.Return;
import com.example.governance.service.ReturnService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    @GetMapping("/{id}")
    public Return getReturn(@PathVariable Long id) {
        return returnService.getReturn(id);
    }

    @PostMapping
    public Return createReturn(@RequestBody Return returnRequest) {
        return returnService.createReturn(returnRequest);
    }

    @GetMapping
    public List<Return> getAllReturns() {
        return returnService.getAllReturns();
    }
}

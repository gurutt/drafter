package org.gurutt.drafter.controller;

import org.gurutt.drafter.service.PlayerSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class LineUpRoute {

    @Autowired
    private PlayerSelector playerSelector;

    @PostMapping(value = "/lineup/select", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> build(
            @RequestBody List<String> data) {

        return ResponseEntity.ok(playerSelector.select(data));
    }
}

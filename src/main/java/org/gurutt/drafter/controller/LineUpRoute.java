package org.gurutt.drafter.controller;

import io.vavr.collection.List;
import org.gurutt.drafter.service.LineUpEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class LineUpRoute {

    @Autowired
    private LineUpEngine lineUpEngine;

    @PostMapping(value = "/lineup/build", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> build(
            @RequestBody List<String> data) {

        return ResponseEntity.ok(lineUpEngine.build(data));
    }
}

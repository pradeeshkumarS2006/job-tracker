package com.pradeesh.jobtracker.controller;

import com.pradeesh.jobtracker.dto.SuggestionDtos.SuggestionRequest;
import com.pradeesh.jobtracker.dto.SuggestionDtos.SuggestionResponse;
import com.pradeesh.jobtracker.service.ResumeSuggestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
public class SuggestionController {

    private final ResumeSuggestionService suggestionService;

    public SuggestionController(ResumeSuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/{id}/suggest-bullets")
    public ResponseEntity<?> suggestBullets(@PathVariable Long id, @Valid @RequestBody SuggestionRequest request) {
        try {
            SuggestionResponse response = suggestionService.getSuggestions(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
}

package com.pradeesh.jobtracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pradeesh.jobtracker.dto.SuggestionDtos.SuggestionRequest;
import com.pradeesh.jobtracker.dto.SuggestionDtos.SuggestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Concept 5: LLM integration.
 * One narrow job: given a job description + the user's current resume text,
 * ask an LLM for 3-5 tailored bullet point suggestions.
 * Every call is validated (input length capped) and logged with an approximate cost/token count.
 */
@Service
public class ResumeSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(ResumeSuggestionService.class);
    private static final int MAX_INPUT_CHARS = 6000; // basic validation / cost guard

    @Value("${llm.api.key:}")
    private String apiKey;

    @Value("${llm.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${llm.model:claude-3-5-haiku-20241022}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public SuggestionResponse getSuggestions(SuggestionRequest request) {
        // --- validation ---
        if (request.jobDescription.length() > MAX_INPUT_CHARS || request.currentResumeText.length() > MAX_INPUT_CHARS) {
            throw new IllegalArgumentException("Input too long. Max " + MAX_INPUT_CHARS + " characters each.");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("LLM_API_KEY is not configured. Set it as an environment variable.");
        }

        String prompt = buildPrompt(request);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "model": "%s",
                  "max_tokens": 400,
                  "messages": [{"role": "user", "content": %s}]
                }
                """.formatted(model, mapper.valueToTree(prompt).toString());

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
        long durationMs = System.currentTimeMillis() - start;

        List<String> bullets = parseBullets(response.getBody());
        int approxTokens = (prompt.length() + String.join(" ", bullets).length()) / 4; // rough estimate

        // Cost log - required by the concept: "with validation and a cost log"
        log.info("LLM call complete | model={} | durationMs={} | approxTokens={}", model, durationMs, approxTokens);

        return new SuggestionResponse(bullets, approxTokens);
    }

    private String buildPrompt(SuggestionRequest request) {
        return """
                You are a resume-writing assistant. Given the job description and the candidate's current resume text,
                suggest 3 to 5 tailored resume bullet points that highlight relevant skills for this specific job.
                Keep each bullet under 20 words. Return ONLY the bullets, one per line, no numbering, no extra text.

                JOB DESCRIPTION:
                %s

                CURRENT RESUME TEXT:
                %s
                """.formatted(request.jobDescription, request.currentResumeText);
    }

    private List<String> parseBullets(String rawJsonResponse) {
        List<String> bullets = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(rawJsonResponse);
            String text = root.path("content").get(0).path("text").asText();
            for (String line : text.split("\n")) {
                if (!line.isBlank()) {
                    bullets.add(line.trim());
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse LLM response: {}", e.getMessage());
            bullets.add("Could not parse suggestions - check LLM response format.");
        }
        return bullets;
    }
}

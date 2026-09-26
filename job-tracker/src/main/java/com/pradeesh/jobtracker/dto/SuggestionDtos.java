package com.pradeesh.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class SuggestionDtos {

    public static class SuggestionRequest {
        @NotBlank
        public String jobDescription;
        @NotBlank
        public String currentResumeText;
    }

    public static class SuggestionResponse {
        public List<String> suggestedBullets;
        public int approxTokensUsed;

        public SuggestionResponse(List<String> suggestedBullets, int approxTokensUsed) {
            this.suggestedBullets = suggestedBullets;
            this.approxTokensUsed = approxTokensUsed;
        }
    }
}

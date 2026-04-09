package com.willmear.sprint.ai.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.ai.domain.model.AiPrompt;
import com.willmear.sprint.ai.domain.model.AiResponse;
import com.willmear.sprint.ai.domain.model.TokenUsage;
import com.willmear.sprint.ai.model.SprintReviewAiResponse;
import com.willmear.sprint.ai.parser.AiJsonContentExtractor;
import com.willmear.sprint.ai.parser.SprintReviewParser;
import com.willmear.sprint.ai.prompt.builder.SprintPromptCompressionService;
import com.willmear.sprint.ai.prompt.builder.SprintReviewPromptBuilder;
import com.willmear.sprint.ai.prompt.builder.SprintReviewPromptFormatter;
import com.willmear.sprint.common.exception.AiGenerationException;
import com.willmear.sprint.common.exception.AiPromptBuildException;
import com.willmear.sprint.common.exception.AiResponseParseException;
import com.willmear.sprint.config.SprintReviewAiProperties;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiParsingAndPromptBuilderTest {

    private final AiJsonContentExtractor extractor = new AiJsonContentExtractor();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final SprintReviewAiProperties sprintReviewAiProperties = new SprintReviewAiProperties(true, true, 100, 5, 2000, 800);

    @Test
    void shouldValidateAiResponses() {
        AiResponseValidator validator = new AiResponseValidator();
        AiResponse response = new AiResponse("{}", "gpt-test", new TokenUsage(1, 1, 2), Instant.now(), "stop", true, null);

        assertThat(validator.validate(response)).isEqualTo(response);
        assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(AiGenerationException.class);
        assertThatThrownBy(() -> validator.validate(new AiResponse("", "gpt-test", null, Instant.now(), "stop", true, null)))
                .isInstanceOf(AiGenerationException.class);
    }

    @Test
    void shouldExtractJsonAndParseStructuredSprintReviewResponse() {
        AiResponse response = new AiResponse("""
                ```json
                {
                  "summary":{"title":"Sprint Review","overview":"Overview","deliverySummary":"Delivery","qualitySummary":"Quality","outcomeSummary":"Outcome"},
                  "themes":[{"name":"Theme","description":"Desc","relatedIssueKeys":["SPR-1"]}],
                  "highlights":[{"title":"Highlight","description":"Delivered","relatedIssueKeys":["SPR-1"],"category":"FEATURE"}],
                  "blockers":[{"title":"Blocker","description":"Dependency","relatedIssueKeys":["SPR-2"],"severity":"MEDIUM"}],
                  "speakerNotes":[{"section":"Intro","note":"Say hello","displayOrder":1}]
                }
                ```
                """, "gpt-test", null, Instant.now(), "stop", true, null);

        SprintReviewAiResponse parsed = new SprintReviewParser(objectMapper, extractor).parse(response);

        assertThat(parsed.summary().title()).isEqualTo("Sprint Review");
        assertThat(parsed.themes().getFirst().name()).isEqualTo("Theme");
        assertThat(parsed.highlights().getFirst().category()).isEqualTo("FEATURE");
        assertThat(parsed.blockers().getFirst().severity()).isEqualTo("MEDIUM");
        assertThat(parsed.speakerNotes().getFirst().section()).isEqualTo("Intro");
    }

    @Test
    void shouldRejectInvalidJsonPayloads() {
        assertThatThrownBy(() -> extractor.extractJsonObject("not-json")).isInstanceOf(AiResponseParseException.class);
        assertThatThrownBy(() -> new SprintReviewParser(objectMapper, extractor).parse(new AiResponse("oops", "gpt-test", null, Instant.now(), "stop", true, null)))
                .isInstanceOf(AiResponseParseException.class);
    }

    @Test
    void shouldBuildStructuredSprintReviewPrompt() {
        var context = TestSprintReviewFactory.contextWithIssues();
        SprintPromptCompressionService compressionService = new SprintPromptCompressionService(sprintReviewAiProperties);
        SprintReviewPromptFormatter formatter = new SprintReviewPromptFormatter(objectMapper);
        SprintReviewPromptBuilder builder = new SprintReviewPromptBuilder(compressionService, formatter);

        AiPrompt prompt = builder.build(context, "leadership", "concise", "gpt-test");

        assertThat(prompt.responseFormat()).isEqualTo("json-object");
        assertThat(prompt.userPrompt()).contains("Return JSON only using this schema:");
        assertThat(prompt.userPrompt()).contains("\"SPR-4\"");
        assertThat(prompt.userPrompt()).contains("Blocked on upstream dependency");
    }

    @Test
    void shouldRejectNullContextInPromptBuilder() {
        SprintPromptCompressionService compressionService = new SprintPromptCompressionService(sprintReviewAiProperties);
        SprintReviewPromptFormatter formatter = new SprintReviewPromptFormatter(objectMapper);
        SprintReviewPromptBuilder builder = new SprintReviewPromptBuilder(compressionService, formatter);

        assertThatThrownBy(() -> builder.build(null, null, null, null))
                .isInstanceOf(AiPromptBuildException.class);
    }
}

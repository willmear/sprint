package com.willmear.sprint.ai.prompt.template;

public final class PromptTemplateCatalog {

    private PromptTemplateCatalog() {
    }

    public static String systemTemplate(String promptName) {
        return switch (promptName) {
            case "sprint-summary" -> "You are an engineering program assistant. Produce concise structured sprint review summaries.";
            case "sprint-themes" -> "You are an engineering insights assistant. Extract a few clear sprint themes in structured form.";
            case "sprint-speaker-notes" -> "You are a presentation assistant. Produce concise speaker notes for a sprint review.";
            default -> "You are a helpful assistant.";
        };
    }

    public static String userTemplatePrefix(String promptName) {
        return switch (promptName) {
            case "sprint-summary" -> "Generate a structured sprint summary from the following sprint context.";
            case "sprint-themes" -> "Generate structured sprint themes from the following sprint context.";
            case "sprint-speaker-notes" -> "Generate structured speaker notes from the following sprint context.";
            default -> "Process the following input.";
        };
    }
}

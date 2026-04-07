package com.willmear.sprint.artifact.application.support;

import com.willmear.sprint.sprintreview.domain.model.SpeakerNote;
import com.willmear.sprint.sprintreview.domain.model.SprintBlocker;
import com.willmear.sprint.sprintreview.domain.model.SprintHighlight;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import com.willmear.sprint.sprintreview.domain.model.SprintTheme;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewMarkdownRenderer {

    public String render(SprintReview sprintReview) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(sprintReview.summary().title()).append("\n\n");
        markdown.append(sprintReview.summary().overview()).append("\n\n");

        appendSection(markdown, "Themes", sprintReview.themes().stream()
                .map(this::renderTheme)
                .toList());
        appendSection(markdown, "Highlights", sprintReview.highlights().stream()
                .map(this::renderHighlight)
                .toList());
        appendSection(markdown, "Blockers", sprintReview.blockers().stream()
                .map(this::renderBlocker)
                .toList());
        appendSection(markdown, "Speaker Notes", sprintReview.speakerNotes().stream()
                .map(this::renderSpeakerNote)
                .toList());

        return markdown.toString().trim();
    }

    private void appendSection(StringBuilder markdown, String title, java.util.List<String> lines) {
        markdown.append("## ").append(title).append("\n\n");
        if (lines.isEmpty()) {
            markdown.append("- None recorded.\n\n");
            return;
        }
        lines.forEach(line -> markdown.append(line).append('\n'));
        markdown.append('\n');
    }

    private String renderTheme(SprintTheme theme) {
        return "- **" + theme.name() + "**: " + theme.description() + relatedIssues(theme.relatedIssueKeys());
    }

    private String renderHighlight(SprintHighlight highlight) {
        return "- **" + highlight.title() + "**: " + highlight.description() + relatedIssues(highlight.relatedIssueKeys());
    }

    private String renderBlocker(SprintBlocker blocker) {
        return "- **" + blocker.title() + "**: " + blocker.description() + relatedIssues(blocker.relatedIssueKeys());
    }

    private String renderSpeakerNote(SpeakerNote speakerNote) {
        return "- **" + speakerNote.section() + "**: " + speakerNote.note();
    }

    private String relatedIssues(java.util.List<String> issueKeys) {
        if (issueKeys == null || issueKeys.isEmpty()) {
            return "";
        }
        return " _(Issues: " + issueKeys.stream().collect(Collectors.joining(", ")) + ")_";
    }
}

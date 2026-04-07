package com.willmear.sprint.sprintreview.mapper;

import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.sprintreview.api.response.GenerateSprintReviewJobResponse;
import com.willmear.sprint.sprintreview.api.response.SpeakerNoteResponse;
import com.willmear.sprint.sprintreview.api.response.SprintBlockerResponse;
import com.willmear.sprint.sprintreview.api.response.SprintHighlightResponse;
import com.willmear.sprint.sprintreview.api.response.SprintReviewResponse;
import com.willmear.sprint.sprintreview.api.response.SprintSummaryResponse;
import com.willmear.sprint.sprintreview.api.response.SprintThemeResponse;
import com.willmear.sprint.sprintreview.domain.model.SprintReview;
import org.springframework.stereotype.Component;

@Component
public class SprintReviewMapper {

    public SprintReviewResponse toResponse(SprintReview sprintReview) {
        return new SprintReviewResponse(
                sprintReview.id(),
                sprintReview.workspaceId(),
                sprintReview.externalSprintId(),
                sprintReview.sprintName(),
                new SprintSummaryResponse(
                        sprintReview.summary().title(),
                        sprintReview.summary().overview(),
                        sprintReview.summary().deliverySummary(),
                        sprintReview.summary().qualitySummary(),
                        sprintReview.summary().outcomeSummary()
                ),
                sprintReview.themes().stream()
                        .map(theme -> new SprintThemeResponse(theme.name(), theme.description(), theme.relatedIssueKeys()))
                        .toList(),
                sprintReview.highlights().stream()
                        .map(highlight -> new SprintHighlightResponse(
                                highlight.title(),
                                highlight.description(),
                                highlight.relatedIssueKeys(),
                                highlight.category()
                        ))
                        .toList(),
                sprintReview.blockers().stream()
                        .map(blocker -> new SprintBlockerResponse(
                                blocker.title(),
                                blocker.description(),
                                blocker.relatedIssueKeys(),
                                blocker.severity()
                        ))
                        .toList(),
                sprintReview.speakerNotes().stream()
                        .map(note -> new SpeakerNoteResponse(note.section(), note.note(), note.displayOrder()))
                        .toList(),
                sprintReview.generatedAt(),
                sprintReview.generationSource(),
                sprintReview.status()
        );
    }

    public GenerateSprintReviewJobResponse toJobResponse(Job job) {
        return new GenerateSprintReviewJobResponse(job.id(), job.status().name(), job.availableAt(), job.createdAt());
    }
}

package com.willmear.sprint.retrieval.domain.service;

import com.willmear.sprint.config.RetrievalProperties;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import org.springframework.stereotype.Component;

@Component
public class MetadataFilterService {

    private final RetrievalProperties retrievalProperties;

    public MetadataFilterService(RetrievalProperties retrievalProperties) {
        this.retrievalProperties = retrievalProperties;
    }

    public RetrievalQuery normalize(RetrievalQuery query) {
        int topK = query.topK() == null || query.topK() < 1 ? retrievalProperties.defaultTopK() : query.topK();
        return new RetrievalQuery(
                query.workspaceId(),
                query.queryText(),
                topK,
                query.externalSprintId(),
                query.sourceType(),
                query.metadataFilters(),
                query.includeContent(),
                query.includeScores()
        );
    }
}

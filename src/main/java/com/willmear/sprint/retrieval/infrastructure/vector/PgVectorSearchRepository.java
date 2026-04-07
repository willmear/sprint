package com.willmear.sprint.retrieval.infrastructure.vector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.common.exception.RetrievalIndexingException;
import com.willmear.sprint.common.exception.RetrievalSearchException;
import com.willmear.sprint.retrieval.domain.model.EmbeddingDocument;
import com.willmear.sprint.retrieval.domain.model.RetrievalQuery;
import com.willmear.sprint.retrieval.domain.model.RetrievalResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PgVectorSearchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PgVectorSearchRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void batchInsert(List<EmbeddingDocument> documents) {
        if (documents.isEmpty()) {
            return;
        }
        try {
            jdbcTemplate.batchUpdate(
                    """
                    INSERT INTO embedding_document (
                        id, workspace_id, jira_connection_id, external_sprint_id, source_type, source_id, source_key, title,
                        content, chunk_text, chunk_index, token_count_estimate, metadata, embedding, indexed_at, created_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), CAST(? AS vector), ?, ?, ?)
                    """,
                    documents,
                    documents.size(),
                    (ps, document) -> {
                        Instant now = document.createdAt() != null ? document.createdAt() : Instant.now();
                        ps.setObject(1, document.id());
                        ps.setObject(2, document.workspaceId());
                        ps.setObject(3, document.jiraConnectionId());
                        ps.setObject(4, document.externalSprintId());
                        ps.setString(5, document.sourceType());
                        ps.setString(6, document.sourceId());
                        ps.setString(7, document.sourceKey());
                        ps.setString(8, document.title());
                        ps.setString(9, document.content());
                        ps.setString(10, document.chunkText());
                        ps.setInt(11, document.chunkIndex());
                        if (document.tokenCountEstimate() != null) {
                            ps.setInt(12, document.tokenCountEstimate());
                        } else {
                            ps.setNull(12, java.sql.Types.INTEGER);
                        }
                        ps.setString(13, serializeJson(document.metadata()));
                        ps.setString(14, toVectorLiteral(document.embedding()));
                        ps.setTimestamp(15, Timestamp.from(document.indexedAt()));
                        ps.setTimestamp(16, Timestamp.from(now));
                        ps.setTimestamp(17, Timestamp.from(document.updatedAt() != null ? document.updatedAt() : now));
                    }
            );
        } catch (RuntimeException exception) {
            throw new RetrievalIndexingException("Failed to insert embedding documents.", exception);
        }
    }

    public List<RetrievalResult> search(RetrievalQuery query, List<Double> queryEmbedding) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, source_type, source_id, source_key, title, chunk_text, metadata,
                       1 - (embedding <=> CAST(? AS vector)) AS score
                FROM embedding_document
                WHERE workspace_id = ?
                """);
        java.util.List<Object> args = new java.util.ArrayList<>();
        args.add(toVectorLiteral(queryEmbedding));
        args.add(query.workspaceId());

        if (query.externalSprintId() != null) {
            sql.append(" AND external_sprint_id = ?");
            args.add(query.externalSprintId());
        }
        if (query.sourceType() != null && !query.sourceType().isBlank()) {
            sql.append(" AND source_type = ?");
            args.add(query.sourceType());
        }

        sql.append(" ORDER BY embedding <=> CAST(? AS vector) ASC LIMIT ?");
        args.add(toVectorLiteral(queryEmbedding));
        args.add(query.topK());

        try {
            return jdbcTemplate.query(sql.toString(), args.toArray(), new RetrievalResultRowMapper(objectMapper, query.includeContent()));
        } catch (RuntimeException exception) {
            throw new RetrievalSearchException("Failed to execute retrieval query.", exception);
        }
    }

    private String serializeJson(JsonNode jsonNode) {
        try {
            return jsonNode == null ? "{}" : objectMapper.writeValueAsString(jsonNode);
        } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
            throw new RetrievalIndexingException("Failed to serialize retrieval metadata.", exception);
        }
    }

    private String toVectorLiteral(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return "[]";
        }
        return "[" + embedding.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) + "]";
    }

    private record RetrievalResultRowMapper(ObjectMapper objectMapper, boolean includeContent) implements RowMapper<RetrievalResult> {
        @Override
        public RetrievalResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                String chunkText = rs.getString("chunk_text");
                return new RetrievalResult(
                        rs.getObject("id", UUID.class),
                        rs.getString("source_type"),
                        rs.getString("source_id"),
                        rs.getString("source_key"),
                        rs.getString("title"),
                        includeContent ? truncate(chunkText) : null,
                        rs.getDouble("score"),
                        objectMapper.readTree(rs.getString("metadata"))
                );
            } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
                throw new SQLException("Failed to deserialize retrieval metadata.", exception);
            }
        }

        private String truncate(String text) {
            if (text == null) {
                return null;
            }
            return text.length() <= 240 ? text : text.substring(0, 240) + "...";
        }
    }
}

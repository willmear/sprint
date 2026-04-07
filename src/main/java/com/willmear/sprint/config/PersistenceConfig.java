package com.willmear.sprint.config;

import com.willmear.sprint.artifact.entity.ArtifactEntity;
import com.willmear.sprint.artifact.repository.ArtifactRepository;
import com.willmear.sprint.jira.infrastructure.entity.JiraBoardEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraChangelogEventEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraOAuthStateEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraRawPayloadEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.jira.infrastructure.repository.JiraBoardRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraConnectionRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraOAuthStateRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraRawPayloadRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.jobs.entity.JobEntity;
import com.willmear.sprint.jobs.repository.JobRepository;
import com.willmear.sprint.retrieval.infrastructure.entity.EmbeddingDocumentEntity;
import com.willmear.sprint.retrieval.infrastructure.repository.EmbeddingDocumentRepository;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import com.willmear.sprint.workspace.repository.WorkspaceRepository;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableConfigurationProperties(RetrievalProperties.class)
@EntityScan(basePackageClasses = {
        WorkspaceEntity.class,
        JiraConnectionEntity.class,
        JiraOAuthStateEntity.class,
        JiraBoardEntity.class,
        JiraSprintEntity.class,
        JiraIssueEntity.class,
        JiraCommentEntity.class,
        JiraChangelogEventEntity.class,
        JiraRawPayloadEntity.class,
        JobEntity.class,
        ArtifactEntity.class,
        EmbeddingDocumentEntity.class
})
@EnableJpaRepositories(basePackageClasses = {
        WorkspaceRepository.class,
        JiraConnectionRepository.class,
        JiraOAuthStateRepository.class,
        JiraBoardRepository.class,
        JiraSprintRepository.class,
        JiraIssueRepository.class,
        JiraCommentRepository.class,
        JiraChangelogEventRepository.class,
        JiraRawPayloadRepository.class,
        JobRepository.class,
        ArtifactRepository.class,
        EmbeddingDocumentRepository.class
})
public class PersistenceConfig {

    @Bean(name = "flyway", initMethod = "migrate")
    public Flyway flyway(
            DataSource dataSource,
            @Value("${spring.flyway.locations:classpath:db/migration}") String locations
    ) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations.split(","))
                .load();
    }

    @Configuration
    static class EntityManagerFactoryDependencyConfig extends AbstractDependsOnBeanFactoryPostProcessor {

        EntityManagerFactoryDependencyConfig() {
            super(EntityManagerFactory.class, Flyway.class);
        }
    }
}

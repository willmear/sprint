package com.willmear.sprint.retrieval.domain.port;

import java.util.List;

public interface EmbeddingGeneratorPort {

    List<List<Double>> generateEmbeddings(List<String> contents);

    List<Double> generateEmbedding(String content);
}

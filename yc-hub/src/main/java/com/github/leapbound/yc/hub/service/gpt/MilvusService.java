package com.github.leapbound.yc.hub.service.gpt;

import java.util.List;

public interface MilvusService {

    void createCollection();

    void dropCollection();

    void insertData(String id, List<Float> embedding);

    String search(List<Float> vector, Double score);
}

package yzggy.yucong.service;

import java.util.List;

public interface MilvusService {

    void createCollection();

    void dropCollection();

    void insertData(long id, List<Float> embedding);
}

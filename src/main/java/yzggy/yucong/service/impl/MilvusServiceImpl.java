package yzggy.yucong.service.impl;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.service.MilvusService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilvusServiceImpl implements MilvusService {

    private final MilvusServiceClient milvusServiceClient;

    @Override
    public void createCollection() {
        FieldType fieldType1 = FieldType.newBuilder()
                .withName("message_id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build();
        FieldType fieldType3 = FieldType.newBuilder()
                .withName("message_summary")
                .withDataType(DataType.FloatVector)
                .withDimension(1536)
                .build();
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName("message")
                .withDescription("Message summary search")
                .withShardsNum(2)
                .addFieldType(fieldType1)
                .addFieldType(fieldType3)
                .withEnableDynamicField(true)
                .build();

        this.milvusServiceClient.createCollection(createCollectionReq);
    }

    @Override
    public void dropCollection() {
        this.milvusServiceClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName("message")
                        .build()
        );
    }

    @Override
    public void insertData(long id, List<Float> embedding) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("message_id", List.of(id)));
        fields.add(new InsertParam.Field("message_summary", List.of(embedding)));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName("message")
                .withFields(fields)
                .build();
        this.milvusServiceClient.insert(insertParam);
    }

    @Override
    public Long search(List<Float> vector) {
        this.milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName("message")
                        .build()
        );

        List<String> searchOutputField = List.of("message_id");
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName("message")
                .withMetricType(MetricType.L2)
                .withVectors(List.of(vector))
                .withVectorFieldName("message_summary")
                .withOutFields(searchOutputField)
                .withTopK(2)
                .build();
        R<SearchResults> respSearch = this.milvusServiceClient.search(searchParam);
        SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
        log.info("getIDScore {}", wrapperSearch.getIDScore(0));

        this.milvusServiceClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName("message")
                        .build());

        return (Long) wrapperSearch.getFieldData("message_id", 0).get(0);
    }
}

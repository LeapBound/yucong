package yzggy.yucong.service.impl;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
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
}

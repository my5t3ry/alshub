package de.my5t3ry.alshub.project;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;


@Builder
@Data
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project-meta-data", shards = 1, type = "ccl_doc")
public class ProjectMetaData {
    @Id
    @Field(type = FieldType.Text, store = true)
    private String id = UUID.randomUUID().toString();
    @Field(type = FieldType.Integer, store = true)
    private Integer projectId = 0;
    @Field(type = FieldType.Integer, store = true)
    private Integer viewCount = 0;
    @Field(type = FieldType.Integer, store = true)
    private Integer ownerUserId = 0;
    @Field(type = FieldType.Integer, store = true)
    private Integer likeCount = 0;
    @Field(type = FieldType.Integer, store = true)
    private Integer forkCount = 0;
}

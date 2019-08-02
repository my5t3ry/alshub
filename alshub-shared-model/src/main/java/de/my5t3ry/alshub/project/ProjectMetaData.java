package de.my5t3ry.alshub.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@org.springframework.data.elasticsearch.annotations.Document(indexName = "project-meta-data", shards = 1, type = "ccl_doc")
public class ProjectMetaData {
    @Id
    @Field(type = FieldType.Text, store = true)
    private String id;
    @Field(type = FieldType.Text, store = true)
    private String name;
    @Field(type = FieldType.Text, store = true)
    private String genres;
    @Field(type = FieldType.Text, store = true)
    private String description;
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

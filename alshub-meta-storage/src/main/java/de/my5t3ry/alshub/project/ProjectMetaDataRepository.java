package de.my5t3ry.alshub.project;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProjectMetaDataRepository extends ElasticsearchRepository<ProjectMetaData, String> {
}

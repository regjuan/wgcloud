package com.wgcloud.entity;

import lombok.Data;

@Data
public class TagRelation {
    private String id;
    private String tagId;
    private String relationId;
    private String relationType;
}

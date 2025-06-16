package com.markpen.library.model.entity;

import lombok.Data;

@Data
public class Comment {
    private String id;
    private Long userId;
    private String username;
    private String content;
    private Long timestamp;
}
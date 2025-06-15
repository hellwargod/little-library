package com.markpen.library.model.dto.libraryResource;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class LibraryResourceCreateRequest implements Serializable {
    private static final long serialVersionUID = 3125L;


    private String title;

    private String type;

    private String locationName;

    private MultipartFile file;

    private String coverUrl;
    private String description;
}
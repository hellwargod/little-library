package com.markpen.library.model.dto.libraryResource;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class LibraryResourceUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3125L;


    private String id;

    private String newTitle;
    private String newType;
    private String newContributor;
    private String newLocationName;
    private String newCoverUrl;
    private String newDescription;
    private MultipartFile newFile;

}
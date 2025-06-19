package com.markpen.library.model.dto.libraryResource;

import com.markpen.library.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class LibraryResourceQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private String id;

    private String title;

    private String type;

    private String contributor;

    private String locationName;

    private String sortField;

    private String sortOrder;
}
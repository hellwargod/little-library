package com.markpen.library.model.dto.libraryResource;

import lombok.Data;

import java.io.Serializable;

@Data
public class LibraryResourceDeleteRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private Long id;

}
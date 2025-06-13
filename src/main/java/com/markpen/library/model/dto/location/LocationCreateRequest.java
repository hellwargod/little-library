package com.markpen.library.model.dto.location;

import lombok.Data;

import java.io.Serializable;
@Data
public class LocationCreateRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private String locationName;

    private String geolocation;
}

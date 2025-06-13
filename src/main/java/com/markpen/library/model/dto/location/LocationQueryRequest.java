package com.markpen.library.model.dto.location;

import com.markpen.library.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class LocationQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private Long id;

    private String locationName;

    private String geolocation;
}

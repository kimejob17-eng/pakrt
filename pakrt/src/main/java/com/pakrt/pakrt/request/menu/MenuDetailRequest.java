package com.pakrt.pakrt.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetailRequest {

    private String id;
    private String menuId;
    private String name;
    private String path;
    private Integer sequence;
    private Boolean isEnabled;
    private String createdBy;
}
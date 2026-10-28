package com.pakrt.pakrt.request.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {

    private String id;
    private String name;
    private String menuKey;
    private String path;
    private String icon;
    private Integer sequence;
    private Boolean isEnabled;
}

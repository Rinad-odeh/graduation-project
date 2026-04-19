package com.baligh.backend.dto.response;

import com.baligh.backend.model.Category;
import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String nameAr;
    private String icon;
    private String color;

    public static CategoryResponse from(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.id = c.getId();
        r.nameAr = c.getNameAr();
        r.icon = c.getIcon();
        r.color = c.getColor();
        return r;
    }
}

package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Folder;
import lombok.Data;

@Data
public class FolderResponse {

    private Long id;
    private String name;

    public FolderResponse(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
    }

}
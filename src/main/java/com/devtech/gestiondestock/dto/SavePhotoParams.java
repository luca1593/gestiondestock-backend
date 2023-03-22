package com.devtech.gestiondestock.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavePhotoParams {
    private String title;
    private Integer id;
    private String context;
}

package com.example.playcation.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileResponseDto {

  private byte[] file;

  private String fileName;

}

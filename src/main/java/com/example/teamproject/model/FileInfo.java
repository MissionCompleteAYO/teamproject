package com.example.teamproject.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FileInfo {
  MultipartFile storeFile;

  MultipartFile reviewFile;
}

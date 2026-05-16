package com.devtech.gestiondestock.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.devtech.gestiondestock.services.CloudinaryService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

  @Value("${cloudinary.cloudName}")
  private String cloudName;

  @Value("${cloudinary.apiKey}")
  private String apiKey;

  @Value("${cloudinary.apiSecret}")
  private String apiSecret;

  private Cloudinary cloudinary;

  @PostConstruct
  private void init() {
    cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret
    ));
    log.info("Cloudinary initialized with cloud name: {}", cloudName);
  }

  @Override
  public String savePhoto(InputStream photo, String title, Integer id) {
    try {
      byte[] bytes = photo.readAllBytes();
      Map<?, ?> uploadResult = cloudinary.uploader().unsignedUpload(bytes, "gestiondestock", ObjectUtils.asMap(
          "public_id", title + "_" + id + "_" + System.currentTimeMillis()
      ));
      String url = (String) uploadResult.get("secure_url");
      log.info("Photo uploaded to Cloudinary: {}", url);
      return url;
    } catch (Exception e) {
      log.error("Failed to upload photo to Cloudinary", e);
      throw new RuntimeException("Failed to upload photo to Cloudinary", e);
    }
  }
}

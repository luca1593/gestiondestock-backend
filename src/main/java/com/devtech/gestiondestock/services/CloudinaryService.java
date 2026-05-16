package com.devtech.gestiondestock.services;

import java.io.InputStream;

public interface CloudinaryService {
  String savePhoto(InputStream photo, String title, Integer id);
}

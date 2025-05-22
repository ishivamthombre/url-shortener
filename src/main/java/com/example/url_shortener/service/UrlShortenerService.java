package com.example.url_shortener.service;

import com.example.url_shortener.model.UrlMapping;
import com.example.url_shortener.repository.UrlMappingRepository;
import com.example.url_shortener.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenerService {

  @Autowired
  private UrlMappingRepository urlMappingRepository;

  public String shortenUrl(String longUrl) {
    UrlMapping urlMapping = new UrlMapping();
    urlMapping.setLongUrl(longUrl);
    urlMapping = urlMappingRepository.save(urlMapping);

    String shortCode = Base62Encoder.encode(urlMapping.getId());
    urlMapping.setShortCode(shortCode);
    urlMappingRepository.save(urlMapping);

    return shortCode;
  }

  public Optional<String> getLongUrl(String shortCode) {
    return urlMappingRepository.findByShortCode(shortCode).map(UrlMapping::getLongUrl);
  }
}

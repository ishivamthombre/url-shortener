package com.example.url_shortener.controller;

import com.example.url_shortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlShortenerController {

  @Autowired
  private UrlShortenerService service;

  @PostMapping("/shorten")
  public String shortenUrl(@RequestBody Map<String, String> request) {
    String longUrl = request.get("longUrl");
    return service.shortenUrl(longUrl);
  }

  @GetMapping("/{shortCode}")
  public ResponseEntity<Object> redirectUrl(@PathVariable String shortCode) {
    return service.getLongUrl(shortCode).map(url -> ResponseEntity.status(302).location(URI.create(url)).build())
        .orElse(ResponseEntity.notFound().build());
  }
}

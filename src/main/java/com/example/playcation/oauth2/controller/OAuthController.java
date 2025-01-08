package com.example.playcation.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthController {

  @GetMapping("/")
  public String mainAPI() {

    return "main";
  }

  @GetMapping("/my")
  public String myAPI() {

    return "my";
  }

}

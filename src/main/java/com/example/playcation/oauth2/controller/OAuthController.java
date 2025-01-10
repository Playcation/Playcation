package com.example.playcation.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/login/oauth2/code/google")
  public String loginGoogle(
      @RequestParam String code
  ) {
    return code;
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login"; // login.html 템플릿 반환
  }
}

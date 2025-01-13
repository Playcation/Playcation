package com.example.playcation.oauth2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

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
  public ModelAndView loginGoogle(@RequestParam String code) {
    return new ModelAndView("redirect:/oauth2/login/success?code=" + code);
  }

//  @GetMapping("/auth/login")
//  public String loginForm() {
//    return "login"; // login.html로 직접 리다이렉트
//  }

  @GetMapping("/oauth2-login")
  public String oauth2LoginForm() {
    return "redirect:/oauth2/authorization/google"; // OAuth2 로그인 페이지로 자동 리다이렉트
  }
}

package com.example.playcation.tag.controller;


import com.example.playcation.tag.Dto.CreatedTagRequestDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.service.TagService;
import com.example.playcation.util.TokenUtil;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;
  private final TokenUtil tokenUtil;

  @PostMapping
  public ResponseEntity<CreatedTagResponseDto> createdTag(@RequestBody CreatedTagRequestDto requestDto) {
    CreatedTagResponseDto responseDto = tagService.CreatedTag(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{tagId}")
  public ResponseEntity<CreatedTagResponseDto> findTag(@PathVariable Long tagId) {
    CreatedTagResponseDto responseDto = tagService.findTag(tagId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<CreatedTagResponseDto>> findAllTag(
      @RequestParam(defaultValue = "0") int page
  ) {
    List<CreatedTagResponseDto> responseDto = tagService.findAllTag(page);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}

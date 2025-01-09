package com.example.playcation.tag.controller;


import com.example.playcation.tag.Dto.CreatedTagRequestDto;
import com.example.playcation.tag.Dto.CreatedTagResponseDto;
import com.example.playcation.tag.service.TagService;
import com.example.playcation.util.JWTUtil;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
  private final JWTUtil jwtUtil;

  @PostMapping
  public ResponseEntity<CreatedTagResponseDto> createTag(@RequestBody CreatedTagRequestDto requestDto) {
    CreatedTagResponseDto responseDto = tagService.CreateTag(requestDto);
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

  @PatchMapping("/{tagId}")
  public ResponseEntity<CreatedTagResponseDto> updateTag(@PathVariable Long tagId, @RequestBody CreatedTagRequestDto requestDto) {
    CreatedTagResponseDto responseDto = tagService.updateTag(tagId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{tagId}")
  public ResponseEntity<String> deleteTag(@PathVariable Long tagId) {
    tagService.deleteTag(tagId);
    return new ResponseEntity<>("삭제되었습니다", HttpStatus.OK);
  }
}

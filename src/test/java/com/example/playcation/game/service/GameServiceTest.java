package com.example.playcation.game.service;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.playcation.category.dto.CategoryRequestDto;
import com.example.playcation.category.entity.Category;
import com.example.playcation.category.repository.CategoryRepository;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.game.dto.CreatedGameRequestDto;
import com.example.playcation.game.dto.GameResponseDto;
import com.example.playcation.game.dto.UpdatedGameRequestDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.GameFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.GameFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito.Then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GameServiceTest {

  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private FileDetailRepository fileDetailRepository;

  @Autowired
  private GameFileRepository gameFileRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GameService gameService;

  @Autowired
  private S3Service s3Service;

  @MockitoBean
  private AmazonS3 s3;


  private FileDetail userFileDetail;
  private FileDetail mainFileDetail;
  private FileDetail gameFileDetail;
  private List<FileDetail> subFileDetail;
  private MultipartFile userFile;
  private MultipartFile mainFile;
  private MultipartFile gameFile;
  private List<MultipartFile> subFileList;
  private CreatedGameRequestDto requestDto;
  private Category category;
  private User user;
  private Game game;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .email("test@example.com")
        .password("encodedPassword")
        .name("Test User")
        .role(Role.USER)
        .social(Social.NORMAL)
        .imageUrl("test-image-url")
        .build();

    userRepository.save(user);

    gameFile = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    mainFile = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    category = Category.builder()
        .categoryName("testName")
        .build();

    categoryRepository.save(category);

    mainFileDetail = new FileDetail("Game", "test-file-path", "test-file-name", "test-file-path", 10000L, "image/png");
    gameFileDetail = new FileDetail("Game", "test-file-path", "test-file-name", "test-file-path", 10000L, "image/png");
    subFileDetail = List.of(
        new FileDetail("Game", "sub-file-path-1", "sub-file-name-1", "sub-file-path-1", 10000L, "image/png"),
        new FileDetail("Game", "sub-file-path-2", "sub-file-name-2", "sub-file-path-2", 10000L, "image/png")
    );



    userFileDetail = new FileDetail("Game", "test-file-path", "test-file-name", "test-file-path",
        10000L, "image/png");

    userFile = new MockMultipartFile("name", "originFileName", "image/jpg", new byte[]{});

    MockMultipartFile subImage1 = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    MockMultipartFile subImage2 = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    subFileList = List.of(subImage1, subImage2);

    requestDto = new CreatedGameRequestDto(
        "test",
        category.getId(),
        new BigDecimal("1000.00"),
        "test");

    game = Game.builder()
        .user(user)
        .title("testTitle")
        .category(category)
        .price(new BigDecimal("1000.00"))
        .description("testDescription")
        .status(GameStatus.ON_SAL)
        .imageUrl(mainFileDetail == null ? "" : mainFileDetail.getFilePath())
        .filePath(gameFileDetail.getFilePath())
        .build();
    gameRepository.save(game);
  }



  @Test
  @DisplayName("게임 생성")
  void createGame() {

    // Given
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    for (MultipartFile file : subFileList) {
      request.addFile(file);
    }

    List<MultipartFile> subImageList = List.of(request.getFiles("files").toArray(new MultipartFile[0]));

    // When
    GameResponseDto responseDto = gameService.createGame(user.getId(), requestDto, mainFile, subImageList, gameFile);

    // Then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getTitle()).isEqualTo("test");
    assertThat(responseDto.getCategoryId()).isEqualTo(category.getId());
    assertThat(responseDto.getPrice()).isEqualTo("1000.00");
    assertThat(gameRepository.findByIdOrElseThrow(game.getId()));
  }

  @Test
  @DisplayName("게임 생성 실패 : 중복 타이틀")
  void createGameFail() {

    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    for (MultipartFile file : subFileList) {
      request.addFile(file);
    }

    List<MultipartFile> subImageList = List.of(request.getFiles("files").toArray(new MultipartFile[0]));

    CreatedGameRequestDto requestDto1 = new CreatedGameRequestDto(
        "test",
        category.getId(),
        new BigDecimal("1000.00"),
        "test");

    CreatedGameRequestDto requestDto2 = new CreatedGameRequestDto(
        "test",
        category.getId(),
        new BigDecimal("3000.00"),
        "test123");

    gameService.createGame(user.getId(), requestDto1, mainFile, subImageList, gameFile);

    assertThrows(DuplicatedException.class, () -> gameService.createGame(user.getId(), requestDto2, mainFile, subImageList, gameFile));

  }

  @Test
  @DisplayName("게임 정보 수정")
  void updateGame() {

    // Given
    MockMultipartFile updateGameFile = new MockMultipartFile(
        "updateGameFile", "test.txt", "text/plain", "Hello, world!".getBytes()
    );
    MockMultipartFile updateMainFile = new MockMultipartFile(
        "updateMainFile", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    MockMultipartFile updateSubImage1 = new MockMultipartFile(
        "updateSubImage1", "test.txt", "text/plain", "Hello, world!".getBytes()
    );
    MockMultipartFile updateSubImage2 = new MockMultipartFile(
        "updateSubImage2", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    List<MultipartFile> subFileList = List.of(updateSubImage1, updateSubImage2);

    UpdatedGameRequestDto requestDto = new UpdatedGameRequestDto("updateTitle", category, new BigDecimal("5000.00"), "UpdateDescription");

    // When
    GameResponseDto responseDto = gameService.updateGame(game.getId(), user.getId(), requestDto, updateMainFile, subFileList, updateGameFile);

    // Then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getTitle()).isEqualTo(responseDto.getTitle());
  }

  @Test
  @DisplayName("유저 정보 수정 실패 : 잘못된 유저")
  void updateGame_UserFail() {

    // Given
    User user2 = User.builder()
        .email("test123@example.com")
        .password("encodedPassword123")
        .name("Test User2")
        .role(Role.USER)
        .social(Social.NORMAL)
        .imageUrl("test-image-url")
        .build();

    userRepository.save(user);

    UpdatedGameRequestDto requestDto = new UpdatedGameRequestDto("updateTitle2", category, new BigDecimal("5001.00"), "UpdateDescription");
    // When
    // Then
    assertThrows(
        NoAuthorizedException.class, () -> gameService.updateGame(game.getId(), user2.getId(), requestDto, mainFile, subFileList, gameFile)
    );

  }

}
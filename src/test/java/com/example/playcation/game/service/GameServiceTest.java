package com.example.playcation.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.playcation.category.entity.Category;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.game.dto.CreatedGameRequestDto;
import com.example.playcation.game.dto.GameResponseDto;
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
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

  @Mock
  private GameRepository gameRepository;

  @Mock
  private FileDetailRepository fileDetailRepository;

  @Mock
  private GameFileRepository gameFileRepository;

  @InjectMocks
  private GameService gameService;

  @InjectMocks
  private S3Service s3Service;

  private Category category;

  private FileDetail userFileDetail;

  private FileDetail mainFileDetail;

  private FileDetail gameFileDetail;

  private MultipartFile userFile;

  private MultipartFile mainFile;

  private MultipartFile gameFile;

  private List<MultipartFile> subFileList;

  private CreatedGameRequestDto requestDto;

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

    gameFile = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    mainFile = new MockMultipartFile(
        "file", "test.txt", "text/plain", "Hello, world!".getBytes()
    );

    category = Category.builder()
        .categoryName("testName")
        .build();

    mainFileDetail = s3Service.uploadFile(mainFile);
    gameFileDetail = s3Service.uploadFile(gameFile);

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
        1L,
        new BigDecimal("1000.00"),
        "test");
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

    // s3Service
    when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(mainFileDetail);
    when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(gameFileDetail);

    // gameService
    when(gameRepository.save(any(Game.class))).thenReturn(game);

    // When
    gameRepository.save(game);
    GameResponseDto responseDto = gameService.createGame(1L, requestDto, mainFile, subImageList, gameFile);

    // Then
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getTitle()).isEqualTo("test");
    assertThat(responseDto.getCategoryId()).isEqualTo(category.getId());
    assertThat(responseDto.getPrice()).isEqualTo(1000.00);
    assertThat(responseDto.getMainImagePath()).isEqualTo(mainFileDetail.getFilePath());
    assertThat(responseDto.getGameFilePath()).isEqualTo(gameFileDetail.getFilePath());

    verify(gameRepository, times(1)).save(any(Game.class)); // 게임 저장 검증
    verify(gameFileRepository, times(3)).save(any(GameFile.class)); // 파일 저장 검증
  }
}
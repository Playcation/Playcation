package com.example.playcation.game.service;


import com.example.playcation.common.PagingDto;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.exception.GameErrorCode;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.game.dto.CreatedGameRequestDto;
import com.example.playcation.game.dto.GameResponseDto;
import com.example.playcation.game.dto.PagingGameResponseDto;
import com.example.playcation.game.dto.UpdatedGameRequestDto;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.gametag.repository.GameTagRepository;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.s3.entity.FileDetail;
import com.example.playcation.s3.entity.GameFile;
import com.example.playcation.s3.repository.FileDetailRepository;
import com.example.playcation.s3.repository.GameFileRepository;
import com.example.playcation.s3.service.S3Service;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GameService {

  private final UserRepository userRepository;
  private final GameRepository gameRepository;
  private final LibraryRepository libraryRepository;
  private final GameTagRepository gameTagRepository;
  private final S3Service s3Service;
  private final GameFileRepository gameFileRepository;
  private final FileDetailRepository fileDetailRepository;
//  private final ReviewRepository reviewRepository;

  // 게임 생성
  @Transactional
  public GameResponseDto createGame(Long id,
      CreatedGameRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> subImageList, MultipartFile gameFile) {

    User user = userRepository.findByIdOrElseThrow(id);
    FileDetail mainFileDetail = s3Service.uploadFile(mainImage);
    FileDetail gameFileDetail = s3Service.uploadFile(gameFile);
    Game game = Game.builder()
        .user(user)
        .title(requestDto.getTitle())
        .category(requestDto.getCategory())
        .price(requestDto.getPrice())
        .description(requestDto.getDescription())
        .status(GameStatus.ON_SAL)
        .imageUrl(mainFileDetail == null ? "": mainFileDetail.getFilePath())
        .filePath(gameFileDetail.getFilePath())
        .build();

      gameRepository.save(game);

      List<FileDetail> subImageDetail = s3Service.uploadFiles(subImageList).join();
      List<String> subImagePathList = new ArrayList<>();
      for (FileDetail subImage : subImageDetail) {
        subImagePathList.add(subImage.getFilePath());
      }

      List<GameFile> gameFileList = subImageDetail.stream().map(subfileimage -> {return new GameFile(game, subfileimage, "subImage");}).toList();
      gameFileRepository.save(new GameFile(game, mainFileDetail, "mainImage"));
      gameFileRepository.save(new GameFile(game, gameFileDetail, "game"));
      gameFileRepository.saveAll(gameFileList);

    return GameResponseDto.toDto(game, subImagePathList);
  }

  // 게임 단건 조회
  public GameResponseDto findGameById(Long gameId) {
    Game game = gameRepository.findByIdOrElseThrow(gameId);
    List<GameFile> subImageList = gameFileRepository.findByGameIdAndBucket(gameId, "subImage");
    List<String> subImagePathList = new ArrayList<>();
    for (GameFile subImage : subImageList) {
      subImagePathList.add(subImage.getFileDetail().getFilePath());
    }
    return GameResponseDto.toDto(game, subImagePathList);
  }

  // 게임 다건 조회
  public PagingDto<GameResponseDto> searchGames(int page, String title, String category, BigDecimal price,
      LocalDateTime createdAt) {

    // 페이징시 최대 출력 갯수와 정렬조건 설정
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "id"));

    PagingGameResponseDto responseDto = gameRepository.searchGames(pageable, title, category, price, createdAt);

    List<Game> gameList = responseDto.getGameList();

    List<GameResponseDto> responseDtoList = createDto(gameList);

    // 위에서 for문을 돌려 만든 dtoList와 dsl에서 구한 count 반환
    return new PagingDto<>(responseDtoList, responseDto.getCount());
  }

  // 게임 수정
  @Transactional
  public GameResponseDto updateGame(Long gameId, Long userId,
      UpdatedGameRequestDto requestDto, MultipartFile mainImage, List<MultipartFile> subImageList,
      MultipartFile gameFile) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);

    FileDetail mainFileDetail = uploadFile(game, "image", mainImage);
    List<FileDetail> subFileDetailList = null;
    FileDetail gameFileDetail = uploadFile(game, "file", gameFile);

    List<GameFile> gameFileList = gameFileRepository.findByGameIdAndBucket(gameId, "subImage");

    for (MultipartFile subFile : subImageList) {

      if (gameFileList != null) {

        FileDetail uploadFileDetail = s3Service.uploadFile(subFile);

        subFileDetailList.add(uploadFileDetail);

        gameFileRepository.save(new GameFile(game, uploadFileDetail, "subImage"));

      }
    }

    List<FileDetail> files = new ArrayList<>();

    for (GameFile ordGameFile : gameFileList) {
      files.add(ordGameFile.getFileDetail());
    }

    for (FileDetail file : files) {
      s3Service.deleteFile(file.getFilePath());
    }

    gameFileRepository.deleteAll(gameFileList);
    fileDetailRepository.deleteAll(files);

  List<String> pathList = new ArrayList<>();
  for (FileDetail subFileDetail : subFileDetailList) {
    pathList.add(subFileDetail.getFilePath());
  }

    // 현재 접속한 유저가 게임을 생성한 유저가 맞는지 비교
    if (!game.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(GameErrorCode.DOES_NOT_MATCH);
    }

    fileDetailRepository.saveAll(subFileDetailList);
    game.updateGame(requestDto, mainFileDetail.getFilePath(), gameFileDetail.getFilePath());
    gameRepository.save(game);
    return GameResponseDto.toDto(game, pathList);
  }

  @Transactional
  public void deleteGame(Long gameId, Long userId) {

    Game game = gameRepository.findByIdOrElseThrow(gameId);

    // 현재 접속한 유저가 게임을 생성한 유저가 맞는지 비교
    if (!game.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(GameErrorCode.DOES_NOT_MATCH);
    }

    game.deleteGame();

    // 삭제하는 게임 id를 가지고 있는 게임 태그를 hard delete
    List<GameTag> gameTagList = gameTagRepository.findGameTagsByGameId(gameId);
    gameTagRepository.deleteAll(gameTagList);

    // 삭제하는 게임 id를 가지고 있는 라이브러리를 hard delete
    List<Library> libraryList = libraryRepository.findLibraryByGameId(gameId);
    libraryRepository.deleteAll(libraryList);

//    List<Review> reviewList = reviewRepository.findReviewByGame(game);
//    reviewRepository.deleteAll(reviewList);

    gameRepository.save(game);
  }

  public FileDetail uploadFile(Game game, String bucket, MultipartFile file) {
    FileDetail fileDetail = null;
    if (file ==null) {
      if (game.getFilePath().isEmpty()) {
        fileDetail = fileDetailRepository.findByFilePathOrElseThrow(game.getFilePath());
        gameFileRepository.deleteByGameIdAndFileDetailId(game.getId(), fileDetail.getId());
        s3Service.deleteFile(game.getFilePath());
      }
      fileDetail = new FileDetail();
    } else if (!file.isEmpty()) {
      FileDetail uploadMainFileDetail = s3Service.uploadFile(file);
      gameFileRepository.save(new GameFile(game, uploadMainFileDetail, bucket));
      if (!game.getFilePath().isEmpty()) {
        fileDetail = fileDetailRepository.findByFilePathOrElseThrow(game.getFilePath());
        gameFileRepository.deleteByGameIdAndFileDetailId(game.getId(), fileDetail.getId());
        s3Service.deleteFile(game.getFilePath());
      }
      fileDetail = uploadMainFileDetail;
    }
    return fileDetail;
  }

  // game이 가지고 있는 subImagePath를 구하여 dto를 만들어주는 메서드
  public List<GameResponseDto> createDto(List<Game> gameList) {

    List<GameResponseDto> responseDtoList = new ArrayList<>();

    for(Game game : gameList) {

      // 해당 게임의 id와 "subImage"라는 bucket을 가진 gameFileList 생성
      List<GameFile> gameFileList = gameFileRepository.findByGameIdAndBucket(game.getId(), "subImage");

      List<FileDetail> fileDetailList = new ArrayList<>();

      // gameFile을 돌려가며 fileDetail을 뽑음
      for (GameFile gameFile : gameFileList) {
        fileDetailList.add(gameFile.getFileDetail());
      }

      List<String> subImageUrl = new ArrayList<>();

      // 뽑아둔 fileDetailList의 filePath를 돌려가며 뽑음
      for(FileDetail fileDetail : fileDetailList) {
        subImageUrl.add(fileDetail.getFilePath());
      }

      // 뽑아둔 filePathList와 해당 회차의 game으로 dto 생성
      GameResponseDto dto = GameResponseDto.toDto(game, subImageUrl);

      //반환에 필요한 dtoList에 저장
      responseDtoList.add(dto);
    }
    return responseDtoList;
  }


}

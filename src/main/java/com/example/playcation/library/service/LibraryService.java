package com.example.playcation.library.service;

import com.example.playcation.common.PagingDto;
import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.exception.LibraryErrorCode;
import com.example.playcation.exception.NoAuthorizedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.library.dto.LibraryGameResponseDto;
import com.example.playcation.library.dto.LibraryListResponseDto;
import com.example.playcation.library.dto.LibraryRequestDto;
import com.example.playcation.library.dto.LibraryResponseDto;
import com.example.playcation.library.dto.UpdatedFavouriteRequestDto;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final LibraryRepository libraryRepository;

  // library 생성(중복되는 게임의 추가에 대한 에외처리는 앞서 카드에서 하였기 때문에 생략)
  @Transactional
  public LibraryResponseDto createLibrary(LibraryRequestDto requestDto, Long userId) {

    Game game = gameRepository.findByIdOrElseThrow(requestDto.getGameId());

    User user = userRepository.findByIdOrElseThrow(userId);

    Library library = Library.builder()
        .user(user)
        .game(game)
        .favourite(false)
        .build();

    libraryRepository.save(library);

    return LibraryResponseDto.toDto(library);
  }

  public LibraryResponseDto findLibrary(Long libraryId) {
    Library library = libraryRepository.findByIdOrElseThrow(libraryId);
    return LibraryResponseDto.toDto(library);
  }

  public PagingDto<LibraryGameResponseDto> findLibraryList(int page, Long userId) {

    PageRequest pageRequest = PageRequest.of(page, 10);

    User user = userRepository.findByIdOrElseThrow(userId);

    LibraryListResponseDto listDto = libraryRepository.findLibraryByUserId(pageRequest, user);

    List<Library> libraryList = listDto.getLibraryList();

    List<LibraryGameResponseDto> gameList = libraryList.stream()
        .map(ld -> new LibraryGameResponseDto(ld.getGame(), ld.getFavourite())).toList();

    return new PagingDto<>(gameList, listDto.getCount());

  }

  @Transactional
  public LibraryResponseDto updateFavourite(Long libraryId, UpdatedFavouriteRequestDto requestDto, Long userId) {
    Library library = libraryRepository.findByIdOrElseThrow(libraryId);

    // 접속한 유저가 수정하려는 라이브러리의 소유자가 맞는지 확인
    if (!library.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(LibraryErrorCode.CANNOT_BE_MODIFIED_LIBRARY);
    }

    // 유저가 바꾸려는 즐겨찾기 상태와 현재 라이브러리의 즐겨찾기의 상태가 같은지 확인
    if (library.getFavourite() == requestDto.isFavourite()) {
      throw new NoAuthorizedException(LibraryErrorCode.INVALID_INPUT_LIBRARY);
    }

    library.updateFavourite(requestDto.isFavourite());
    libraryRepository.save(library);

    return LibraryResponseDto.toDto(library);
  }


  @Transactional
  public void deleteLibrary(Long libraryId, Long userId) {
    Library library = libraryRepository.findByIdOrElseThrow(libraryId);

    if (!library.getUser().getId().equals(userId)) {
      throw new NoAuthorizedException(LibraryErrorCode.CANNOT_BE_MODIFIED_LIBRARY);
    }

    libraryRepository.delete(library);
  }
}

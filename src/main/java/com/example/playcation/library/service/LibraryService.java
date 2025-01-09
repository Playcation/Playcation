package com.example.playcation.library.service;

import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.gametag.dto.GameListResponseDto;
import com.example.playcation.library.dto.LibraryGameResponseDto;
import com.example.playcation.library.dto.LibraryListResponseDto;
import com.example.playcation.library.dto.LibraryRequestDto;
import com.example.playcation.library.dto.LibraryResponseDto;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final LibraryRepository libraryRepository;

  // library 생성(중복되는 게임의 추가에 대한 에외처리는 앞서 카드에서 하였기 때문에 생략)
  public LibraryResponseDto createLibrary(LibraryRequestDto requestDto, Long userId) {

    Game game = gameRepository.findByIdOrElseThrow(requestDto.getGameId());

    User user = userRepository.findByIdOrElseThrow(userId);

    Library library = Library.builder()
        .user(user)
        .game(game)
        .isFavourite(false)
        .build();

    libraryRepository.save(library);

    return LibraryResponseDto.toDto(library);
  }

  public LibraryResponseDto findLibrary(Long libraryId) {
    Library library = libraryRepository.findByIdOrElseThrow(libraryId);
    return LibraryResponseDto.toDto(library);
  }

  public List<LibraryGameResponseDto> findLibraryList(int page, Long userId) {

    PageRequest pageRequest = PageRequest.of(page, 10);

    User user = userRepository.findByIdOrElseThrow(userId);

    LibraryListResponseDto listDto = libraryRepository.findLibraryByUserId(pageRequest, user);

    List<Library> libraryList = listDto.getLibraryList();

    List<LibraryGameResponseDto> gameList = new ArrayList<>();
    for (Library library : libraryList) {
      gameList.add(new LibraryGameResponseDto(library.getGame(), library.getIsFavourite(), listDto.getCount()));
    }

    return gameList;

  }
}

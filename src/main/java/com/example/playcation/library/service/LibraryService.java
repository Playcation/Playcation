package com.example.playcation.library.service;

import com.example.playcation.exception.DuplicatedException;
import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.library.dto.LibraryRequestDto;
import com.example.playcation.library.dto.LibraryResponseDto;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
}

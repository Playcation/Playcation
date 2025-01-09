package com.example.playcation.library.service;

import com.example.playcation.game.entity.Game;
import com.example.playcation.game.repository.GameRepository;
import com.example.playcation.library.dto.LibraryRequestDto;
import com.example.playcation.library.dto.LibraryResponseDto;
import com.example.playcation.library.entity.Library;
import com.example.playcation.library.repository.LibraryRepository;
import com.example.playcation.user.entity.User;
import com.example.playcation.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final LibraryRepository libraryRepository;

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

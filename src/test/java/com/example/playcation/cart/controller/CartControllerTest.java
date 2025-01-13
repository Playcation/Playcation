package com.example.playcation.cart.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.playcation.cart.dto.CartGameResponseDto;
import com.example.playcation.cart.dto.UpdatedCartGameResponseDto;
import com.example.playcation.cart.service.CartService;
import com.example.playcation.enums.GameStatus;
import com.example.playcation.enums.Role;
import com.example.playcation.enums.Social;
import com.example.playcation.game.entity.Game;
import com.example.playcation.user.entity.User;
import com.example.playcation.util.JWTUtil;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
class CartControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CartService cartService;

  @Mock
  private JWTUtil jwtUtil;

  private String authorizationHeader;

  @BeforeEach
  void setUp() {

    authorizationHeader = "Bearer habinhahahaha"; // Mock Authorization 헤더

  }

  /**
   * 회원의 장바구니 게임을 전체 조회 테스트
   */
  @Test
  void testFindCartItems() throws Exception {
    Long userId = 1L;
    List<CartGameResponseDto> cartItems = List.of(
        new CartGameResponseDto(1L, "image1.png", "Game 1", BigDecimal.valueOf(10000)),
        new CartGameResponseDto(2L, "image2.png", "Game 2", BigDecimal.valueOf(20000))
    );

    given(jwtUtil.findUserByToken(authorizationHeader)).willReturn(userId);
    given(cartService.findCartItems(userId)).willReturn(cartItems);

    mockMvc.perform(get("/carts")
            .header("Authorization", authorizationHeader))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Game 1"))
        .andExpect(jsonPath("$[0].image").value("image1.png"))
        .andExpect(jsonPath("$[0].price").value(10000.00))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].title").value("Game 2"))
        .andExpect(jsonPath("$[1].image").value("image2.png"))
        .andExpect(jsonPath("$[1].price").value(20000.00));

    verify(jwtUtil).findUserByToken(authorizationHeader);
    verify(cartService).findCartItems(userId);
  }

  /**
   * 장바구니에 게임 추가 테스트
   */
  @Test
  void testAddGameToCart() throws Exception {
    Long userId = 1L, gameId = 1L;
    User user = new User("a@a.com", "q1w2e3r4!!", "jina", Role.USER, Social.NORMAL);
    Game game = new Game(user, "Game1", "rpg", BigDecimal.valueOf(10000), "game1 description",
        GameStatus.ON_SAL, "iamge1.png");

    UpdatedCartGameResponseDto responseDto = new UpdatedCartGameResponseDto(1L, userId,
        gameId);

    given(jwtUtil.findUserByToken(authorizationHeader)).willReturn(userId);
    given(cartService.addGameToCart(userId, gameId, authorizationHeader)).willReturn(
        responseDto);

    mockMvc.perform(post("/carts/add/{gameId}", gameId)
            .header("Authorization", authorizationHeader))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.cartId").value(1L))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.gameId").value(gameId));

    verify(jwtUtil).findUserByToken(authorizationHeader);
    verify(cartService).addGameToCart(userId, gameId, authorizationHeader);
  }

  /**
   * 장바구니에서 특정 게임 삭제 테스트
   */
  @Test
  void testDeleteGameFromCart() throws Exception {
    Long userId = 1L;
    Long gameId = 1L;

    given(jwtUtil.findUserByToken(authorizationHeader)).willReturn(userId);

    mockMvc.perform(delete("/carts/delete/{gameId}", gameId)
            .header("Authorization", authorizationHeader))
        .andExpect(status().isOk())
        .andExpect(content().string("게임 삭제 완료"));

    verify(jwtUtil).findUserByToken(authorizationHeader);
  }
}
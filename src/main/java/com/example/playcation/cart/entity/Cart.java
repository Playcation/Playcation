package com.example.playcation.cart.entity;

import com.example.playcation.game.entity.Game;
import com.example.playcation.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`cart`")
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany
  @JoinColumn(name = "cart_id") // 외래 키
  private List<Game> games; // Game과의 관계
  
  public static Cart createCart(User user) {
    Cart cart = new Cart();
    cart.user = user;

    return cart;
  }

}

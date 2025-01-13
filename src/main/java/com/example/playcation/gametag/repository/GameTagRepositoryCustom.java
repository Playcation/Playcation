package com.example.playcation.gametag.repository;

import com.example.playcation.gametag.dto.GameTagListResponseDto;
import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.tag.entity.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface GameTagRepositoryCustom {
  GameTagListResponseDto findGameTagByTag(Pageable pageable, Tag tag);
}

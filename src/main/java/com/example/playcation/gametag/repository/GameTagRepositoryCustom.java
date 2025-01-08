package com.example.playcation.gametag.repository;

import com.example.playcation.gametag.entity.GameTag;
import com.example.playcation.tag.entity.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public interface GameTagRepositoryCustom {
  List<GameTag> findGameTagByTag(PageRequest pageRequest, Tag tag);
}

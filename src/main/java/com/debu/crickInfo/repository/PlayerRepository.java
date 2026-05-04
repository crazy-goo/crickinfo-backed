package com.debu.crickInfo.repository;

import com.debu.crickInfo.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player, String> {
    // add custom queries if needed
}

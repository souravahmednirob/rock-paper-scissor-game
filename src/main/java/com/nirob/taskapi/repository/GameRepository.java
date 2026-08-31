package com.nirob.taskapi.repository;

import com.nirob.taskapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllByOrderByCreatedAtDesc();
}
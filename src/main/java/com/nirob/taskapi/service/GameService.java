package com.nirob.taskapi.service;

import com.nirob.taskapi.model.Game;
import com.nirob.taskapi.model.Move;
import com.nirob.taskapi.model.Result;
import com.nirob.taskapi.model.Round;
import com.nirob.taskapi.repository.GameRepository;
import com.nirob.taskapi.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final Random random = new Random();

    @Transactional
    public Game startNewGame(String playerName) {
        String name = (playerName == null || playerName.isBlank()) ? "Player" : playerName.trim();
        Game game = Game.builder()
                .playerName(name)
                .playerScore(0)
                .computerScore(0)
                .draws(0)
                .createdAt(LocalDateTime.now())
                .build();
        return gameRepository.save(game);
    }

    @Transactional
    public Round playRound(Long gameId, Move playerMove) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        Move computerMove = randomMove();
        Result result = determineResult(playerMove, computerMove);

        Round round = Round.builder()
                .game(game)
                .playerMove(playerMove)
                .computerMove(computerMove)
                .result(result)
                .playedAt(LocalDateTime.now())
                .build();

        switch (result) {
            case WIN -> game.setPlayerScore(game.getPlayerScore() + 1);
            case LOSE -> game.setComputerScore(game.getComputerScore() + 1);
            case DRAW -> game.setDraws(game.getDraws() + 1);
        }

        game.getRounds().add(round);
        gameRepository.save(game);
        return round;
    }

    @Transactional(readOnly = true)
    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
    }

    @Transactional(readOnly = true)
    public List<Game> getAllGames() {
        return gameRepository.findAllByOrderByCreatedAtDesc();
    }

    private Move randomMove() {
        Move[] moves = Move.values();
        return moves[random.nextInt(moves.length)];
    }

    private Result determineResult(Move player, Move computer) {
        if (player == computer) return Result.DRAW;
        return switch (player) {
            case ROCK -> computer == Move.SCISSORS ? Result.WIN : Result.LOSE;
            case PAPER -> computer == Move.ROCK ? Result.WIN : Result.LOSE;
            case SCISSORS -> computer == Move.PAPER ? Result.WIN : Result.LOSE;
        };
    }
}
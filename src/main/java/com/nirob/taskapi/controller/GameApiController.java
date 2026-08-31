package com.nirob.taskapi.controller;

import com.nirob.taskapi.model.Game;
import com.nirob.taskapi.model.Move;
import com.nirob.taskapi.model.Round;
import com.nirob.taskapi.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameApiController {

    private final GameService gameService;

    @PostMapping("/{id}/play")
    public Map<String, Object> play(@PathVariable Long id, @RequestParam Move move) {
        Round round = gameService.playRound(id, move);
        Game game = gameService.getGame(id);
        return Map.of(
                "playerMove", round.getPlayerMove().name(),
                "computerMove", round.getComputerMove().name(),
                "result", round.getResult().name(),
                "playerScore", game.getPlayerScore(),
                "computerScore", game.getComputerScore(),
                "draws", game.getDraws()
        );
    }
}
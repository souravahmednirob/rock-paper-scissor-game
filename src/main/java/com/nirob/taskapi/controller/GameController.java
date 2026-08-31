package com.nirob.taskapi.controller;

import com.nirob.taskapi.model.Game;
import com.nirob.taskapi.model.Move;
import com.nirob.taskapi.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/games/start")
    public String startGame(@RequestParam(value = "playerName", required = false) String playerName) {
        Game game = gameService.startNewGame(playerName);
        return "redirect:/games/" + game.getId();
    }

    @GetMapping("/games/{id}")
    public String viewGame(@PathVariable Long id, Model model) {
        Game game = gameService.getGame(id);
        model.addAttribute("game", game);
        model.addAttribute("moves", Move.values());
        return "game";
    }

    @PostMapping("/games/{id}/play")
    public String play(@PathVariable Long id, @RequestParam Move move) {
        gameService.playRound(id, move);
        return "redirect:/games/" + id;
    }

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        return "history";
    }
}
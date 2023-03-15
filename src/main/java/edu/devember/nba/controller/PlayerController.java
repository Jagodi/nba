package edu.devember.nba.controller;

import edu.devember.nba.model.Player;
import edu.devember.nba.service.PlayerService;
import edu.devember.nba.util.NonValidJsonFormException;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getPlayers() {
        return playerService.findAllPlayers();
    }

    @GetMapping("/{playerId}")
    public Player getPlayer(@PathVariable("playerId") int playerId) {
        return playerService.findPlayerById(playerId);
    }

    @PostMapping
    public Player addPlayer(@RequestBody @Valid Player thePlayer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new NonValidJsonFormException(bindingResult);
        playerService.save(thePlayer);
        return thePlayer;
    }

    @PutMapping("/{playerId}")
    public Player updatePlayer(@PathVariable("playerId") int playerId, @RequestBody Player thePlayer) {

        Player player = playerService.findPlayerById(playerId);
        if (thePlayer.getName() != null) player.setName(thePlayer.getName());
        player.setNumber(thePlayer.getNumber());
        if (thePlayer.getFrom() != null) player.setFrom(thePlayer.getFrom());
        if (thePlayer.getPosition() != null) player.setPosition(thePlayer.getPosition());
        if (thePlayer.getDateOfBirth() != null) player.setDateOfBirth(thePlayer.getDateOfBirth());

        return playerService.save(player);
    }

    @DeleteMapping("/{playerId}")
    public String deletePlayer(@PathVariable("playerId") int playerId) {

        Player player = playerService.findPlayerById(playerId);
        if (player.getTeam() != null) {
            player.getTeam().removePlayer(player);
        }
        playerService.delete(playerId);
        return "Deleted player with id - " + playerId;
    }
}

package edu.devember.nba.controller;

import edu.devember.nba.model.Player;
import edu.devember.nba.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return playerService.findAll();
    }

    @GetMapping("/players/{playerId}")
    public Player getPlayer(@PathVariable("playerId") int playerId) {
        return playerService.findPlayerById(playerId);
    }

    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player thePlayer) {
        return playerService.save(thePlayer);
    }

    @PutMapping("/players/{playerId}")
    public Player updatePlayer(@PathVariable("playerId") int playerId, @RequestBody Player thePlayer) {

        Player player = playerService.getReferenceById(playerId);
        player.setName(thePlayer.getName());
        player.setNumber(thePlayer.getNumber());
        player.setFrom(thePlayer.getFrom());
        player.setPosition(thePlayer.getPosition());
        player.setDateOfBirth(thePlayer.getDateOfBirth());
        return playerService.save(player);
    }

    @DeleteMapping("/players/{playerId}")
    public String deletePlayer(@PathVariable("playerId") int playerId) {

        Player player = playerService.findPlayerById(playerId);
        if (player.getTeam() != null) {
            player.getTeam().removePlayer(player);
        }
        playerService.delete(playerId);
        return "Deleted player with id - " + playerId;
    }
}

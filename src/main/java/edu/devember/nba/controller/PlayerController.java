package edu.devember.nba.controller;

import edu.devember.nba.model.Player;
import edu.devember.nba.model.Team;
import edu.devember.nba.service.PlayerService;
import edu.devember.nba.service.TeamService;
import edu.devember.nba.util.TeamNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlayerController {

    private final PlayerService playerService;

    private final TeamService teamService;

    public PlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping("/players")
    public List<Player> listPlayers() {
        return playerService.findAll();
    }

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable("id") int theId) {
        return playerService.findById(theId);
    }

    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player thePlayer) {
        thePlayer.setId(0);
        return playerService.save(thePlayer);
    }

    @PutMapping("/players")
    public Player updatePlayer(@RequestBody Player thePlayer) {
        return playerService.save(thePlayer);
    }

    @DeleteMapping("/players/{id}")
    public String deletePlayer(@PathVariable("id") int theId) {

        playerService.delete(theId);
        return "Deleted player with id - " + theId;
    }



    @GetMapping("/teams/{teamId}/players")
    public List<Player> getAllPlayersFromTheTeam(@PathVariable("teamId") int theId) {
        if (!teamService.existsById(theId)) throw new TeamNotFoundException();

        return playerService.findByTeamId(theId);
    }

    @GetMapping("/teams/{teamId}/players/{playerId}")
    public Player getPlayerFromTheTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId) {
        if (!teamService.existsById(teamId)) throw new TeamNotFoundException();

        return playerService.findById(playerId);
    }

    @PostMapping("/teams/{teamId}/players")
    public Player addPlayerToTheTeam(@PathVariable("teamId") int theId, @RequestBody Player thePlayer) {
        Team team = teamService.findById(theId);
        team.addPlayers(thePlayer);
        return playerService.save(thePlayer);
    }

    @PutMapping("/teams/{teamId}/players/{playerId}")
    public Player updatePlayerInTheTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId, @RequestBody Player thePlayer) {
        if (!teamService.existsById(teamId)) throw new TeamNotFoundException();

        Player player = playerService.findById(playerId);
        player.setName(thePlayer.getName());
        player.setNumber(thePlayer.getNumber());
        player.setFrom(thePlayer.getFrom());
        player.setPosition(thePlayer.getPosition());
        player.setDateOfBirth(thePlayer.getDateOfBirth());

        return playerService.save(player);
    }

    @DeleteMapping("/teams/{teamId}/players/{playerId}")
    public String deletePlayerFromTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId) {
        if (!teamService.existsById(teamId)) throw new TeamNotFoundException();

        Team team = teamService.findById(teamId);
        team.removePlayer(playerService.findById(playerId));
        playerService.delete(playerId);

        return "Deleted player with id - " + playerId;
    }

}

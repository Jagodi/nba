package edu.devember.nba.controller;

import edu.devember.nba.model.Player;
import edu.devember.nba.model.Team;
import edu.devember.nba.service.PlayerService;
import edu.devember.nba.service.TeamService;
import edu.devember.nba.util.NonValidJsonFormException;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    private final PlayerService playerService;

    public TeamController(TeamService teamService, PlayerService playerService) {
        this.teamService = teamService;
        this.playerService = playerService;
    }

    @GetMapping
    public List<Team> getTeams() {
        return teamService.findAllTeams();
    }

    @GetMapping("/{teamId}")
    public Team getTeam(@PathVariable("teamId") int teamId) {
        return teamService.findTeamById(teamId);
    }

    @PostMapping
    public Team addTeam(@RequestBody @Valid Team theTeam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new NonValidJsonFormException(bindingResult);
        teamService.save(theTeam);
        return theTeam;
    }

    @PutMapping("/{teamId}")
    public Team updateTeam(@PathVariable("teamId") int teamId, @RequestBody Team theTeam) {
        Team team = teamService.findTeamById(teamId);
        if (theTeam.getTeamName() != null) team.setTeamName(theTeam.getTeamName());
        if (theTeam.getCity() != null) team.setCity(theTeam.getCity());
        if (theTeam.getStadium() != null) team.setStadium(theTeam.getStadium());
        if (theTeam.getFounded() != null) team.setFounded(theTeam.getFounded());
        return teamService.save(team);
    }

    @DeleteMapping("/{teamId}")
    public String deleteTeam(@PathVariable("teamId") int teamId) {
        Team team = teamService.findTeamById(teamId);
        if (!team.getPlayers().isEmpty()) {
            for (Player player : team.getPlayers()) {
                team.removePlayer(player);
            }
        }
        teamService.delete(teamId);
        return "Deleted team with id - " + teamId;
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public String addPlayerToTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId) {

        Team team = teamService.findTeamById(teamId);
        Player player = playerService.findPlayerById(playerId);
        team.addPlayers(player);
        teamService.save(team);
        return "Player with id - " + player.getId() + " added to team " + team.getTeamName();
    }

    @GetMapping("/{teamId}/players")
    public List<Player> getAllPlayersFromTeam(@PathVariable("teamId") int teamId) {
        teamService.existsById(teamId);

        return playerService.findByTeamId(teamId);
    }

    @PostMapping("/{teamId}/players")
    public Player addNewPlayerToTeam(@PathVariable("teamId") int teamId, @RequestBody Player thePlayer) {
        Team team = teamService.findTeamById(teamId);
        team.addPlayers(thePlayer);
        return playerService.save(thePlayer);
    }

    @PutMapping("/{teamId}/players/{playerId}")
    public String addExistingPlayerToTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId) {
        Team team = teamService.findTeamById(teamId);
        Player player = playerService.findPlayerById(playerId);

        if (player.getTeam() != null) {
            player.getTeam().removePlayer(player);
        }

        team.addPlayers(player);
        teamService.save(team);

        return player.getName() + " was added to team " + team.getTeamName();
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public String deletePlayerFromTeam(@PathVariable("teamId") int teamId, @PathVariable("playerId") int playerId) {
        teamService.existsById(teamId);

        Team team = teamService.findTeamById(teamId);
        Player player = playerService.findPlayerById(playerId);
        team.removePlayer(player);
        teamService.save(team);

        return player.getName() + " was deleted from " + team.getTeamName();
    }

}

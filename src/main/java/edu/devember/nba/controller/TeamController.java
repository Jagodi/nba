package edu.devember.nba.controller;

import edu.devember.nba.model.Team;
import edu.devember.nba.service.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<Team> listTeams() {
        return teamService.findAll();
    }

    @GetMapping("/{id}")
    public Team getTeam(@PathVariable("id") int id) {
        return teamService.findById(id);
    }

}

package edu.devember.nba.controller;

import edu.devember.nba.model.Team;
import edu.devember.nba.service.TeamService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Team addTeam(@RequestBody Team theTeam) {

        theTeam.setId(0);
        teamService.save(theTeam);
        return theTeam;
    }

    @PutMapping
    public Team updateTeam(@RequestBody Team theTeam) {
        return teamService.save(theTeam);
    }

    @DeleteMapping("/{id}")
    public String deleteTeam(@PathVariable("id") int theId) {
        teamService.delete(theId);
        return "Deleted team with id - " + theId;
    }

}

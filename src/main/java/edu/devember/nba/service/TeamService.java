package edu.devember.nba.service;

import edu.devember.nba.model.Team;
import edu.devember.nba.repository.TeamRepository;
import edu.devember.nba.util.TeamNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> findAllTeams() {
        List<Team> team = teamRepository.findAll();
        if (team.isEmpty()) throw new TeamNotFoundException();
        return team;
    }

    public Team findTeamById(int theId) {
        Optional<Team> optionalTeam = teamRepository.findById(theId);
        return optionalTeam.orElseThrow(TeamNotFoundException::new);
    }

    @Transactional
    public Team save(Team theTeam) {
        return teamRepository.save(theTeam);
    }

    @Transactional
    public void delete(int theId) {
        Team team = findTeamById(theId);
        teamRepository.delete(team);
    }

    public boolean existsById(int teamId) {
        boolean isTeamExist = teamRepository.existsById(teamId);
        if (!isTeamExist) throw new TeamNotFoundException();
        return teamRepository.existsById(teamId);
    }

}

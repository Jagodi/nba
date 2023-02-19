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

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Team findById(int id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        return optionalTeam.orElseThrow(TeamNotFoundException::new);
    }
}

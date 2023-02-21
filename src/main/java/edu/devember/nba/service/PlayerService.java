package edu.devember.nba.service;

import edu.devember.nba.model.Player;
import edu.devember.nba.repository.PlayerRepository;
import edu.devember.nba.util.PlayerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player findById(int theId) {
        Optional<Player> optionalPlayer = playerRepository.findById(theId);
        return optionalPlayer.orElseThrow(PlayerNotFoundException::new);
    }

    @Transactional
    public Player save(Player thePlayer) {
        return playerRepository.save(thePlayer);
    }

    @Transactional
    public void delete(int theId) {
        Player player = findById(theId);
        playerRepository.delete(player);
    }

    public List<Player> findByTeamId(int theId) {
        return playerRepository.findByTeamId(theId);
    }
}

package edu.devember.nba.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotEmpty(message = "teamName should not be empty")
    @Column(name = "team_name", unique = true)
    private String teamName;

    @NotEmpty(message = "city should not be empty")
    @Column(name = "city")
    private String city;

    @NotEmpty(message = "stadium should not be empty")
    @Column(name = "stadium")
    private String stadium;

    @Column(name = "founded")
    @Temporal(TemporalType.DATE)
    private LocalDate founded;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonManagedReference
    private List<Player> players = new ArrayList<>();

    public void addPlayers(Player thePlayer) {
        players.add(thePlayer);
        thePlayer.setTeam(this);
    }

    public void removePlayer(Player thePlayer) {
        players.remove(thePlayer);
        thePlayer.setTeam(null);
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamName='" + teamName + '\'' +
                ", city='" + city + '\'' +
                ", stadium='" + stadium + '\'' +
                ", founded=" + founded +
                ", players=" + players +
                '}';
    }
}

package edu.devember.nba.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @NotEmpty(message = "name should not be empty")
    @Column(name = "player_name")
    private String name;

    @NotEmpty(message = "number should not be empty")
    @Digits(fraction = 0, integer = 2)
    @Column(name = "player_number")
    private int number;

    @NotEmpty(message = "position should not be empty")
    @Column(name = "position")
    private String position;

    @NotEmpty(message = "from should not be empty")
    @Column(name = "university_or_country")
    private String from;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;

}

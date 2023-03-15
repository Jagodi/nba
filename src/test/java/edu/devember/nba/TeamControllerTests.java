package edu.devember.nba;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.devember.nba.model.Player;
import edu.devember.nba.model.Team;
import edu.devember.nba.service.PlayerService;
import edu.devember.nba.service.TeamService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeamControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TeamService teamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    Team team;

    @Autowired
    Player player;

    @Value("${sql.script.create.teams}")
    private String sqlAddTeams;

    @Value("${sql.script.delete.teams}")
    private String sqlDeleteTeams;

    @Value("${sql.script.delete.players}")
    private String sqlDeletePlayers;

    @Value("${sql.script.reset.players.autoincrement}")
    private String sqlResetPlayersAutoIncrement;

    @Value("${sql.script.create.players}")
    private String sqlAddPlayers;

    @Value("${sql.script.create.player.to.team}")
    private String sqlAddPlayerToTeam;


    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddTeams);
        jdbc.execute(sqlAddPlayers);
    }

    @Test
    @DisplayName("Get All Teams")
    @Order(1)
    void getAllTeams() throws Exception {
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(11)))
                .andExpect(jsonPath("$[0].teamName", is("Detroit Pistons")))
                .andExpect(jsonPath("$[1].id", is(12)))
                .andExpect(jsonPath("$[1].teamName", is("Atlanta Hawks")));
    }

    @Test
    @DisplayName("If DB is Empty")
    @Order(2)
    void getEmptyListOfTeams() throws Exception {

        jdbc.execute("delete from teams");

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Oops. Teams list is empty!")));
    }

    @Test
    @DisplayName("Get One Team by Id")
    @Order(3)
    void getOneTeamById() throws Exception {
        mockMvc.perform(get("/api/teams/{teamId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(13)))
                .andExpect(jsonPath("$.teamName", is("Los Angeles Lakers")))
                .andExpect(jsonPath("$.city", is("Los Angeles")))
                .andExpect(jsonPath("$.stadium", is("Staples Center")))
                .andExpect(jsonPath("$.founded", is("1947-01-01")));
    }

    @Test
    @DisplayName("If Team by Id Not Found")
    @Order(4)
    void getANonValidTeamById() throws Exception {
        mockMvc.perform(get("/api/teams/{teamId}", 4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Team with this id wasn`t found")));
    }

    @Test
    @DisplayName("Add a New Team")
    @Order(5)
    void addNewTeam() throws Exception {
        team.setTeamName("Philadelphia 76ers");
        team.setCity("Philadelphia");
        team.setStadium("Wells Fargo Center");
        team.setFounded(LocalDate.of(1946, 1, 1));

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk());

        Team verifyTeam = teamService.findTeamById(1);
        assertNotNull(verifyTeam, "Team should be valid");
    }

    @Test
    @DisplayName("Update a Team")
    @Order(6)
    void updateTeam() throws Exception {
        team.setStadium("Crypto.com Arena");

        // 13 - LA`s id
        mockMvc.perform(put("/api/teams/{teamId}", 13)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk());

        Team tmpTeam = teamService.findTeamById(13);
        assertEquals("Crypto.com Arena", tmpTeam.getStadium());
        System.out.println(tmpTeam);
    }

    @Test
    @DisplayName("If Not Found Team For Update")
    @Order(7)
    void updateANonValidTeamById() throws Exception {
        team.setTeamName("Chicago Bulls");
        team.setCity("Chicago");
        team.setStadium("United Center");
        team.setFounded(LocalDate.of(1966, 1, 1));

        // 22 id is not represented in the db
        mockMvc.perform(put("/api/teams/{teamId}", 22)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Team with this id wasn`t found")));

    }

    @Test
    @DisplayName("Delete a Team")
    @Order(8)
    void deleteOneTeamById() throws Exception {

        mockMvc.perform(delete("/api/teams/{teamId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deleted team with id - " + 13)));
    }

    @Test
    @DisplayName("Get All Players From Team")
    @Order(9)
    void getAllPlayersFromTeam() throws Exception {

        player.setName("Dejounte Murray");
        player.setNumber(5);
        player.setPosition("G");
        player.setFrom("Washington");
        player.setDateOfBirth(LocalDate.of(1996, 9, 19));

        team = teamService.findTeamById(12);
        team.addPlayers(player);
        entityManager.persist(team);
        entityManager.flush();

        mockMvc.perform(get("/api/teams/{teamId}/players", 12))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Dejounte Murray")));

        team.removePlayer(player);
        entityManager.persist(team);
        entityManager.flush();

    }

    @Test
    @DisplayName("If Team Has Not Any Players")
    @Order(10)
    void getEmptyListOfPlayersFromTeam() throws Exception {

        mockMvc.perform(get("/api/teams/{teamId}/players", 12))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Oops. Players list is empty!")));
    }

    @Test
    @DisplayName("Add a New Player To Team")
    @Order(11)
    void addNewPlayerToTeam() throws Exception {

        player.setName("Dejounte Murray");
        player.setNumber(5);
        player.setPosition("G");
        player.setFrom("Washington");
        player.setDateOfBirth(LocalDate.of(1996, 9, 19));

        mockMvc.perform(post("/api/teams/{teamId}/players", 12)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isOk());

        Player tmpPlayer = playerService.findPlayerById(1);
        assertEquals("Dejounte Murray", tmpPlayer.getName());

        team = teamService.findTeamById(12);
        assertFalse(team.getPlayers().isEmpty());

        team.removePlayer(tmpPlayer);
        entityManager.persist(team);
        entityManager.flush();
    }

    @Test
    @DisplayName("Add an Existing Player To Team")
    @Order(12)
    void addFreeAndExistingPlayerToTeam() throws Exception {

        team = teamService.findTeamById(12);
        player = playerService.findPlayerById(11);

        assertNull(player.getTeam());

        mockMvc.perform(put("/api/teams/{teamId}/players/{playerId}", 12, 11))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(player.getName() + " was added to team " + team.getTeamName())));

        assertEquals("Trae Young", team.getPlayers().get(0).getName());
    }

    @Test
    @DisplayName("Add an Existing PLayer to Team If He is Not Free")
    @Order(13)
    void addNotFreeAndExistingPlayerToTeam() throws Exception {

        jdbc.execute(sqlAddPlayerToTeam);
        team = teamService.findTeamById(13);
        player = playerService.findPlayerById(14);

        assertEquals("Atlanta Hawks", player.getTeam().getTeamName());

        mockMvc.perform(put("/api/teams/{teamId}/players/{playerId}", 13, 14))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(player.getName() + " was added to team " + team.getTeamName())));

        assertEquals("Los Angeles Lakers", player.getTeam().getTeamName());

    }

    @Test
    @DisplayName("Delete Player From a Team")
    @Order(14)
    void deletePlayerFromTeam() throws Exception {

        jdbc.execute(sqlAddPlayerToTeam);
        team = teamService.findTeamById(12);
        player = playerService.findPlayerById(14);

        assertEquals("Atlanta Hawks", player.getTeam().getTeamName());

        mockMvc.perform(delete("/api/teams/{teamId}/players/{playerId}", 12, 14))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(player.getName() + " was deleted from " + team.getTeamName())));

        assertNull(player.getTeam());
    }

    @AfterEach
    public void setupUpAfterTransaction() {
        jdbc.execute(sqlDeletePlayers);
        jdbc.execute(sqlDeleteTeams);
        jdbc.execute(sqlResetPlayersAutoIncrement);
        team = null;
        player = null;
    }
}

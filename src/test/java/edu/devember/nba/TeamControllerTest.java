package edu.devember.nba;


import com.fasterxml.jackson.databind.ObjectMapper;
import edu.devember.nba.model.Team;
import edu.devember.nba.service.TeamService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TeamService teamService;

    @Autowired
    Team team;

    @Value("${sql.script.create.team}")
    private String sqlAddTeam;

    @Value("${sql.script.delete.team}")
    private String sqlDeleteTeams;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddTeam);
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
    @DisplayName("Get One Team by Id")
    @Order(2)
    void getOneTeamById() throws Exception {
        mockMvc.perform(get("/api/teams/{teamsId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(13)))
                .andExpect(jsonPath("$.teamName", is("Los Angeles Lakers")))
                .andExpect(jsonPath("$.city", is("Los Angeles")))
                .andExpect(jsonPath("$.stadium", is("Crypto.com Arena")))
                .andExpect(jsonPath("$.founded", is("1947-01-01")));
    }

    @Test
    @DisplayName("If Team by Id Not Found")
    @Order(3)
    void getANonValidTeamById() throws Exception {
        mockMvc.perform(get("/api/teams/{teamId}", 4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Team with this id wasn`t found")));
    }

    @Test
    @DisplayName("Add One New Team")
    @Order(4)
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

    // Добавить Post test при одинковых названиях команд

    @Test
    @DisplayName("Update a Team")
    @Order(5)
    void updateTeam() throws Exception {
        team.setTeamName("Chicago Bulls");
        team.setCity("Chicago");
        team.setStadium("United Center");
        team.setFounded(LocalDate.of(1966, 1, 1));

        // 13 - LA`s id
        mockMvc.perform(put("/api/teams/{teamId}", 13)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk());

        Team tmpTeam = teamService.findTeamById(13);
        assertEquals("Chicago Bulls", tmpTeam.getTeamName());
    }

    @Test
    @DisplayName("If Not Found Team For Update")
    @Order(6)
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
    @Order(7)
    void deleteOneTeamById() throws Exception {

        mockMvc.perform(delete("/api/teams/{teamId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)));
    }

    @AfterEach
    public void setupUpAfterTransaction() {
        jdbc.execute(sqlDeleteTeams);
    }
}

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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class TeamControllerTest {

    private static MockHttpServletRequest mockRequest;

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

    @BeforeAll
    public static void setup() {

        mockRequest = new MockHttpServletRequest();

        mockRequest.setParameter("teamName", "Chicago Bulls");
        mockRequest.setParameter("city", "Chicago");
        mockRequest.setParameter("stadium", "United Center");
        mockRequest.setParameter("founded", "1966-01-01");

    }

    @Value("${sql.script.create.team}")
    private String sqlAddTeam;

    @Value("${sql.script.delete.team}")
    private String sqlDeleteTeams;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddTeam);
    }

    // (id, team_name, city, stadium, founded)
    // (1,'Detroit Pistons','Detroit','Little Caesars Arena', '1937-01-01')
    // (2,'Atlanta Hawks','Atlanta','State Farm Arena', '1946-01-01')
    // (3, 'Los Angeles Lakers', 'Los Angeles', 'Crypto.com Arena', '1947-01-01')
    @Test
    @DisplayName("Get All Teams")
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
    void getANonValidTeamById() throws Exception {
        mockMvc.perform(get("/api/teams/{teamId}", 4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Team with this id wasn`t found")));
    }

    @Test
    @DisplayName("Add One New Team")
    void addNewTeam() throws Exception {
        team.setId(14);
        team.setTeamName("Philadelphia 76ers");
        team.setCity("Philadelphia");
        team.setStadium("Wells Fargo Center");
        team.setFounded(LocalDate.of(1946, 1, 1));
        
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(team)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Philadelphia")));

//        Team verifyTeam = teamService.findTeamById(14);
//        assertNotNull(verifyTeam, "Team should be valid");

    }

    @AfterEach
    public void setupUpAfterTransaction() {
        jdbc.execute(sqlDeleteTeams);
    }


}

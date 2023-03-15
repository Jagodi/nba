package edu.devember.nba;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.devember.nba.model.Player;
import edu.devember.nba.service.PlayerService;
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
class PlayerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PlayerService playerService;

    @Autowired
    Player player;

    @Value("${sql.script.create.players}")
    private String sqlAddPlayers;

    @Value("${sql.script.delete.players}")
    private String sqlDeletePlayers;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddPlayers);
    }

    @Test
    @DisplayName("Get All Players")
    @Order(1)
    void getAllPlayers() throws Exception {
        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(11)))
                .andExpect(jsonPath("$[0].name", is("Trae Young")))
                .andExpect(jsonPath("$[1].id", is(12)))
                .andExpect(jsonPath("$[1].name", is("Clint Capela")));
    }

    @Test
    @DisplayName("If DB is Empty")
    @Order(2)
    void getEmptyListOfPlayers() throws Exception {

        jdbc.execute("delete from players");

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Oops. Players list is empty!")));
    }

    @Test
    @DisplayName("Get One Player by Id")
    @Order(3)
    void getOnePlayerById() throws Exception {
        mockMvc.perform(get("/api/players/{playerId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(13)))
                .andExpect(jsonPath("$.name", is("John Collins")))
                .andExpect(jsonPath("$.number", is(20)))
                .andExpect(jsonPath("$.position", is("F/C")))
                .andExpect(jsonPath("$.from", is("Wake Forest")))
                .andExpect(jsonPath("$.dateOfBirth", is("1997-09-23")));
    }

    @Test
    @DisplayName("If Player by Id Not Found")
    @Order(4)
    void getANonValidPlayerById() throws Exception {
        mockMvc.perform(get("/api/players/{playerId}", 4))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Player with this id wasn`t found")));
    }

    @Test
    @DisplayName("Add One New Player")
    @Order(5)
    void addNewPlayer() throws Exception {
        player.setName("Dejounte Murray");
        player.setNumber(5);
        player.setPosition("G");
        player.setFrom("Washington");
        player.setDateOfBirth(LocalDate.of(1996, 9, 19));

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isOk());

        Player verifyPlayer = playerService.findPlayerById(1);
        assertNotNull(verifyPlayer, "Player should be valid");
    }

    @Test
    @DisplayName("Update a Player")
    @Order(6)
    void updatePlayer() throws Exception {
        player.setFrom("UCLA");

        // 13 - John Collins`s id
        mockMvc.perform(put("/api/players/{playerId}", 13)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player)))
                .andExpect(status().isOk());

        Player tmpPlayer = playerService.findPlayerById(13);
        assertEquals("UCLA", tmpPlayer.getFrom());
    }

    @Test
    @DisplayName("Delete a Player")
    @Order(8)
    void deleteOnePlayerById() throws Exception {

        mockMvc.perform(delete("/api/players/{playerId}", 13))
                .andExpect(status().isOk())
                .andExpect(content().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Deleted player with id - " + 13)));
    }

    @AfterEach
    public void setupUpAfterTransaction() {
        jdbc.execute(sqlDeletePlayers);
    }

}

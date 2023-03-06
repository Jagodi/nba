package edu.devember.nba;

import edu.devember.nba.model.Team;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class NbaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NbaApplication.class, args);
    }

    @Bean
    @Scope(value = "prototype")
    Team getTeam() {
        return new Team();
    }

}

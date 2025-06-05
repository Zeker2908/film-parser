package ru.zeker.filmparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.zeker.filmparser.service.MovieService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class FilmParserApplication implements CommandLineRunner {

    private final MovieService movieService;

    @Value("${parser.movieCount}")
    private int movieCount;

    public static void main(String[] args) {
        SpringApplication.run(FilmParserApplication.class, args);
    }

    @Override
    public void run(String... args) {
       int results = movieService.parseAndSave(movieCount);
       log.info("Saved {} movies", results);
    }

}

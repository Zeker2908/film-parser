package ru.zeker.filmparser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.zeker.filmparser.service.MovieService;

import java.io.IOException;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class FilmParserApplication implements CommandLineRunner {

    private final MovieService movieService;

    private final ConfigurableApplicationContext context;

    @Value("${parser.movieCount}")
    private int movieCount;

    @Value("${parser.startPage}")
    private int startPage;

    public static void main(String[] args) {
        SpringApplication.run(FilmParserApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        long start = System.currentTimeMillis();

        movieService.parseAndSave(movieCount, startPage);
        movieService.exportAllMoviesToJsonFile("movies.json");

        long end = System.currentTimeMillis();
        long duration = end - start;

        log.info("Parser finished in {} ms ({} seconds)", duration, duration / 1000.0);
        log.warn("Stopping the parser...");
        SpringApplication.exit(context, () -> 0);
    }
}

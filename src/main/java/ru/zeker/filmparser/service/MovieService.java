package ru.zeker.filmparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.filmparser.dto.MovieParseResult;
import ru.zeker.filmparser.mapper.MovieMapper;
import ru.zeker.filmparser.parser.MovieParser;
import ru.zeker.filmparser.repositpory.MovieRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieParser movieParser;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public int parseAndSave(int movieCount) {
        List<MovieParseResult> results = movieParser.parse(movieCount);
        log.info("Parsed {} movies", results.size());
        List<String> urls = results.stream().map(MovieParseResult::getUrl).toList();
        Set<String> existingUrls = movieRepository.findUrlsIn(urls);

        return movieRepository.saveAll(
                results.stream()
                        .filter(m -> !existingUrls.contains(m.getUrl()))
                        .map(movieMapper::toEntity)
                        .toList()
        ).size();
    }

    public void exportAllMoviesToJsonFile(String filename) throws IOException {
        File exportDir = new File("/app/export");
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + exportDir.getAbsolutePath());
        }

        String filepath = "/app/export/" + filename;
        List<MovieParseResult> allMovies = movieRepository.findAll().stream()
                .map(movieMapper::toDto)
                .toList();

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filepath), allMovies);
        log.info("Exported {} movies to {}", allMovies.size(), filepath);
    }
}

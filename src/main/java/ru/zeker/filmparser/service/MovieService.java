package ru.zeker.filmparser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zeker.filmparser.dto.MovieParseResult;
import ru.zeker.filmparser.mapper.MovieMapper;
import ru.zeker.filmparser.parser.MovieParser;
import ru.zeker.filmparser.repositpory.MovieRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieParser movieParser;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

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
}

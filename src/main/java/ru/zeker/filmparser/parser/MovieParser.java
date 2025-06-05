package ru.zeker.filmparser.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.zeker.filmparser.component.JsoupClient;
import ru.zeker.filmparser.dto.MovieMeta;
import ru.zeker.filmparser.dto.MovieParseResult;
import ru.zeker.filmparser.exception.CaptchaException;
import ru.zeker.filmparser.exception.PageNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieParser {
    private final JsoupClient jsoupClient;

    @Value("${parser.base-url}")
    private String baseUrl;

    @Value("${parser.films-url}")
    private String filmsUrl;

    @Value("${parser.timeout.ms}")
    private Integer timeout;

    @Value("${parser.page-size}")
    private Integer pageSize;

    @Value("${parser.max-retries}")
    private Integer maxRetries;

    @Value("${parser.selectors.movie}")
    private String movieSelector;

    @Value("${parser.selectors.title}")
    private String titleSelector;

    @Value("${parser.selectors.rating}")
    private String ratingSelector;

    @Value("${parser.selectors.url}")
    private String urlSelector;

    @Value("${parser.selectors.meta}")
    private String metaSelector;

    @Value("${parser.selectors.year}")
    private String yearSelector;

    @Value("${parser.selectors.original-title}")
    private String originalTitleSelector;



    public List<MovieParseResult> parse(int movieCount, int startPage) {
        List<MovieParseResult> results = new ArrayList<>(movieCount);
        int pageCount = (movieCount + pageSize - 1) / pageSize;
        int parsed = 0;
        int currentPage = startPage;

        log.info("Start parsing. Pages needed: {}", pageCount);

        while (parsed < movieCount) {
            String url = String.format("%s%s&page=%d", baseUrl, filmsUrl, currentPage);

            boolean pageProcessed = false;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    int toParse = Math.min(pageSize, movieCount - parsed);
                    log.info("Processing page {} (attempt {}/{})", url, attempt, maxRetries);

                    List<MovieParseResult> pageResults = processPage(url, toParse);
                    results.addAll(pageResults);
                    parsed += pageResults.size();

                    pageProcessed = true;
                    break;
                } catch (PageNotFoundException ex) {
                    log.error("The page was not loaded because it does not exist");
                    log.warn("Stopping the parser...");
                    return results;
                } catch (CaptchaException ex) {
                    log.warn("The page was not loaded because of a captcha");
                    log.error("Error processing page {} (attempt {}): {}", url, attempt, ex.getMessage());
                    if (attempt < maxRetries) {
                        log.info("Retrying in {} ms...", timeout);
                        randomDelay(timeout, timeout + 2000);
                    }
                } catch (Exception ex) {
                    log.error("Error processing page {}", ex.getMessage());
                    return results;
                }
            }

            if (!pageProcessed) {
                log.error("Failed to process page {} after {} attempts", url, maxRetries);
            }

            currentPage++;
        }

        return results;
    }

    private List<MovieParseResult> processPage(String url, int remainingCount) {
        try {
            Document doc = jsoupClient.fetchDocument(url);

            String pageSource = doc.html();

            if (isCaptchaPage(pageSource)) {
                log.warn("Captcha detected on page: {}", url);
                throw new CaptchaException("CAPTCHA page detected!");
            }

            if (isPageNotFound(pageSource)) {
                log.warn("Page not found: {}", url);
                throw new PageNotFoundException("Page not found");
            }

            Elements elements = doc.select(movieSelector);
            if (elements.isEmpty()) {
                log.warn("No movie elements found on page: {}", url);
                throw new RuntimeException("No movie elements found");
            }

            List<MovieParseResult> pageResults = new ArrayList<>(remainingCount);
            int count = 0;
            for (Element movieElement : elements) {
                if (count >= remainingCount) break;
                try {
                    pageResults.add(parseMovieElement(movieElement));
                    count++;
                } catch (Exception e) {
                    log.error("Error parsing movie element: {}", e.getMessage());
                    log.debug("Stack trace: {}", (Object) e.getStackTrace());
                }
            }

            return pageResults;
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to " + url + ": " + e.getMessage(), e);
        }
    }

    private MovieParseResult parseMovieElement(Element movieElement) {
        String title = movieElement.select(titleSelector).text();
        String originalTitle = Optional.of(movieElement.select(originalTitleSelector).text())
                .filter(s -> !s.isEmpty())
                .orElse(title);
        Double rating = parseRating(movieElement);
        String url = baseUrl + movieElement.select(urlSelector).attr("href");
        Integer year = parseYear(movieElement);

        Element metaElement = movieElement.selectFirst(metaSelector);
        MovieMeta movieMeta = MovieMeta.parseMeta(metaElement != null ? metaElement.text() : "");

        log.debug("Parsed movie: {}, {}, {}, {}, {}, {}, {}, {}", title, originalTitle, year, movieMeta.genre(), movieMeta.country(), movieMeta.director(), rating, url);

        return MovieParseResult.builder()
                .title(title)
                .originalTitle(originalTitle)
                .year(year)
                .rating(rating)
                .url(url)
                .genre(movieMeta.genre())
                .country(movieMeta.country())
                .director(movieMeta.director())
                .build();

    }

    private Double parseRating(Element movieElement) {
        String rawText = movieElement.select(ratingSelector).text();
        try {
            return Double.parseDouble(rawText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseYear(Element movieElement) {
        String rawText = movieElement.select(yearSelector).text();
        Matcher matcher = Pattern.compile("\\b(\\d{4})\\b").matcher(rawText);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private void randomDelay(int min, int max) {
        try {
            Thread.sleep(min + (int)(Math.random() * (max - min)));
        } catch (InterruptedException ignored) {}
    }

    private boolean isCaptchaPage(String pageSource) {
        return pageSource.contains("captcha") ||
                pageSource.contains("recaptcha") ||
                pageSource.contains("Подтвердите, что вы не робот");
    }

    private boolean isPageNotFound(String pageSource) {
        return pageSource.contains("Ничего не найдено") ||
                pageSource.contains("Попробуйте изменить параметры фильтра");
    }

}
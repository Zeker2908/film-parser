package ru.zeker.filmparser.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.zeker.filmparser.dto.MovieMeta;
import ru.zeker.filmparser.dto.MovieParseResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class MovieParser {
    private final WebDriver driver;

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



    public List<MovieParseResult> parse(int movieCount) {
        List<MovieParseResult> results = new ArrayList<>(movieCount);
        int pageCount = (movieCount + pageSize - 1) / pageSize;
        int parsed = 0;

        log.info("Start parsing. Pages needed: {}", pageCount);

        for (int i = 1; i <= pageCount; i++) {
            String url = String.format("%s%s&page=%d", baseUrl, filmsUrl, i);

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
                } catch (Exception ex) {
                    log.error("Error processing page {} (attempt {}): {}", url, attempt, ex.getMessage());

                    if (attempt < maxRetries) {
                        log.info("Retrying in {} ms...", timeout);
                        randomDelay(timeout, timeout + 2000);
                    }
                }
            }

            if (!pageProcessed) {
                log.error("Failed to process page {} after {} attempts", url, maxRetries);
            }
        }

        return results;
    }

    private List<MovieParseResult> processPage(String url, int remainingCount) {
        driver.get(url);

        new WebDriverWait(driver, Duration.ofMillis(timeout))
                .until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.tagName("body")),
                        ExpectedConditions.presenceOfElementLocated(By.id("captcha"))
                ));

        String pageSource = driver.getPageSource();

        if (isCaptchaPage(Objects.requireNonNull(pageSource))) {
            throw new RuntimeException("CAPTCHA page detected!");
        }

        Document doc = Jsoup.parse(pageSource);
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
            }
        }

        return pageResults;
    }

    private boolean isCaptchaPage(String pageSource) {
        return pageSource.contains("captcha") ||
                pageSource.contains("recaptcha") ||
                pageSource.contains("Подтвердите, что вы не робот");
    }

    private MovieParseResult parseMovieElement(Element movieElement) {
        String title = movieElement.select(titleSelector).text();
        String originalTitle = Optional.of(movieElement.select(originalTitleSelector).text())
                .filter(s -> !s.isEmpty())
                .orElse(title);
        Double rating = Double.parseDouble(movieElement.select(ratingSelector).text());
        String url = baseUrl + movieElement.select(urlSelector).attr("href");
        Integer year = parseYear(movieElement);

        Element metaElement = movieElement.selectFirst(metaSelector);
        MovieMeta movieMeta = MovieMeta.parseMeta(metaElement != null ? metaElement.text() : "");

        log.info("Parsed movie: {}, {}, {}, {}, {}, {}, {}, {}", title, originalTitle, year, movieMeta.genre(), movieMeta.country(), movieMeta.director(), rating, url);

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

}
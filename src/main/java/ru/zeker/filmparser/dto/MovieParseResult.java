package ru.zeker.filmparser.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieParseResult {
    private String title;
    private String originalTitle;
    private String genre;
    private String country;
    private Integer year;
    private String director;
    private Double rating;
    private String url;
}

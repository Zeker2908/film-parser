package ru.zeker.filmparser.dto;

public record MovieMeta(String country, String genre, String director) {

    public static MovieMeta parseMeta(String rawText){
        if (rawText.isBlank()) {
            return new MovieMeta("", "", "");
        }
        String[] parts = rawText.split("•");
        String country = parts.length > 0 ? parts[0].trim() : "";
        String genre = parts.length > 1 ? parts[1].split("Режиссёр:")[0].trim() : "";
        String director = parts.length > 1 ? parts[1].split("Режиссёр:")[1].trim() : "";
        return new MovieMeta(country, genre, director);
    }
}

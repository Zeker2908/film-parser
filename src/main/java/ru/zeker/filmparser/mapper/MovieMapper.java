package ru.zeker.filmparser.mapper;

import org.mapstruct.Mapper;
import ru.zeker.filmparser.dto.MovieParseResult;
import ru.zeker.filmparser.model.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    Movie toEntity(MovieParseResult movieParseResult);
    MovieParseResult toDto(Movie movie);
}

package ru.zeker.filmparser.repositpory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zeker.filmparser.model.Movie;

import java.util.List;
import java.util.Set;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m.url FROM Movie m WHERE m.url IN :urls")
    Set<String> findUrlsIn(@Param("urls") List<String> urls);
}

package ru.zeker.filmparser.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "movies", indexes = {
    @Index(name = "movie_url_idx", columnList = "url"),
    @Index(name = "movie_title_idx", columnList = "title"),
    @Index(name = "movie_genre_idx", columnList = "genre"),
    @Index(name = "movie_country_idx", columnList = "country"),
    @Index(name = "movie_year_idx", columnList = "year"),
})
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_seq_gen")
    @SequenceGenerator(
            name = "movie_seq_gen",
            sequenceName = "movie_seq",
            allocationSize = 100
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    private String originalTitle;
    private String genre;
    private String country;
    private Integer year;
    private String director;
    private Double rating;

    @Column(nullable = false)
    private String url;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime parsedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Movie movie = (Movie) o;
        return getId() != null && Objects.equals(getId(), movie.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

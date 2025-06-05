package ru.zeker.filmparser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "parser")
@Getter
@Setter
public class ParserProperties {
    private List<String> userAgents;
}

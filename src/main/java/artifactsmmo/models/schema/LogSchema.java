package artifactsmmo.models.schema;

import artifactsmmo.models.entity.LogContent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class LogSchema {

    @JsonProperty("character")
    private String character;

    @JsonProperty("account")
    private String account;

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("content")
    private LogContent content;

    @JsonProperty("cooldown")
    private int cooldown;

    @JsonProperty("cooldown_expiration")
    private ZonedDateTime cooldownExpiration;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}

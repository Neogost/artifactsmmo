package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class Event {

    @JsonProperty("name")
    private String name;

    @JsonProperty("map")
    private Map map;

    @JsonProperty("previous_skin")
    private String previousSkin;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("expiration")
    private ZonedDateTime expiration;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}

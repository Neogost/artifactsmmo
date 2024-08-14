package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class Announcement {

    @JsonProperty("message")
    private String message;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}

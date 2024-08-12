package artifactsmmo.models.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Cooldown {
    @JsonProperty("total_seconds")
    private int totalSeconds;

    @JsonProperty("remaining_seconds")
    private int remainingSeconds;

    @JsonProperty("started_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    private LocalDateTime startedAt;

    @JsonProperty("expiration")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
    private LocalDateTime expiration;

    @JsonProperty("reason")
    private String reason;
}

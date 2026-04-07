package co.jtmail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailLabelResponse {
    private UUID emailId;
    private UUID labelId;
    private String labelName;
    private String labelColor;
}
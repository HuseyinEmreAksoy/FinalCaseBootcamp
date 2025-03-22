package AdvanceTaskManagement.AdvanceTaskManagement.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

package apinexo.core.modules.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPageResponse {

    private String subscriptionId;

    private String userId;
    private String username;
    private String email;

    private String apiId;
    private String planId;

    private boolean active;
    private boolean isFree;

    private Long quota;
    private long quotaUsed;

    private Double price;
    private String period;

    private LocalDateTime subscribedAt;
}

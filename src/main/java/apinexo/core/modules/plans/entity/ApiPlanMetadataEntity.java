package apinexo.core.modules.plans.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_plan_metadata")
public class ApiPlanMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_id")
    private String apiId;

    @Column(name = "is_soft_limit")
    private Boolean isSoftLimit;

    @Column(name = "key")
    private String key;

    @Column(name = "private")
    private Boolean privateS;

    @Column(name = "rate_limit")
    private String rateLimit;

    @Column(name = "rate_limit_period")
    private Boolean rateLimitPeriod;

    @OneToOne
    @JoinColumn(name = "plan_id")
    private ApiPlanEntity plan;
}
package apinexo.core.modules.plans.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "api_plan_overage_price")
public class ApiPlanOveragePriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private ApiPlanEntity plan;
}
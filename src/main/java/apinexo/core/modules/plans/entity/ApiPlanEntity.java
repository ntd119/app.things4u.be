package apinexo.core.modules.plans.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_plan")
public class ApiPlanEntity {

    @Id
    private String id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "key")
    private String key;

    @Column(name = "up_to")
    private Integer upTo;

    @Column(name = "period")
    private String period;

    @Column(name = "currency")
    private String currency;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "price")
    private Double price;

    @Column(name = "is_free")
    private Boolean isFree;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiPlanOveragePriceEntity> overagePrices;

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApiPlanMetadataEntity metadata;
}

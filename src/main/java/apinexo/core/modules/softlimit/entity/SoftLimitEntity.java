package apinexo.core.modules.softlimit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "soft_limit")
public class SoftLimitEntity {

    @Id
    @Column(name = "price_per_request")
    private Double pricePerRequest;

    @Column(name = "price_id")
    private String priceId;
}

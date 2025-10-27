package apinexo.core.modules.plans.entity;

import java.util.List;

import org.hibernate.annotations.ColumnTransformer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import apinexo.common.entity.EntityCommon;
import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.subscription.entity.SubscribeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "plans")
public class PlansEntity extends EntityCommon {

    @Id
    private String id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "key")
    private String key;

    @Column(name = "up_to")
    private Long upTo;

    @Column(name = "period")
    private String period;

    @Column(name = "currency")
    private String currency;

    @Column(name = "price")
    private Integer price;

    @Column(name = "is_free")
    private Boolean isFree;

    @Column(name = "active", length = 1)
    private Boolean active;

    @Column(name = "overage_prices", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String overagePrices;

    @Column(name = "metadata", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String metadata;

    @ManyToOne
    @JoinColumn(name = "api_id")
    @JsonIgnore
    private ApiEntity api;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscribeEntity> subscriptions;

}

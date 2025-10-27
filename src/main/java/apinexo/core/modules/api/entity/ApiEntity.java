package apinexo.core.modules.api.entity;

import java.util.List;

import apinexo.common.entity.EntityCommon;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.subscription.entity.SubscribeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "apis")
public class ApiEntity extends EntityCommon {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "api", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlansEntity> plans;

    @OneToMany(mappedBy = "api", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscribeEntity> subscriptions;
}

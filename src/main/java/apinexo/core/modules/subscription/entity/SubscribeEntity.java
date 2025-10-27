package apinexo.core.modules.subscription.entity;

import java.time.LocalDateTime;

import apinexo.core.modules.api.entity.ApiEntity;
import apinexo.core.modules.plans.entity.PlansEntity;
import apinexo.core.modules.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "subscribe")
public class SubscribeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "api_id")
    private ApiEntity api;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PlansEntity plan;

    private LocalDateTime subscribedAt;
}

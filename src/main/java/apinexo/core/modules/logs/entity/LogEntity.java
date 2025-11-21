package apinexo.core.modules.logs.entity;

import apinexo.core.modules.subscription.entity.SubscriptionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "log")
public class LogEntity {

    @Id
    private String id;

    @Column(name = "create_at")
    private Long createAt;

    @Column(name = "statusCode")
    private Integer statusCode;

    @Column(name = "responseBody")
    private String responseBody;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private SubscriptionEntity subscription;
}

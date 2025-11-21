package apinexo.core.modules.logs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "create_at")
    private Long createAt;

    @Column(name = "statusCode")
    private Integer statusCode;

    @Column(name = "responseBody")
    private String responseBody;
}

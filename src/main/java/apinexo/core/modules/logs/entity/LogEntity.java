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

    @Column(name = "time")
    private Long time;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "method")
    private String method;

    @Column(name = "location")
    private String location;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "latency")
    private String latency;

    @Column(name = "request_headers")
    private String requestHeaders;

    @Column(name = "request_query_parameters")
    private String requestQueryParameters;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "response_headers")
    private String responseHeaders;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
}

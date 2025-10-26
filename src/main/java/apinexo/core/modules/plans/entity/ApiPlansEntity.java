package apinexo.core.modules.plans.entity;

import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "api_plans")
public class ApiPlansEntity {

    @Id
    private String id;

    @Column(name = "basic", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String basic;

    @Column(name = "pro", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String pro;

    @Column(name = "ultra", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String ultra;

    @Column(name = "mega", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String mega;
}

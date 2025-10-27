package apinexo.common.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@Setter
public abstract class EntityCommon {

    @Column(name = "create_date", updatable = true)
    private Long createDate;

    @Column(name = "update_date", updatable = true)
    private Long updatedDate;

    @Column(name = "active", length = 1)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        long milliseconds = now.toInstant(ZoneOffset.UTC).toEpochMilli();
        if (createDate == null) {
            createDate = milliseconds;
        }
        if (updatedDate == null) {
            updatedDate = milliseconds;
        }
        if (active == null) {
            active = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (updatedDate == null) {
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            long milliseconds = now.toInstant(ZoneOffset.UTC).toEpochMilli();
            updatedDate = milliseconds;
        }
    }
}
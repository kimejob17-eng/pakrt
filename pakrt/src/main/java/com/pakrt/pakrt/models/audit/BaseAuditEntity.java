package com.pakrt.pakrt.models.audit;

import com.pakrt.pakrt.utils.General;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseAuditEntity implements Serializable {

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    public void onPrePersist() {
        String currentUser = General.getUsername();
        if (this.createdBy == null || this.createdBy.trim().isEmpty()) {
            this.createdBy = currentUser;
        }
        if (this.updatedBy == null || this.updatedBy.trim().isEmpty()) {
            this.updatedBy = currentUser;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = this.createdAt;
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedBy = General.getUsername();
        this.updatedAt = LocalDateTime.now();
    }
}

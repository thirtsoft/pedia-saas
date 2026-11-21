package com.wokite.net.shared.persistence;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wokite_sequence_generator"
    )
    @SequenceGenerator(
            name = "wokite_sequence_generator",
            sequenceName = "wokite_sequence",
            allocationSize = 50
    )
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    private Long id;

    @UuidGenerator
    @Column(
            name = "uuid",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID uuid;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "record_status",
            nullable = false,
            length = 20
    )
    private RecordStatus recordStatus = RecordStatus.ACTIVE;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }
}
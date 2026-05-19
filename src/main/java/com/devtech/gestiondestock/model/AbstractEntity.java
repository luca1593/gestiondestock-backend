package com.devtech.gestiondestock.model;

import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.Instant;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Instant creationDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "identreprise")
    private Integer identreprise;

    @PrePersist
    public void prePersist() {
        if (identreprise == null) {
            Integer entrepriseId = resolveEntrepriseId();
            if (entrepriseId != null) {
                this.identreprise = entrepriseId;
            }
        }
    }

    private Integer resolveEntrepriseId() {
        try {
            for (Field f : getClass().getDeclaredFields()) {
                if ("entreprise".equals(f.getName()) && Entreprise.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    Object value = f.get(this);
                    if (value instanceof Entreprise) {
                        return ((Entreprise) value).getId();
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        String mdcVal = MDC.get("idEntreprise");
        if (mdcVal != null) {
            try {
                return Integer.parseInt(mdcVal);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}

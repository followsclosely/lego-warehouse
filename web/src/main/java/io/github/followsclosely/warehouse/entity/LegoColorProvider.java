package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class LegoColorProvider {
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String providerId;
    private String description;
}

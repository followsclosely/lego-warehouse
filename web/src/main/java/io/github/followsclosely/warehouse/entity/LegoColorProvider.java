package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.ValueObject;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(includeFieldNames = false)
public class LegoColorProvider {
    @Id
    @Column(nullable = false)
    private String provider;

    @Id
    @Column(nullable = false)
    private String providerId;

    @Column(length = 1000)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegoColorProvider that = (LegoColorProvider) o;
        return java.util.Objects.equals(provider, that.provider) &&
                java.util.Objects.equals(providerId, that.providerId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(provider, providerId);
    }
}

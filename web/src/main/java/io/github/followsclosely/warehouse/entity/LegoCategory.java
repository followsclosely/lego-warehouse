package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.javers.core.metamodel.annotation.ValueObject;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(includeFieldNames = false)
public class LegoCategory {
    @Id
    private String id;
    private String name;
}
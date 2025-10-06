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
@ToString(of = {"id", "name"}, includeFieldNames = false)
public class LegoMinifig {
    @Id
    private String id;
    private String name;
    private int partCount;
    private String imageUrl;
}

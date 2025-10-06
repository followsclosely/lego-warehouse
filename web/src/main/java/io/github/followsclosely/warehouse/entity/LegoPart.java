package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class LegoPart {
    @Id
    private String id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private LegoCategory legoCategory;
    private String material;
}

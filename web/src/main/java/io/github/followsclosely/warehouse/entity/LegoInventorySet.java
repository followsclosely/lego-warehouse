package io.github.followsclosely.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.javers.core.metamodel.annotation.ValueObject;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(includeFieldNames = false)
public class LegoInventorySet {

    @ManyToOne
    @JoinColumn(name = "set_id")
    private LegoSet legoSet;

    @Column(nullable = false)
    private int quantity;
}

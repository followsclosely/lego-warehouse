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
public class LegoInventoryPart {

    @ManyToOne
    @JoinColumn(name = "part_id")
    private LegoPart legoPart;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private LegoColor legoColor;

    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private boolean spare;
}

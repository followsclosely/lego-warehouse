package io.github.followsclosely.warehouse.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.javers.core.metamodel.annotation.ValueObject;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.Optional;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValueObject
@ToString(of = {"legoSet", "id", "version"}, includeFieldNames = false)
public class LegoInventory {
    @Id
    private String id;
    private Integer version;

    @DiffIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lego_inventory_parts", joinColumns = @JoinColumn(name = "inventory_id"))
    private Set<LegoInventoryPart> parts;

    @DiffIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lego_inventory_minifigs", joinColumns = @JoinColumn(name = "inventory_id"))
    private Set<LegoInventoryMinifig> minifigs;

    @DiffIgnore
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lego_inventory_sets", joinColumns = @JoinColumn(name = "inventory_id"))
    private Set<LegoInventorySet> sets;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "set_id")
    private LegoSet legoSet;

    public Optional<LegoInventoryPart> getPart(@Nonnull String partId, @Nonnull String colorId, boolean isSpare) {
        return parts.stream()
                .filter(i ->
                        partId.equals(i.getLegoPart().getId())
                                && i.getLegoColor().getId().equals(colorId)
                                && i.isSpare() == isSpare
                )
                .findFirst();

    }

    public Optional<LegoInventoryMinifig> getMinifig(@Nonnull String minifigId) {
        return minifigs.stream()
                .filter(i -> minifigId.equals(i.getLegoMinifig().getId()))
                .findFirst();
    }
}

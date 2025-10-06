package io.github.followsclosely.warehouse.diff;

import io.github.followsclosely.warehouse.entity.LegoColor;
import io.github.followsclosely.warehouse.entity.LegoColorProvider;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LegoColorDifferTest {

    @Test
    void testNameChange() {
        LegoColor left = LegoColor.builder().id("1").name("Red").rgb("FF0000").transparent(false).build();
        left.getProviders().add(LegoColorProvider.builder().provider("Provider1").providerId("1.1").description("Bright Red").build());
        left.getProviders().add(LegoColorProvider.builder().provider("Provider2").providerId("Q").description("Original Red").build());
        left.getProviders().add(LegoColorProvider.builder().provider("Provider2").providerId("DP").description("Dark Pink").build());

        LegoColor right = LegoColor.builder().id("1").name("Red").rgb("FF0000").transparent(false).build();
        right.getProviders().add(LegoColorProvider.builder().provider("Provider1").providerId("1.1").description("Bright Red").build());
        right.getProviders().add(LegoColorProvider.builder().provider("Provider2").providerId("Q").description("Original Red").build());
        right.getProviders().add(LegoColorProvider.builder().provider("Provider2").providerId("DP").description("Dark Pink").build());

        Javers javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build();

        Diff diff = javers.compare(left, right);
        assertNotNull(diff.changesSummary());

        System.out.println(diff.prettyPrint());
        diff.getChanges().forEach(System.out::println);

        assertEquals(0, diff.getChanges().size());
    }

}
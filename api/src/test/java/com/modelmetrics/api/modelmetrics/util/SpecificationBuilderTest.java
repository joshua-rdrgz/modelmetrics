package com.modelmetrics.api.modelmetrics.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/** SpecificationBuilderTest. */
public class SpecificationBuilderTest {

  @Test
  public void testSpecificationBuilderWithSingleSpecification() {
    // Arrange
    Specification<String> spec =
        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "test");

    // Act
    SpecificationBuilder<String> builder = new SpecificationBuilder<>(spec);

    // Assert
    assertNotNull(builder.build());
    assertSame(spec, builder.build());
  }

  @Test
  public void testSpecificationBuilderWithMultipleSpecifications() {
    // Arrange
    Specification<String> spec1 =
        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "test");
    Specification<String> spec2 =
        (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("description"), "test description");

    // Act
    SpecificationBuilder<String> builder = new SpecificationBuilder<>(spec1);
    Specification<String> result = builder.addSpecification(spec2, true).build();

    // Assert
    assertNotNull(result);
    assertNotSame(spec1, result);
    assertNotSame(spec2, result);
  }

  @Test
  public void testSpecificationBuilderWithMultipleSpecificationsWhereSecondIsNotAdded() {
    // Arrange
    Specification<String> spec1 =
        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "test");
    Specification<String> spec2 =
        (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("description"), "test description");

    // Act
    SpecificationBuilder<String> builder = new SpecificationBuilder<>(spec1);
    Specification<String> result = builder.addSpecification(spec2, false).build();

    // Assert
    assertNotNull(result);
    assertSame(spec1, result);
  }

  @Test
  public void testSpecificationBuilderWithNullSpecification() {
    // Act and Assert
    assertThrows(
        NullPointerException.class,
        () -> new SpecificationBuilder<>(null),
        "SpecificationBuilder constructor did not throw an exception like it should.");
  }

  @Test
  public void testAddSpecificationWithNullSpecification() {
    // Arrange
    Specification<String> spec =
        (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), "test");

    // Act and Assert
    assertThrows(
        NullPointerException.class,
        () -> new SpecificationBuilder<>(spec).addSpecification(null, true),
        "Add Specification method did not throw an exception like it should.");
  }
}

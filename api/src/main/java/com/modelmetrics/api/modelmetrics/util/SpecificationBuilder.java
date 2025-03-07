package com.modelmetrics.api.modelmetrics.util;

import org.springframework.data.jpa.domain.Specification;

/** SpecificationBuilder. */
public class SpecificationBuilder<T> {
  private Specification<T> specification;

  /**
   * Creates a new SpecificationBuilder with the given specification.
   *
   * @param specification the specification to build upon
   * @throws NullPointerException if the specification is null
   */
  public SpecificationBuilder(Specification<T> specification) {
    if (specification == null) {
      throw new NullPointerException("Specification cannot be nullssssss");
    }
    this.specification = specification;
  }

  /**
   * Adds a specification to the existing one.
   *
   * @param specToAdd the specification to add
   * @param criteriaMet whether the specification should be added
   * @return this SpecificationBuilder instance
   * @throws NullPointerException if the specification to add is null
   */
  public SpecificationBuilder<T> addSpecification(Specification<T> specToAdd, boolean criteriaMet) {
    if (specToAdd == null) {
      throw new NullPointerException("Specification to add cannot be nullssssssss");
    }
    if (criteriaMet) {
      specification = specification.and(specToAdd);
    }
    return this;
  }

  public Specification<T> build() {
    return specification;
  }
}

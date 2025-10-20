package org.springframework.samples.petclinic.vets.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.samples.petclinic.rest.dto.PageVetDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;

final class PageDtos {
  private PageDtos() {}

  static PageVetDto vet(Page<VetDto> p) {
    PageVetDto dto = new PageVetDto();
    dto.setContent(p.getContent());
    dto.setPage(p.getNumber());
    dto.setSize(p.getSize());
    dto.setTotalElements(p.getTotalElements());
    dto.setTotalPages(p.getTotalPages());
    dto.setSort(p.getSort().stream().map(Sort.Order::toString).toList());
    return dto;
  }
}

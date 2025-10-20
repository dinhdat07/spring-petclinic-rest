package org.springframework.samples.petclinic.visits.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.samples.petclinic.rest.dto.PageVisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

final class PageDtos {
  private PageDtos() {}

  static PageVisitDto visit(Page<VisitDto> p) {
    PageVisitDto dto = new PageVisitDto();
    dto.setContent(p.getContent());
    dto.setPage(p.getNumber());
    dto.setSize(p.getSize());
    dto.setTotalElements(p.getTotalElements());
    dto.setTotalPages(p.getTotalPages());
    dto.setSort(p.getSort().stream().map(Sort.Order::toString).toList());
    return dto;
  }
}

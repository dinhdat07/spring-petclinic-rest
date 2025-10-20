package org.springframework.samples.petclinic.platform.web;

import java.util.List;
import org.springframework.data.domain.*;

public final class WebPageables {
  private WebPageables() {}

  public static Pageable pageable(Integer page, Integer size, List<String> sort, Sort defaultSort) {
    int p = page == null ? 0 : Math.max(0, page);
    int s = size == null ? 20 : Math.max(1, Math.min(100, size));
    Sort srt = (sort == null || sort.isEmpty())
        ? defaultSort
        : Sort.by(sort.stream().map(WebPageables::toOrder).toList());
    return PageRequest.of(p, s, srt);
  }

  private static Sort.Order toOrder(String token) {
    int i = token.indexOf(',');
    if (i < 0) return Sort.Order.asc(token.trim());
    String prop = token.substring(0, i).trim();
    String dir  = token.substring(i + 1).trim();
    return "desc".equalsIgnoreCase(dir) ? Sort.Order.desc(prop) : Sort.Order.asc(prop);
  }
}

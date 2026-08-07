package com.harvesttracker.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        boolean last = (page + 1) >= totalPages;
        return new PagedResponse<>(content, page, size, totalElements, totalPages, last);
    }

    public static <T> PagedResponse<T> of(List<T> content, org.springframework.data.domain.Page<?> page) {
        return of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}

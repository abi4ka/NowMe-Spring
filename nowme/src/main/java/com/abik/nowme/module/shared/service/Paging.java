package com.abik.nowme.module.shared.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Builds page requests from client input, clamping values that would otherwise
 * make PageRequest throw (negative page, zero size) or load huge pages.
 */
public final class Paging {

    public static final int MAX_PAGE_SIZE = 50;

    private Paging() {
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), sort);
    }
}

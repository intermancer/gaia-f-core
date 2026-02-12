package com.intermancer.gaiaf.core.dto;

import java.util.List;

/**
 * Generic wrapper for paginated API responses.
 * Contains the list of items for the current page along with
 * pagination metadata.
 *
 * @param <T> The type of items in the paginated list
 */
public record PaginatedResponse<T>(
    List<T> items,
    int totalCount,
    int offset,
    int limit
) {}

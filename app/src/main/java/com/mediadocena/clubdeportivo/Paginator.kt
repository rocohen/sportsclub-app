package com.mediadocena.clubdeportivo.com.mediadocena.clubdeportivo


class Paginator<T>(
    private val items: List<T>,         // List of elements to paginate
    private val pageSize: Int = 4        // Page size (4 element per page by default)
) {
    private var currentPage = 0          // Current page

    // This function returns all the elements of current page
    fun getCurrentPageItems(): List<T> {
        val start = currentPage * pageSize
        val end = minOf(start + pageSize, items.size)
        return items.subList(start, end)
    }

    // Navigates to the following page if it exists
    fun goToNextPage(): Boolean {
        return if ((currentPage + 1) * pageSize < items.size) {
            currentPage++
            true
        } else {
            false
        }
    }

    // Navigates to the previous page if it exists
    fun goToPreviousPage(): Boolean {
        return if (currentPage > 0) {
            currentPage--
            true
        } else {
            false
        }
    }

    // Returns true if there are more pages after current page
    fun hasNextPage(): Boolean {
        return (currentPage + 1) * pageSize < items.size
    }

    // Returns true if there are more pages previous to current page
    fun hasPreviousPage(): Boolean {
        return currentPage > 0
    }

    // Resets pagination to the first page
    fun reset() {
        currentPage = 0
    }
}
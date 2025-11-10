package com.amanjeet.ktoon

/**
 * Delimiter options for tabular array rows and inline primitive arrays.
 */
enum class Delimiter(val value: String) {
    /**
     * Comma delimiter (,) - default option
     */
    COMMA(","),

    /**
     * Tab delimiter (\t)
     */
    TAB("\t"),

    /**
     * Pipe delimiter (|)
     */
    PIPE("|");

    override fun toString(): String = value
}

package com.amanjeet.ktoon.encoder

/**
 * Line writer that accumulates indented lines for building the final output.
 */
class LineWriter(private val indentSize: Int) {
    private val lines = mutableListOf<String>()
    private val indentationString = " ".repeat(indentSize)

    /**
     * Adds a line with the specified depth and content.
     *
     * @param depth   Indentation depth (0 = no indentation)
     * @param content Line content to add
     */
    fun push(depth: Int, content: String) {
        val indent = indentationString.repeat(depth)
        lines.add(indent + content)
    }

    /**
     * Joins all accumulated lines with newlines.
     *
     * @return The complete output string
     */
    override fun toString(): String = lines.joinToString("\n")
}

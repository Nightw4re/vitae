package com.vitae.data;

/**
 * A single line of dialogue spoken by a Vitae NPC.
 *
 * @param text       raw text of the line (supports translation keys prefixed with "lang:")
 * @param condition  optional condition key that must be met to show this line (nullable = always shown)
 * @param nextId     ID of the next dialogue node to advance to (nullable = end conversation)
 */
public record DialogueLine(
        String text,
        String condition,
        String nextId
) {}

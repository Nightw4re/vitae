package com.vitae.data;

/**
 * Defines a single trade offered by a Vitae NPC.
 *
 * <p>Maps directly to a villager MerchantOffer. Both cost slots support vanilla item IDs
 * or modded items via resource location.
 *
 * @param costA      primary cost item resource location (e.g. "minecraft:emerald")
 * @param costACount amount of costA required
 * @param costB      optional secondary cost item (nullable = single-item trade)
 * @param costBCount amount of costB required (ignored if costB is null)
 * @param result     result item resource location
 * @param resultCount amount of result given
 * @param maxUses    how many times this trade can be used before it locks (-1 = unlimited)
 */
public record TradeEntry(
        String costA,
        int costACount,
        String costB,
        int costBCount,
        String result,
        int resultCount,
        int maxUses
) {}

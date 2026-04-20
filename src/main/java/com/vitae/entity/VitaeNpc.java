package com.vitae.entity;

import com.vitae.animation.VitaeAnimationController;
import com.vitae.data.NpcDefinition;
import com.vitae.data.TradeEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;

import java.util.Optional;

/**
 * Base class for all Vitae-managed friendly NPCs.
 *
 * <p>Behaviour (dialogue, trades, movement) is driven by an {@link NpcDefinition}
 * loaded from a datapack JSON. Do not hardcode NPC types — register via the Vitae registry.
 */
public class VitaeNpc extends AbstractVillager implements GeoEntity {

    private final NpcDefinition definition;
    private final AnimatableInstanceCache animatableCache = GeckoLibUtil.createInstanceCache(this);

    protected VitaeNpc(EntityType<? extends VitaeNpc> type, Level level, NpcDefinition definition) {
        super(type, level);
        this.definition = definition;
    }

    public NpcDefinition getDefinition() {
        return definition;
    }

    @Override
    protected void updateTrades() {
        if (!definition.hasTrades()) return;
        MerchantOffers offers = getOffers();
        for (TradeEntry entry : definition.trades()) {
            ItemCost costA = new ItemCost(net.minecraft.world.item.Items.EMERALD, entry.costACount());
            Optional<ItemCost> costB = entry.costB() != null
                    ? Optional.of(new ItemCost(net.minecraft.world.item.Items.EMERALD, entry.costBCount()))
                    : Optional.empty();
            ItemStack result = new ItemStack(net.minecraft.world.item.Items.EMERALD, entry.resultCount());
            int maxUses = entry.maxUses() < 0 ? Integer.MAX_VALUE : entry.maxUses();
            offers.add(new MerchantOffer(costA, costB, result, 0, maxUses, 0, 0.05f));
        }
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        // Vitae NPCs do not give XP on trade — intentional
    }

    @Override
    public VitaeNpc getBreedOffspring(ServerLevel level, AgeableMob partner) {
        // Vitae NPCs do not breed
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(VitaeAnimationController.createIdle(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableCache;
    }
}

package com.vitae.demo;

import com.vitae.data.EntityDefinition;
import com.vitae.data.EntityDefinitionLoader;
import com.vitae.data.BossBarDefinition;
import com.vitae.registry.VitaeRegistry;
import net.minecraft.resources.ResourceLocation;

public final class VitaeDemoDefinition {

    public static final ResourceLocation TEST_ENTITY_ID = ResourceLocation.fromNamespaceAndPath("vitae", "angry_boy");

    private static final String FALLBACK_JSON = """
            {
              "model": "vitae:geo/angry_boy.geo.json",
              "animations": "vitae:animations/angry_boy.animation.json",
              "intro_animation": "intro",
              "xp_reward": 30,
              "attributes": {
                "max_health": 12,
                "follow_range": 32,
                "movement_speed": 0.26,
                "attack_damage": 1,
                "armor": 0
              },
              "abilities": [
                { "id": "vitae:guard_summon" },
                { "id": "vitae:vex_grab", "cooldown_ticks": "200" },
                { "id": "vitae:spin_slash" },
                { "id": "melee_attack" }
              ],
              "phases": [
                {
                  "id": "normal",
                  "health_threshold": 1.0,
                  "animation": "angry_idle"
                },
                {
                  "id": "guarded",
                  "health_threshold": 0.75,
                  "animation": "angry_guard"
                },
                {
                  "id": "enraged",
                  "health_threshold": 0.5,
                  "animation": "angry_spin"
                }
              ],
              "boss_bar": {
                "color": "green",
                "overlay": "progress",
                "text": "Angry Boy"
              },
              "on_death": {
                "spawn_loot_chest": true
              },
              "loot_table": "vitae:entities/angry_boy",
              "combat": {
                "basic_melee_enabled": true,
                "basic_melee_cooldown_ticks": 20,
                "basic_melee_range": 3.5,
                "scale_damage_with_held_weapon": true,
                "scale_attack_speed_with_held_weapon": true,
                "spin_radius": 3.0,
                "spin_invulnerable": true
              },
              "equipment": {
                "main_hand": null
              },
              "hp_lock": [0.75]
            }
            """;

    private static final EntityDefinition FALLBACK_DEFINITION = EntityDefinitionLoader.parse(FALLBACK_JSON);

    private VitaeDemoDefinition() {}

    public static EntityDefinition testEntityDefinition() {
        EntityDefinition loaded = VitaeRegistry.get().getEntity(TEST_ENTITY_ID);
        return loaded != null ? loaded : FALLBACK_DEFINITION;
    }
}


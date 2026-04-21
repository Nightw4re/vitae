package com.vitae.api;

import com.vitae.data.AbilityDefinition;
import com.vitae.data.AbilityReference;
import com.vitae.data.AbilityParameters;
import com.vitae.data.PhaseDefinition;
import com.vitae.testsupport.TestAssertions;

import java.util.ArrayList;
import java.util.List;

public final class VitaeEventBusTest {

    public static void run() {
        testListenerReceivesEvent();
        testMultipleListeners();
        testUnregisterStopsDelivery();
        testClearRemovesAllListeners();
        testPhaseChangeEventCarriesData();
        testAbilityEventCarriesData();
    }

    private static void testListenerReceivesEvent() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> received = new ArrayList<>();
        bus.register(received::add);
        bus.fire(new VitaeEntityEvent.Spawn("jaffa_warrior"));
        TestAssertions.assertEquals(1, received.size());
        TestAssertions.assertEquals("jaffa_warrior", ((VitaeEntityEvent.Spawn) received.get(0)).entityId());
    }

    private static void testMultipleListeners() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> a = new ArrayList<>();
        List<VitaeEntityEvent> b = new ArrayList<>();
        bus.register(a::add);
        bus.register(b::add);
        bus.fire(new VitaeEntityEvent.Death("jaffa_warrior", false));
        TestAssertions.assertEquals(1, a.size());
        TestAssertions.assertEquals(1, b.size());
    }

    private static void testUnregisterStopsDelivery() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> received = new ArrayList<>();
        java.util.function.Consumer<VitaeEntityEvent> listener = received::add;
        bus.register(listener);
        bus.unregister(listener);
        bus.fire(new VitaeEntityEvent.Spawn("jaffa_warrior"));
        TestAssertions.assertEquals(0, received.size());
    }

    private static void testClearRemovesAllListeners() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> received = new ArrayList<>();
        bus.register(received::add);
        bus.register(received::add);
        bus.clear();
        bus.fire(new VitaeEntityEvent.Reset("jaffa_warrior"));
        TestAssertions.assertEquals(0, received.size());
    }

    private static void testPhaseChangeEventCarriesData() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> received = new ArrayList<>();
        bus.register(received::add);
        PhaseDefinition p1 = new PhaseDefinition("phase_1", 1.0, List.of(), null, null, 1.0, null);
        PhaseDefinition p2 = new PhaseDefinition("phase_2", 0.5, List.of(), null, null, 1.4, null);
        bus.fire(new VitaeEntityEvent.PhaseChange("system_lord", p1, p2));
        VitaeEntityEvent.PhaseChange event = (VitaeEntityEvent.PhaseChange) received.get(0);
        TestAssertions.assertEquals("phase_1", event.previous().id());
        TestAssertions.assertEquals("phase_2", event.next().id());
    }

    private static void testAbilityEventCarriesData() {
        VitaeEventBus bus = new VitaeEventBus();
        List<VitaeEntityEvent> received = new ArrayList<>();
        bus.register(received::add);
        AbilityDefinition ability = new AbilityDefinition("staff_beam", "ranged_projectile", 40, null, AbilityParameters.empty(), List.of(), 0, 0, true);
        bus.fire(new VitaeEntityEvent.AbilityUsed("jaffa_warrior", ability));
        VitaeEntityEvent.AbilityUsed event = (VitaeEntityEvent.AbilityUsed) received.get(0);
        TestAssertions.assertEquals("staff_beam", event.ability().id());
    }
}

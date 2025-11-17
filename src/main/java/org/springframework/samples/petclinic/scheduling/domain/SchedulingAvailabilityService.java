package org.springframework.samples.petclinic.scheduling.domain;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.samples.petclinic.appointments.events.AppointmentConfirmedEvent;

public class SchedulingAvailabilityService {

    private final Map<Integer, Set<Integer>> vetToAppointments = new ConcurrentHashMap<>();

    public void onAppointmentConfirmed(AppointmentConfirmedEvent event) {
        if (event == null || event.appointmentId() == null || event.vetId() == null) {
            return;
        }
        vetToAppointments
            .computeIfAbsent(event.vetId(), ignored -> ConcurrentHashMap.newKeySet())
            .add(event.appointmentId());
    }

    public int activeAppointmentsForVet(Integer vetId) {
        if (vetId == null) {
            return 0;
        }
        return vetToAppointments.getOrDefault(vetId, Set.of()).size();
    }

    public Map<Integer, Integer> snapshot() {
        return vetToAppointments.entrySet().stream()
            .collect(ConcurrentHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue().size()), Map::putAll);
    }
}

package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.EventScheduleDTO;
import tn.enicarthage.backendandroid.models.Event;
import tn.enicarthage.backendandroid.models.EventSchedule;
import tn.enicarthage.backendandroid.models.Lieu;
import tn.enicarthage.backendandroid.repositories.EventRepository;
import tn.enicarthage.backendandroid.repositories.EventScheduleRepository;
import tn.enicarthage.backendandroid.repositories.LieuRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventScheduleService {
    private final EventScheduleRepository eventScheduleRepository;
    private final EventRepository eventRepository;
    private final LieuRepository lieuRepository;

    public List<EventScheduleDTO> getAllEventSchedules() {
        return eventScheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventScheduleDTO> getEventScheduleById(Long id) {
        return eventScheduleRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<EventScheduleDTO> getEventSchedulesByEventId(Long eventId) {
        return eventScheduleRepository.findByEvent_Id(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventScheduleDTO> getEventSchedulesByLieuId(Long lieuId) {
        return eventScheduleRepository.findByLieu_Id(lieuId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventScheduleDTO> getUpcomingEventSchedules() {
        return eventScheduleRepository.findByDateTimeAfter(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventScheduleDTO> getEventSchedulesByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventScheduleRepository.findByDateTimeBetween(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventScheduleDTO createEventSchedule(EventScheduleDTO eventScheduleDTO) {
        Optional<Event> eventOpt = eventRepository.findById(eventScheduleDTO.getEventId());
        Optional<Lieu> lieuOpt = lieuRepository.findById(eventScheduleDTO.getLieuId());

        if (eventOpt.isEmpty() || lieuOpt.isEmpty()) {
            throw new IllegalArgumentException("Event or Lieu not found");
        }

        EventSchedule eventSchedule = new EventSchedule();
        eventSchedule.setEvent(eventOpt.get());
        eventSchedule.setLieu(lieuOpt.get());
        eventSchedule.setDateTime(eventScheduleDTO.getDateTime());
        eventSchedule.setIsSoldOut(eventScheduleDTO.isSoldOut());

        EventSchedule savedEventSchedule = eventScheduleRepository.save(eventSchedule);
        return convertToDTO(savedEventSchedule);
    }

    public Optional<EventScheduleDTO> updateEventSchedule(Long id, EventScheduleDTO eventScheduleDTO) {
        if (!eventScheduleRepository.existsById(id)) {
            return Optional.empty();
        }

        Optional<Event> eventOpt = eventRepository.findById(eventScheduleDTO.getEventId());
        Optional<Lieu> lieuOpt = lieuRepository.findById(eventScheduleDTO.getLieuId());

        if (eventOpt.isEmpty() || lieuOpt.isEmpty()) {
            throw new IllegalArgumentException("Event or Lieu not found");
        }

        EventSchedule eventSchedule = new EventSchedule();
        eventSchedule.setId(id);
        eventSchedule.setEvent(eventOpt.get());
        eventSchedule.setLieu(lieuOpt.get());
        eventSchedule.setDateTime(eventScheduleDTO.getDateTime());
        eventSchedule.setIsSoldOut(eventScheduleDTO.isSoldOut());

        EventSchedule updatedEventSchedule = eventScheduleRepository.save(eventSchedule);
        return Optional.of(convertToDTO(updatedEventSchedule));
    }

    public boolean deleteEventSchedule(Long id) {
        if (!eventScheduleRepository.existsById(id)) {
            return false;
        }

        eventScheduleRepository.deleteById(id);
        return true;
    }

    private EventScheduleDTO convertToDTO(EventSchedule eventSchedule) {
        // Calculer l'heure de check-in (3 heures avant l'événement)
        LocalDateTime checkinTime = eventSchedule.getDateTime().minusHours(3);

        return new EventScheduleDTO(
                eventSchedule.getId(),
                eventSchedule.getEvent().getId(),
                eventSchedule.getLieu().getId(),
                eventSchedule.getDateTime(),
                checkinTime,
                eventSchedule.isIsSoldOut(),
                eventSchedule.getEvent().getTitle(),
                eventSchedule.getLieu().getName()
        );
    }
}

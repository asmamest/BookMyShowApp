package tn.enicarthage.backendandroid.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.dto.request.EventDTO;
import tn.enicarthage.backendandroid.models.Event;
import tn.enicarthage.backendandroid.repositories.EventRepository;
import tn.enicarthage.backendandroid.enums.CategoryEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<EventDTO> getEventsByCategory(CategoryEvent category) {
        return eventRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> searchEventsByTitle(String title) {
        return eventRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = convertToEntity(eventDTO);
        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    public Optional<EventDTO> updateEvent(Long id, EventDTO eventDTO) {
        if (!eventRepository.existsById(id)) {
            return Optional.empty();
        }

        Event event = convertToEntity(eventDTO);
        event.setId(id);
        Event updatedEvent = eventRepository.save(event);
        return Optional.of(convertToDTO(updatedEvent));
    }

    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }

        eventRepository.deleteById(id);
        return true;
    }

    private EventDTO convertToDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getSiteUrl(),
                event.getEventImg(),
                event.getCategory()
        );
    }

    private Event convertToEntity(EventDTO eventDTO) {
        return new Event(
                eventDTO.getId(),
                eventDTO.getTitle(),
                eventDTO.getDescription(),
                eventDTO.getSiteUrl(),
                eventDTO.getEventImg(),
                eventDTO.getCategory()
        );
    }
}

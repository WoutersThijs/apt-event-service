package fact.it.eventservice.controller;

import fact.it.eventservice.model.Event;
import fact.it.eventservice.repository.EventRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @PostConstruct
    public void fillDB(){
        if(eventRepository.count()==0){
            eventRepository.save(new Event("Event1", "Organizer1"));
            eventRepository.save(new Event("Event2", "Organizer2"));
            eventRepository.save(new Event("Event3", "Organizer3"));
            eventRepository.save(new Event("EventTBD", "OrganizerTBD"));
        }
    }


    @GetMapping("/events")
    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    @GetMapping("/events/{eventName}")
    public Event getEventByEventName(@PathVariable String eventName){
        return eventRepository.findEventByEventName(eventName);
    }

    @GetMapping("/events/organizer/{organizer}")
    public List<Event> getEventsByOrganizer(@PathVariable String organizer){
        return eventRepository.findEventsByOrganizer(organizer);
    }

    @PostMapping("/events")
    public Event addEvent(@RequestBody Event event){
        eventRepository.save(event);
        return event;
    }

    @PutMapping("/events")
    public Event updateEvent(@RequestBody Event updatedEvent){
        Event retrievedEvent = eventRepository.findEventByEventName(updatedEvent.getEventName());

        retrievedEvent.setEventName(updatedEvent.getEventName());
        retrievedEvent.setOrganizer(updatedEvent.getOrganizer());

        eventRepository.save(retrievedEvent);

        return retrievedEvent;
    }

    @DeleteMapping("/events/event/{eventName}")
    public ResponseEntity deleteEvent(@PathVariable String eventName){
        Event event = eventRepository.findEventByEventName(eventName);
        if(event!=null){
            eventRepository.delete(event);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }


}

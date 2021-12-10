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

    @GetMapping("/events")
    public List<Event> findAll(){
        return eventRepository.findAll();
    }

    @GetMapping("/events/{eventName}")
    public Event getEventByEventName(@PathVariable String eventName){
        return eventRepository.findEventByEventName(eventName);
    }

    @GetMapping("events/organiser/{organiser}")
    public List<Event> getEventsByOrganiser(@PathVariable String organiser){
        return eventRepository.findEventsByOrganiser(organiser);
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
        retrievedEvent.setOrganiser(updatedEvent.getOrganiser());

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

    @PostConstruct
    public void fillDB(){
        if(eventRepository.count()==0){
            eventRepository.save(new Event("SuperFest 2021", "Thijs Wouters"));
            eventRepository.save(new Event("Bumble 2022", "Gianni De Herdt"));

        }
        System.out.println("The organiser of Bumble 2022 is: " + eventRepository.findEventByEventName("Bumble 2022").getOrganiser());
        System.out.println(eventRepository.findAll());
    }
}

package fact.it.eventservice.controller;

import fact.it.eventservice.model.Event;
import fact.it.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @PostConstruct
    public void fillDB(){
        if(eventRepository.count()==0){
            eventRepository.save(new Event("SuperFest 2021", "Thijs Wouters"));
            eventRepository.save(new Event("Bumble 2022", "Gianni De Herdt"));

        }
        System.out.println("The organiser of HannesPop 2021 is: " + eventRepository.findEventByEventName("HannesPop 2021").getOrganiser());
        System.out.println(eventRepository.findAll());
    }
}

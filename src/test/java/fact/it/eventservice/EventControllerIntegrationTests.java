package fact.it.eventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.eventservice.model.Event;
import fact.it.eventservice.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    private Event event1Organiser1 = new Event("Event1", "Organiser1");
    private Event event2Organiser1 = new Event("Event2", "Organiser1");
    private Event event3Organiser2 = new Event("Event3", "Organiser2");
    private Event eventForDeleting = new Event("EventDelete", "OrganiserDelete");

    @BeforeEach
    public void beforeAllTests(){
        eventRepository.save(event1Organiser1);
        eventRepository.save(event2Organiser1);
        eventRepository.save(event3Organiser2);
        eventRepository.save(eventForDeleting);

    }

    @AfterEach
    public void afterAllTests(){
        eventRepository.delete(event1Organiser1);
        eventRepository.delete(event2Organiser1);
        eventRepository.delete(event3Organiser2);
        eventRepository.delete(eventForDeleting);
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenEvent_whenGetEventByEventName_thenReturnJsonEvent() throws Exception{
        mockMvc.perform(get("/events/{eventName}", "Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event1")))
                .andExpect(jsonPath("$.organiser", is("Organiser1")));
    }

    @Test
    public void givenEvent_whenGetEventsByOrganiser_thenReturnJsonEvents() throws Exception{

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1Organiser1);
        eventList.add(event2Organiser1);

        mockMvc.perform(get("/events/organiser/{organiser}", "Organiser1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("Event1")))
                .andExpect(jsonPath("$[0].organiser", is("Organiser1")))
                .andExpect(jsonPath("$[1].eventName", is("Event2")))
                .andExpect(jsonPath("$[1].organiser", is("Organiser1")));
    }

    @Test
    public void whenPostEvent_thenReturnJsonEvent() throws Exception{
        Event event4Organiser2 = new Event("Event4", "Organiser2");
        mockMvc.perform(post("/events")
                .content(mapper.writeValueAsString(event4Organiser2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event4")))
                .andExpect(jsonPath("$.organiser", is("Organiser2")));

        // Delete event4Organiser2 to prevent it from staying in database. Post still gets tested correctly.
        mockMvc.perform(delete("/events/event/{eventName}", "Event4", "Organiser2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenEvent_whenPutEvent_thenReturnJsonEvent() throws Exception {

        Event updatedEvent = new Event("Event1", "Organiser2");

        mockMvc.perform(put("/events")
                .content(mapper.writeValueAsString(updatedEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event1")))
                .andExpect(jsonPath("$.organiser", is("Organiser2")));
    }

    @Test
    public void givenEvent_whenDeleteEvent_thenStatusOk() throws Exception {

        mockMvc.perform(delete("/events/event/{eventName}", "EventDelete", "OrganiserDelete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



    }

    @Test
    public void givenNoEvent_whenDeleteEvent_thenStatusNotFound() throws Exception {

        mockMvc.perform(delete("/events/event/{eventName}", "EventXXX", "OrganiserXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

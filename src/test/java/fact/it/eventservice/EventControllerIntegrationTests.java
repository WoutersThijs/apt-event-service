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
import static org.hamcrest.Matchers.isA;
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

    private Event event1Organizer1 = new Event("Event1", "Organizer1");
    private Event event2Organizer1 = new Event("Event2", "Organizer1");
    private Event event3Organizer2 = new Event("Event3", "Organizer2");
    private Event eventForDeleting = new Event("EventDelete", "OrganizerDelete");

    @BeforeEach
    public void beforeAllTests(){
        eventRepository.save(event1Organizer1);
        eventRepository.save(event2Organizer1);
        eventRepository.save(event3Organizer2);
        eventRepository.save(eventForDeleting);

    }

    @AfterEach
    public void afterAllTests(){
        eventRepository.delete(event1Organizer1);
        eventRepository.delete(event2Organizer1);
        eventRepository.delete(event3Organizer2);
        eventRepository.delete(eventForDeleting);
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void whenGetAllEvents_thenReturnJsonEvent() throws Exception{
        mockMvc.perform(get("/events"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)));
    }

    @Test
    public void givenEvent_whenGetEventByEventName_thenReturnJsonEvent() throws Exception{
        mockMvc.perform(get("/events/{eventName}", "Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event1")))
                .andExpect(jsonPath("$.organizer", is("Organizer1")));
    }

    @Test
    public void givenEvent_whenGetEventsByOrganizer_thenReturnJsonEvents() throws Exception{

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1Organizer1);
        eventList.add(event2Organizer1);

        mockMvc.perform(get("/events/organizer/{organizer}", "Organizer1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("Event1")))
                .andExpect(jsonPath("$[0].organizer", is("Organizer1")))
                .andExpect(jsonPath("$[1].eventName", is("Event2")))
                .andExpect(jsonPath("$[1].organizer", is("Organizer1")));
    }

    @Test
    public void whenPostEvent_thenReturnJsonEvent() throws Exception{
        Event event4Organizer2 = new Event("Event4", "Organizer2");
        mockMvc.perform(post("/events")
                .content(mapper.writeValueAsString(event4Organizer2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event4")))
                .andExpect(jsonPath("$.organizer", is("Organizer2")));

        // Delete event4Organizer2 to prevent it from staying in database. Post still gets tested correctly.
        mockMvc.perform(delete("/events/event/{eventName}", "Event4", "Organizer2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenEvent_whenPutEvent_thenReturnJsonEvent() throws Exception {

        Event updatedEvent = new Event("Event1", "Organizer2");

        mockMvc.perform(put("/events")
                .content(mapper.writeValueAsString(updatedEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event1")))
                .andExpect(jsonPath("$.organizer", is("Organizer2")));
    }

    @Test
    public void givenEvent_whenDeleteEvent_thenStatusOk() throws Exception {

        mockMvc.perform(delete("/events/event/{eventName}", "EventDelete", "OrganizerDelete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



    }

    @Test
    public void givenNoEvent_whenDeleteEvent_thenStatusNotFound() throws Exception {

        mockMvc.perform(delete("/events/event/{eventName}", "EventXXX", "OrganizerXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

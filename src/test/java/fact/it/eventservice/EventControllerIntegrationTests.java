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

    private Event event1TestOrganizer1 = new Event("TestEvent1", "TestOrganizer1");
    private Event event2TestOrganizer1 = new Event("TestEvent2", "TestOrganizer1");
    private Event event3TestOrganizer2 = new Event("TestEvent3", "TestOrganizer2");
    private Event eventForDeleting = new Event("EventDelete", "OrganizerDelete");

    @BeforeEach
    public void beforeAllTests(){
        eventRepository.deleteAll();
        eventRepository.save(event1TestOrganizer1);
        eventRepository.save(event2TestOrganizer1);
        eventRepository.save(event3TestOrganizer2);
        eventRepository.save(eventForDeleting);

    }

    @AfterEach
    public void afterAllTests(){
        eventRepository.delete(event1TestOrganizer1);
        eventRepository.delete(event2TestOrganizer1);
        eventRepository.delete(event3TestOrganizer2);
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
        mockMvc.perform(get("/events/{eventName}", "TestEvent1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("TestEvent1")))
                .andExpect(jsonPath("$.organizer", is("TestOrganizer1")));
    }

    @Test
    public void givenEvent_whenGetEventsByOrganizer_thenReturnJsonEvents() throws Exception{

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1TestOrganizer1);
        eventList.add(event2TestOrganizer1);

        mockMvc.perform(get("/events/organizer/{organizer}", "TestOrganizer1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("TestEvent1")))
                .andExpect(jsonPath("$[0].organizer", is("TestOrganizer1")))
                .andExpect(jsonPath("$[1].eventName", is("TestEvent2")))
                .andExpect(jsonPath("$[1].organizer", is("TestOrganizer1")));
    }

    @Test
    public void whenPostEvent_thenReturnJsonEvent() throws Exception{
        Event event4TestOrganizer2 = new Event("TestEvent4", "TestOrganizer2");
        mockMvc.perform(post("/events")
                .content(mapper.writeValueAsString(event4TestOrganizer2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("TestEvent4")))
                .andExpect(jsonPath("$.organizer", is("TestOrganizer2")));

        // Delete event4TestOrganizer2 to prevent it from staying in database. Post still gets tested correctly.
        mockMvc.perform(delete("/events/event/{eventName}", "TestEvent4", "TestOrganizer2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenEvent_whenPutEvent_thenReturnJsonEvent() throws Exception {

        Event updatedEvent = new Event("TestEvent1", "TestOrganizer2");

        mockMvc.perform(put("/events")
                .content(mapper.writeValueAsString(updatedEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("TestEvent1")))
                .andExpect(jsonPath("$.organizer", is("TestOrganizer2")));
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

package fact.it.eventservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.eventservice.model.Event;
import fact.it.eventservice.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;

    private ObjectMapper mapper = new ObjectMapper();


    @Test
    public void whenGetAllEvents_thenReturnJsonEvent() throws Exception{
        List<Event> allEvents = eventRepository.findAll();

        given(eventRepository.findAll()).willReturn(allEvents);

        mockMvc.perform(get("/events"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)));
    }


    @Test
    public void givenEvent_whenGetEventByEventName_thenReturnJsonEvent() throws Exception {
        Event event1 = new Event("Event1","Organizer1");

        given(eventRepository.findEventByEventName("Event1")).willReturn(event1);

        mockMvc.perform(get("/events/{eventName}","Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event1")))
                .andExpect(jsonPath("$.organizer",is("Organizer1")));
    }

    @Test
    public void givenEvent_whenGetEventsByOrganizer_thenReturnJsonEvents() throws Exception {
        Event event1Organizer1 = new Event("Event1","Organizer1");
        Event event2Organizer1 = new Event("Event2","Organizer1");

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1Organizer1);
        eventList.add(event2Organizer1);

        given(eventRepository.findEventsByOrganizer("Organizer1")).willReturn(eventList);

        mockMvc.perform(get("/events/organizer/{organizer}","Organizer1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName",is("Event1")))
                .andExpect(jsonPath("$[0].organizer",is("Organizer1")))
                .andExpect(jsonPath("$[1].eventName",is("Event2")))
                .andExpect(jsonPath("$[1].organizer",is("Organizer1")));
    }

    @Test
    public void whenPostEvent_thenReturnJsonEvent() throws Exception{
        Event event3Organizer2 = new Event("Event3","Organizer2");

        mockMvc.perform(post("/events")
                .content(mapper.writeValueAsString(event3Organizer2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event3")))
                .andExpect(jsonPath("$.organizer",is("Organizer2")));
    }

    @Test
    public void givenEvent_whenPutEvent_thenReturnJsonEvent() throws Exception{
        Event event1Organizer1 = new Event("Event1","Organizer1");

        given(eventRepository.findEventByEventName("Event1")).willReturn(event1Organizer1);

        Event updatedEvent = new Event("Event1","Organizer2");

        mockMvc.perform(put("/events")
                .content(mapper.writeValueAsString(updatedEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event1")))
                .andExpect(jsonPath("$.organizer",is("Organizer2")));
    }

    @Test
    public void givenEvent_whenDeleteEvent_thenStatusOk() throws Exception{
        Event eventToBeDeleted = new Event("EventDelete","OrganizerDelete");

        given(eventRepository.findEventByEventName("EventDelete")).willReturn(eventToBeDeleted);

        mockMvc.perform(delete("/events/event/{eventName}","EventDelete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoEvent_whenDeleteEvent_thenStatusNotFound() throws Exception{
        given(eventRepository.findEventByEventName("EventXXX")).willReturn(null);

        mockMvc.perform(delete("/events/event/{eventName}","EventXXX")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

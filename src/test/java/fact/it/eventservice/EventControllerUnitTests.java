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
    public void givenEvent_whenGetEventByEventName_thenReturnJsonEvent() throws Exception {
        Event event1 = new Event("Event1","Organiser1");

        given(eventRepository.findEventByEventName("Event1")).willReturn(event1);

        mockMvc.perform(get("/events/{eventName}","Event1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event1")))
                .andExpect(jsonPath("$.organiser",is("Organiser1")));
    }

    @Test
    public void givenEvent_whenGetEventsByOrganiser_thenReturnJsonEvents() throws Exception {
        Event event1Organiser1 = new Event("Event1","Organiser1");
        Event event2Organiser1 = new Event("Event2","Organiser1");

        List<Event> eventList = new ArrayList<>();
        eventList.add(event1Organiser1);
        eventList.add(event2Organiser1);

        given(eventRepository.findEventsByOrganiser("Organiser1")).willReturn(eventList);

        mockMvc.perform(get("/events/organiser/{organiser}","Organiser1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName",is("Event1")))
                .andExpect(jsonPath("$[0].organiser",is("Organiser1")))
                .andExpect(jsonPath("$[1].eventName",is("Event2")))
                .andExpect(jsonPath("$[1].organiser",is("Organiser1")));
    }

    @Test
    public void whenPostEvent_thenReturnJsonEvent() throws Exception{
        Event event3Organiser2 = new Event("Event3","Organiser2");

        mockMvc.perform(post("/events")
                .content(mapper.writeValueAsString(event3Organiser2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event3")))
                .andExpect(jsonPath("$.organiser",is("Organiser2")));
    }

    @Test
    public void givenEvent_whenPutEvent_thenReturnJsonEvent() throws Exception{
        Event event1Organiser1 = new Event("Event1","Organiser1");

        given(eventRepository.findEventByEventName("Event1")).willReturn(event1Organiser1);

        Event updatedEvent = new Event("Event1","Organiser2");

        mockMvc.perform(put("/events")
                .content(mapper.writeValueAsString(updatedEvent))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName",is("Event1")))
                .andExpect(jsonPath("$.organiser",is("Organiser2")));
    }

    @Test
    public void givenEvent_whenDeleteEvent_thenStatusOk() throws Exception{
        Event eventToBeDeleted = new Event("EventDelete","OrganiserDelete");

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

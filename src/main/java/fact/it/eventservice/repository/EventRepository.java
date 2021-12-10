package fact.it.eventservice.repository;

import fact.it.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findEventByEventName(String eventName);
    List<Event> findEventsByOrganiser(String organiser);
    List<Event> findAll();

}

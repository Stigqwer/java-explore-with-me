package ru.practicum.ewm.compilation;

import lombok.*;
import ru.practicum.ewm.event.Event;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name = "compilations_events",
    joinColumns = @JoinColumn (name = "compilation_id"),
    inverseJoinColumns = @JoinColumn (name = "event_id"))
    private List<Event> events;
    private boolean pinned;
    private String title;
}

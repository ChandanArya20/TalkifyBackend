package in.ineuron.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String textMessage;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private Chat chat;
}

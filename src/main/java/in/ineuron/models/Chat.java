package in.ineuron.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String chatName;
    private String chatImage;
    private Boolean isGroup;

    @ManyToOne
    private User createdBy;

    @ManyToMany
    private Set<User> users;

    @OneToMany
    private List<Message> messages;


}

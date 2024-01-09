package in.ineuron.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupChatRequest {
    private String chatName;
    private String chatImage;
    private List<Long> userIds;
}

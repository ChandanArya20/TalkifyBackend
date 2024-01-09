package in.ineuron.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupChatRequest {
    private String groupName;
    private String groupImage;
    private List<Long> userIds;
}

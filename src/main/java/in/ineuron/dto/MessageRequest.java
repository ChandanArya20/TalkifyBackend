package in.ineuron.dto;

import lombok.Data;

@Data
public class MessageRequest {
    private Long userId;
    private Long chatId;
    private String content;
}

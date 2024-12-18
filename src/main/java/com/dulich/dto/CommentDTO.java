package com.dulich.dto;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor


public class CommentDTO {

    private Long id;

    @JsonProperty("tour_id")
    private Long tourId;  // ID của Tour mà bình luận liên kết

    @JsonProperty("user_id")
    private Long userId;  // ID của User viết bình luận

    private String content;  // Nội dung bình luận

    private Date createdAt;  // Thời gian bình luận
}

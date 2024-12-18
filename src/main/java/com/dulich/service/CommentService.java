package com.dulich.service;

import java.util.List;

import com.dulich.dto.CommentDTO;
import com.dulich.entity.Comment;
import org.springframework.boot.context.config.ConfigDataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



public interface CommentService {
    Comment insertComment(CommentDTO comment);

    void deleteComment(Long commentId);

    void updateComment(Long id , CommentDTO comment)throws ConfigDataNotFoundException;

    List<Comment>getCommentsByUserAndTour(Long userId , Long tourId);

    List<Comment> getCommentsByTour(Long tourId);

}

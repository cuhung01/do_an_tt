package com.dulich.service.impl;

import com.dulich.dto.CommentDTO;
import com.dulich.entity.Comment;
import com.dulich.repository.CommentRepository;
import com.dulich.repository.TourRepository;
import com.dulich.repository.UserRepository;
import com.dulich.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, TourRepository tourRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    @Override
    @Transactional
    public Comment insertComment(CommentDTO commentDTO) {
        Comment newComment = Comment
                .builder()
                .user(userRepository.findById(commentDTO.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .tour(tourRepository.findById(commentDTO.getTourId())
                        .orElseThrow(() -> new RuntimeException("Tour not found")))
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

    @Override
    public List<Comment> getCommentsByUserAndTour(Long userId, Long tourId) {
        return commentRepository.findByUserAndTour(userId, tourId); // Sửa chính tả "Uer" thành "User"
    }


    @Override
    public List<Comment> getCommentsByTour(Long tourId) {
        return commentRepository.findByTourId(tourId, Sort.by(Sort.Direction.DESC, "id"));
    }

}

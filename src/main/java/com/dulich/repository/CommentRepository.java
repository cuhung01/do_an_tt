package com.dulich.repository;

import com.dulich.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUserAndTour(Long userId, Long tourId);
    List<Comment> findByTourId(Long tourId, Sort sort);
}
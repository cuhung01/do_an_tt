package com.dulich.controller.api.admin;

import com.dulich.dto.CommentDTO;
import com.dulich.dto.ResponseDTO;
import com.dulich.entity.Comment;
import com.dulich.service.CommentService;
import com.dulich.service.UserService;
import com.dulich.utilities.SessionUtilities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.net.URI;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;



    @PostMapping("/insertComment")
    public ResponseEntity<?> insertComment(@RequestBody CommentDTO commentDTO) {
        try {
            // Kiểm tra quyền truy cập
            if (SessionUtilities.getUser() == null) {
                if (!this.userService.checkLogin()) {
                    // Chuyển hướng người dùng đến trang đăng nhập bằng mã trạng thái HTTP 3xx (Found)
                    URI redirectUri = URI.create("/login");
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
                }
            }

            // Lấy user_id từ session
            Long userId = SessionUtilities.getUser().getId();
            commentDTO.setUserId(userId);

            // Chèn comment
            commentService.insertComment(commentDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.status(HttpStatus.OK).body("Comment inserted successfully");

        } catch (Exception e) {
            // Trả về phản hồi lỗi khi có ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while inserting the comment");
        }
    }


    @GetMapping("/getAllComment")
    public ResponseDTO getAllComment(
            @RequestParam("user_id") long userId,
            @RequestParam("tour_id") long tourId) {
        List<Comment> comments = commentService.getCommentsByUserAndTour(userId, tourId);
        return new ResponseDTO("Success", comments);
    }

    @PutMapping("updateComment/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO) {
        try {
            // Kiểm tra quyền truy cập
            if (SessionUtilities.getUser() == null) {
                if (!this.userService.checkLogin()) {
                    // Chuyển hướng người dùng đến trang đăng nhập
                    URI redirectUri = URI.create("/login");
                    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
                }
            } else if (!this.userService.checkAdminLogin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
            }

            // Lấy user_id từ session và cập nhật vào commentDTO nếu cần
            Long userId = SessionUtilities.getUser().getId();
            commentDTO.setUserId(userId);

            // Cập nhật comment
            commentService.updateComment(id, commentDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.status(HttpStatus.OK).body("Comment updated successfully");

        } catch (Exception e) {
            // Trả về phản hồi lỗi khi có ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the comment");
        }
    }


    @GetMapping("/getCommentsByTour/{tourId}")
    public List<Comment> getCommentsByTour(@PathVariable Long tourId) {
        return commentService.getCommentsByTour(tourId);
    }

    @DeleteMapping("/deleteComment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            // Kiểm tra quyền truy cập
            if (SessionUtilities.getUser() == null || !this.userService.checkAdminLogin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
            }

            commentService.deleteComment(id);

            // Trả về phản hồi thành công
            return ResponseEntity.status(HttpStatus.OK).body("Delete successfully");

        } catch (Exception e) {
            // Trả về phản hồi lỗi khi có ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment");
        }
    }
}

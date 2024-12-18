package com.dulich.controller;

import com.dulich.dto.BookingDTO;
import com.dulich.dto.TourDTO;
import com.dulich.service.BookingService;
import com.dulich.service.TourService;  // Import đúng TourService
import com.dulich.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;  // Import đúng Model từ Spring
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    TourService tourService;  // Inject đúng TourService

    @Autowired
    BookingService bookingService;

    @GetMapping("/user")
    public String userManage() {
        if(!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }
        return "admin/user";
    }

    @GetMapping("/tour")
    public String tourManage() {
        if(!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }
        return "admin/tour";
    }

    @GetMapping("/booking")
    public String bookingManager() {
        if(!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }
        return "admin/booking";
    }

    @GetMapping("/login")
    public String adminLogin() {
        return "admin/login";
    }

    @GetMapping("/tourStart/{id}")
    public String tourStart(@PathVariable("id") Long id) {
        if(!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }
        return "admin/tourstart";
    }

    @GetMapping("/tourImage/{id}")
    public String tourImage(@PathVariable("id") Long id) {
        if(!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }
        return "admin/tourImage";
    }

    @GetMapping("/logout")
    public String adminLogout() {
        this.userService.adminLogout();
        return "redirect:/admin/login";
    }

    // Phương thức thống kê tour theo điểm đến
    @GetMapping("/tourStatistics")
    public String tourStatistics(Model model) {
        if (!this.userService.checkAdminLogin()) {
            return "redirect:/admin/login";
        }

        List<TourDTO> tourStatistics = tourService.getTourStatisticsByDestination();
        model.addAttribute("tourStatistics", tourStatistics);  // Sử dụng model để truyền dữ liệu

        return "admin/tourStatistics";
    }


    @GetMapping("/bookingStatistics")
    public String getBookingStatistics(@RequestParam(value = "filterType", defaultValue = "day") String filterType,
                                       @RequestParam(value = "date", required = false) String date,
                                       @RequestParam(value = "month", required = false) String month,
                                       @RequestParam(value = "year", required = false) Integer year,
                                       Model model) {

        List<BookingDTO> bookingStatistics;
        List<BookingDTO> completedBookings;


        // Thống kê theo ngày
        if ("day".equals(filterType) && date != null && !date.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                bookingStatistics = bookingService.getStatisticsByDate(localDate);
                completedBookings = bookingService.findCompleteBookingsByDate(localDate);
            } catch (DateTimeParseException e) {
                model.addAttribute("error", "Invalid date format.");
                return "admin/bookingStatistics";
            }
        }
        // Thống kê theo tháng
        else if ("month".equals(filterType) && month != null && !month.isEmpty()) {
            if (year == null) {
                model.addAttribute("error", "Year is required when selecting a month.");
                return "admin/bookingStatistics";
            }

            try {
                // Kiểm tra giá trị tháng
                int monthInt = Integer.parseInt(month);
                if (monthInt < 1 || monthInt > 12) {
                    model.addAttribute("error", "Invalid month value. It should be between 1 and 12.");
                    return "admin/bookingStatistics";
                }

                // Lấy thống kê theo tháng và năm
                bookingStatistics = bookingService.getStatisticsByMonth(monthInt, year);
                completedBookings = bookingService.findCompleteBookingsByMonth(monthInt, year);
            } catch (NumberFormatException e) {
                model.addAttribute("error", "Invalid month or year format.");
                return "admin/bookingStatistics";
            }
        }
        // Thống kê theo năm
        else if ("year".equals(filterType) && year != null) {
            bookingStatistics = bookingService.getStatisticsByYear(year);
            completedBookings = bookingService.findCompleteBookingsByYear(year);
        }
        // Nếu không có điều kiện nào được chọn, trả về thống kê mặc định
        else {
            bookingStatistics = bookingService.getBookingStatistics();
            completedBookings = bookingService.findCompleteBookings();
        }

        model.addAttribute("bookingStatistics", bookingStatistics);
        model.addAttribute("completedBookings", completedBookings);
        return "admin/bookingStatistics";
    }







    @GetMapping("/exportBookingStatistics")
    public ResponseEntity<byte[]> exportBookingStatistics() throws IOException {
        List<BookingDTO> bookingStatistics = bookingService.getBookingStatistics(); // Giả sử bạn lấy toàn bộ dữ liệu thống kê

        // Tạo workbook và sheet trong Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Booking Statistics");

        // Tiêu đề cho các cột
        String[] header = {"Tour ID", "Tên Tour", "Số Người", "Trạng Thái", "Tổng Tiền", "Ngày Booking", "Tổng Tiền Cọc"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
        }

        // Dữ liệu của các booking
        int rowNum = 1;
        for (BookingDTO booking : bookingStatistics) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(booking.getTour_id());
            row.createCell(1).setCellValue(booking.getTen_tour());
            row.createCell(2).setCellValue(booking.getTotalPeople());
            row.createCell(3).setCellValue(getBookingStatus(booking.getTrang_thai())); // Tạo một phương thức để trả về trạng thái
            row.createCell(4).setCellValue(booking.getTotalAmount());
            row.createCell(5).setCellValue(booking.getBooking_at().toString());
            row.createCell(6).setCellValue(booking.getTiencoc());
        }

        // Tạo OutputStream để xuất file Excel
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Trả về file Excel dưới dạng response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=booking_statistics.xlsx");
        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }

    private String getBookingStatus(int status) {
        switch (status) {
            case 0: return "Chờ đặt cọc 20%";
            case 1: return "Đã đặt cọc 20%";
            case 2: return "Đang tiến hành";
            case 3: return "Đã hoàn thành";
            case 4: return "Đã hủy";
            default: return "Không xác định";
        }
    }






}

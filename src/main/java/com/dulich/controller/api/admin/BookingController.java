package com.dulich.controller.api.admin;

import com.dulich.dto.BookingDTO;
import com.dulich.dto.BookingStatisticsDTO;
import com.dulich.dto.ResponseDTO;
import com.dulich.entity.Booking;
import com.dulich.service.BookingService;
import com.dulich.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/getAllBooking")
    public ResponseDTO getAllBooking(@RequestParam(value="trang_thai",required = false) Integer trang_thai,
                                     @RequestParam(value = "ten_tour",required = false) String ten_tour,
                                     @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize,
                                     @RequestParam("pageIndex") Integer pageIndex){


        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        Page<BookingDTO> page = this.bookingService.findAllBooking(trang_thai,ten_tour, PageRequest.of(pageIndex,pageSize));

        return new ResponseDTO("Thành công",page.getContent());

    }




    @GetMapping("/bookingStatistics")
    @ResponseBody  // Trả về JSON thay vì addAttribute
    public BookingStatisticsDTO getBookingStatistics(@RequestParam("filterType") String filterType,
                                                     @RequestParam(value = "date", required = false) String date,
                                                     @RequestParam(value = "month", required = false) String month,
                                                     @RequestParam(value = "year", required = false) Integer year) {
        List<BookingDTO> bookingStatistics;
        List<BookingDTO> completedBookings;

        if ("day".equals(filterType) && date != null) {
            // Thống kê theo ngày
            // Chuyển đổi ngày từ String sang LocalDate
            LocalDate parsedDate = LocalDate.parse(date);
            bookingStatistics = bookingService.getStatisticsByDate(parsedDate); // Sửa phương thức gọi
            completedBookings = bookingService.findCompleteBookingsByDate(parsedDate); // Sửa phương thức gọi
        } else if ("month".equals(filterType) && month != null && year != null) {
            // Thống kê theo tháng
            int parsedMonth = Integer.parseInt(month);  // Chuyển đổi tháng từ String sang int
            bookingStatistics = bookingService.getStatisticsByMonth(parsedMonth, year); // Sửa phương thức gọi
            completedBookings = bookingService.findCompleteBookingsByMonth(parsedMonth, year); // Sửa phương thức gọi
        } else if ("year".equals(filterType) && year != null) {
            // Thống kê theo năm
            bookingStatistics = bookingService.getStatisticsByYear(year); // Phương thức gọi đúng
            completedBookings = bookingService.findCompleteBookingsByYear(year); // Phương thức gọi đúng
        } else {
            // Thống kê mặc định (tất cả)
            bookingStatistics = bookingService.getBookingStatistics();
            completedBookings = bookingService.findCompleteBookings();
        }

        // Gộp dữ liệu và trả về dưới dạng DTO
        return new BookingStatisticsDTO(bookingStatistics, completedBookings);
    }




    @GetMapping("/{id}")
    public ResponseDTO getOneBooking(@PathVariable("id") Long id) {
        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }
        return new ResponseDTO("Thành công",this.bookingService.getBookingById(id));
    }
    @GetMapping("/detail/{id}")
    public ResponseDTO getOneDetailBooking(@PathVariable("id") Long id) {
        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }
        return new ResponseDTO("Thành công",this.bookingService.getBookingDetailById(id));
    }

    @PutMapping("/approve/{id}")
    public ResponseDTO changeStatus(@PathVariable("id") Long id,@RequestParam("trang_thai") Integer trang_thai) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        if(this.bookingService.approveBooking(id,trang_thai)) {
            return new ResponseDTO("Cập nhật thành công",null);
        }
        return new ResponseDTO("Cập nhật thất bại",null);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseDTO deleteBooking(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        if(this.bookingService.deleteBooking(id)) {
            return new ResponseDTO("Xóa thành công",null);
        }

        return new ResponseDTO("Chỉ có thể xóa tour đã hoàn thành và đã hủy",null);
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

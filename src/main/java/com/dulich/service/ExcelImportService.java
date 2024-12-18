package com.dulich.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dulich.entity.Image;
import com.dulich.entity.Tour;
import com.dulich.entity.TourStart;
import com.dulich.repository.ImageRepository;
import com.dulich.repository.TourRepository;
import com.dulich.repository.TourStartRepository;

import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import java.util.UUID;

@Service
public class ExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourStartRepository tourStartRepository;

    @Autowired
    private ImageRepository imageRepository; // Tiêm ImageRepository

    public void importToursFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
        Iterator<Row> rowIterator = sheet.iterator();

        boolean isDataImported = false; // Kiểm tra xem có dữ liệu nào được nhập không

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // Bỏ qua header row
            if (row.getRowNum() == 0) {
                continue;
            }

            // Kiểm tra xem dòng có dữ liệu không
            boolean isRowEmpty = true;
            for (Cell cell : row) {
                if (cell != null && cell.toString().trim().length() > 0) {
                    isRowEmpty = false;
                    break;
                }
            }

            if (isRowEmpty) {
                log.info("Empty row detected at row " + row.getRowNum() + ", stopping import process.");
                break;
            }

            // Đọc dữ liệu từ từng cell và map vào đối tượng Tour
            Tour tour = new Tour();

            if (row.getCell(8) != null) {
                tour.setTen_tour(row.getCell(8).getStringCellValue());
            }

            if (row.getCell(3) != null) {
                tour.setGioi_thieu_tour(row.getCell(3).getStringCellValue());
            }

            if (row.getCell(7) != null) {
                tour.setSo_ngay((int) row.getCell(7).getNumericCellValue());
            }

            if (row.getCell(6) != null) {
                tour.setNoi_dung_tour(row.getCell(6).getStringCellValue());
            }

            if (row.getCell(11) != null) {
                tour.setNgay_khoi_hanh(row.getCell(11).getDateCellValue());
            }

            if (row.getCell(5) != null) {
                tour.setNgay_ket_thuc(row.getCell(5).getDateCellValue());
            }

            if (row.getCell(10) != null) {
                tour.setDiem_den(row.getCell(10).getStringCellValue());
            }

            if (row.getCell(4) != null) {
                tour.setLoai_tour((int) row.getCell(4).getNumericCellValue());
            }

            if (row.getCell(0) != null) {
                String imagePath = row.getCell(0).getStringCellValue();
                imagePath = imagePath.replace("\"", "");
                File originalFile = new File(imagePath);
                String newFileName = UUID.randomUUID().toString() + ".jpg";
                Path targetPath = Paths.get("C:\\Users\\phuhu\\Downloads\\DuLichWeb\\src\\main\\resources\\static\\public\\img\\" + newFileName);                Files.copy(originalFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                tour.setAnh_tour(newFileName); // Lưu tên file vào đối tượng Tour
            }

            if (row.getCell(1) != null) {
                tour.setDiem_khoi_hanh(row.getCell(1).getStringCellValue());
            }

            if (row.getCell(9) != null) {
                tour.setTrang_thai((int) row.getCell(9).getNumericCellValue());
            }

            if (row.getCell(2) != null) {
                tour.setGia_tour((long) row.getCell(2).getNumericCellValue());
            }

            try {
                Tour savedTour = tourRepository.save(tour);
                isDataImported = true;

                if (row.getCell(0) != null) {
                    String imagePath = row.getCell(0).getStringCellValue();
                    imagePath = imagePath.replace("\"", "");
                    File originalFile = new File(imagePath);
                    String newFileName = UUID.randomUUID().toString() + ".jpg";
                    Path targetPath = Paths.get("C:\\Users\\phuhu\\Downloads\\DuLichWeb\\src\\main\\resources\\static\\public\\img\\" + newFileName);
                    Files.copy(originalFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    // Lưu ảnh vào bảng Image
                    Image image = new Image();
                    image.setUrl(newFileName); // Đặt URL ảnh
                    image.setTour(tour); // Gán đối tượng Tour vào Image
                    imageRepository.save(image); // Lưu vào bảng Image
                }
                // Lưu ngày bắt đầu vào bảng tour_start
                if (row.getCell(12) != null) { // Ngày bắt đầu lấy từ cột 12
                    TourStart tourStart = new TourStart();
                    tourStart.setTour_id(savedTour.getId()); // Lấy id từ tour vừa lưu
                    tourStart.setNgay_khoi_hanh(row.getCell(12).getDateCellValue());
                    tourStartRepository.save(tourStart);
                }

            } catch (Exception e) {
                log.error("Error saving tour: " + tour.toString(), e);
            }
        }

        workbook.close();

        if (!isDataImported) {
            log.error("No data was imported from the Excel file.");
        } else {
            log.info("Excel file import completed successfully.");
        }
    }
}

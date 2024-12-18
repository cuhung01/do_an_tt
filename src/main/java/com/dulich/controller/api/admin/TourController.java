package com.dulich.controller.api.admin;

import com.dulich.dto.ResponseDTO;
import com.dulich.dto.TourDTO;
import com.dulich.dto.ToutStartAddDTO;
import com.dulich.entity.Tour;
import com.dulich.entity.TourStart;
import com.dulich.repository.TourStartRepository;
import com.dulich.service.ImageService;
import com.dulich.service.TourService;
import com.dulich.service.UserService;
import com.dulich.utilities.DateUtils;
import com.dulich.utilities.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.dulich.service.ExcelImportService;
import org.springframework.ui.Model;


import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/tour")
public class TourController {

    @Autowired
    private TourService tourService;

    @Autowired
    private TourStartRepository tourStartRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExcelImportService excelImportService;

    @GetMapping("/statistics")
    public List<TourDTO> getTourStatisticsByDestination() {
        return tourService.getTourStatisticsByDestination();
    }



    @PostMapping("/import-tours")
    public ResponseDTO importToursFromExcel(@RequestParam("excelFile") MultipartFile file) {
        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        try {
            excelImportService.importToursFromExcel(file);  // Gọi service để xử lý file Excel
            return new ResponseDTO("Nhập tour từ Excel thành công", null);
        } catch (IOException e) {
            log.error("Lỗi khi nhập tour từ Excel: {}", e.getMessage());
            return new ResponseDTO("Lỗi khi nhập tour từ Excel", null);
        }
    }

    @GetMapping("/getAllTour")
    public ResponseDTO getAllTour(@RequestParam(value="ten_tour",required = false) String ten_tour,
                                  @RequestParam(value="gia_tour_from",required = false) Long gia_tour_from,
                                  @RequestParam(value="gia_tour_to",required = false) Long gia_tour_to,
                                  @RequestParam(value="ngay_khoi_hanh",required = false) Date ngay_khoi_hanh,
                                  @RequestParam(value="loai_tour",required = false) Integer loai_tour,
                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "pageIndex") Integer pageIndex
                                                                                        ) {
        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        Page<TourDTO> page = this.tourService.findAllTourAdmin(ten_tour,gia_tour_from,gia_tour_to,ngay_khoi_hanh,loai_tour, PageRequest.of(pageIndex,pageSize));

        return new ResponseDTO("Thành công",page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseDTO getOneTour(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        TourDTO tour = this.tourService.findTourById(id);

        if(tour!=null) {
            return new ResponseDTO("Thành công",tour);
        }
        return new ResponseDTO("Thất bại" ,null);
    }

    @PostMapping("/test-up-anh")
    public ResponseDTO testUpAnh(@RequestParam("image")MultipartFile image) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String uploadDir = "src/main/resources/static/public/img";


        try {
            // Lưu ảnh vào thư mục "upload"
            String fileName = UUID.randomUUID().toString()+ image.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, image);

            return new ResponseDTO("Thành công",fileName);
        } catch (IOException  e) {
            // Xử lý exception
            log.info("Lỗi upload file: {}",e.getMessage());
        }
        return new ResponseDTO("Thêm thất bại",null);
    }

    @PostMapping("/add/image")
    public ResponseDTO createTourImage(@RequestParam("image")MultipartFile image) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String uploadDir = "main/resources/static/public/img";


        Tour tour = tourService.findFirstByOrderByIdDesc();

        try {
            // Lưu ảnh vào thư mục "upload"
            String fileName = UUID.randomUUID().toString()+ image.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, image);

            // Lưu thông tin của tour vào cơ sở dữ liệu
            tour.setAnh_tour(fileName);
            return new ResponseDTO("Thành công",this.tourService.saveTour(tour));
        } catch (IOException  e) {
            // Xử lý exception
            log.info("Lỗi upload file: {}",e.getMessage());
        }
        return new ResponseDTO("Thêm thất bại",null);
    }

    @PostMapping("/add")
    public ResponseDTO createTour(@RequestBody TourDTO tourDTO) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String[] dataGet = tourDTO.getDiem_den().split("/");
        tourDTO.setDiem_den(dataGet[0]);
        tourDTO.setNgay_khoi_hanh(DateUtils.convertStringToDate(dataGet[1]));
        tourDTO.setNgay_ket_thuc(DateUtils.convertStringToDate(dataGet[2]));

        Tour tour = this.tourService.addTour(tourDTO);
        if(tour!=null) {
            return new ResponseDTO("Thành công",tour);
        }
        return new ResponseDTO("Thêm thất bại",null);

    }


    @PutMapping("/update/image/{id}")
    public ResponseDTO updateTourImage(@PathVariable("id") Long id,@RequestParam("image") MultipartFile image) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String uploadDir = "src/main/resources/static/public/img";


        TourDTO tourDTO = this.tourService.findTourById(id);
        try {
            // Lưu ảnh vào thư mục "upload"
            String fileName = UUID.randomUUID().toString()+ image.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, image);

            // Lưu thông tin của tour vào cơ sở dữ liệu
            tourDTO.setAnh_tour(fileName);
            Tour updateTour = this.tourService.updateTour(tourDTO,id);
            if(updateTour!=null) {
                return new ResponseDTO("Thành công",updateTour);
            }

        } catch (IOException  e) {
            // Xử lý exception
            log.info("Lỗi upload file: {}",e.getMessage());
        }
        return new ResponseDTO("Update thất bại",null);
    }

    @PutMapping("/update/{id}")
    public ResponseDTO updateTour(@PathVariable("id") Long id,@RequestBody TourDTO tourDTO) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String[] dataGet = tourDTO.getDiem_den().split("/");
        tourDTO.setDiem_den(dataGet[0]);
        tourDTO.setNgay_khoi_hanh(DateUtils.convertStringToDate(dataGet[1]));
        tourDTO.setNgay_ket_thuc(DateUtils.convertStringToDate(dataGet[2]));
        Tour updateTour = this.tourService.updateTour(tourDTO,id);
        if(updateTour!=null) {
            return new ResponseDTO("Thành công",updateTour);
        }

        return new ResponseDTO("Update thất bại",null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO deleteTour(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        if(this.tourService.findTourById(id)!=null) {
            if(this.tourService.deleteTour(id)) {
                return new ResponseDTO("Xóa thành công",null);
            }

        }
        return new ResponseDTO("Xóa thất bại",null);
    }

    @GetMapping("/getAllImageOfTour/{id}")
    public ResponseDTO getAllImageOfTour(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        return new ResponseDTO("Thành công",this.imageService.findByTourId(id));
    }

    @PostMapping("/add-image/{id}")
    public ResponseDTO addImage(@PathVariable("id") Long id,@RequestParam("image") MultipartFile image) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        String uploadDir = "src/main/resources/static/public/img";


        try {
            // Lưu ảnh vào thư mục "upload"
            String fileName = UUID.randomUUID()+image.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, image);

            if(this.tourService.findTourById(id)!=null) {

                return new ResponseDTO("Thêm thành công",this.imageService.addToTour(id,fileName));
            }
        } catch (IOException  e) {
            // Xử lý exception
            log.info("Lỗi upload file: {}",e.getMessage());
        }

        return new ResponseDTO("Lỗi khi thêm",null);

    }

    @GetMapping("/StartDate/{id}")
    public ResponseDTO getAllStartDate(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        return new ResponseDTO("Thành công",this.tourStartRepository.getDateStartByTourId(id));
    }


    @DeleteMapping("/StartDate/delete/{id}")
    public ResponseDTO deleteStartDate(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        this.tourStartRepository.deleteById(id);
        return new ResponseDTO("Xóa thành công",null);
    }

    @DeleteMapping("/TourImage/delete/{id}")
    public ResponseDTO deleteTourImage(@PathVariable("id") Long id) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        this.imageService.deleteById(id);

        return new ResponseDTO("Xóa ảnh thành công",null);
    }

    @PostMapping("/add-date/{id}")
    public ResponseDTO addStartDate(@PathVariable("id") Long id , @RequestBody ToutStartAddDTO toutStartAddDTO) {

        if(!this.userService.checkAdminLogin()) {
            return new ResponseDTO("Không có quyền truy cập",null);
        }

        Date ngay_khoi_hanh = DateUtils.convertStringToDate(toutStartAddDTO.getNgay_khoi_hanh());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngay_khoi_hanh);
        calendar.add(Calendar.DAY_OF_MONTH, 1);


        if(this.tourService.findTourById(id)!=null) {

            TourStart tourStart = new TourStart();

            tourStart.setTour_id(id);
            tourStart.setNgay_khoi_hanh(calendar.getTime());

            return new ResponseDTO("Thêm thành công",this.tourStartRepository.save(tourStart));
        }

        return new ResponseDTO("Tour không tồn tại khi thêm",null);
    }

}

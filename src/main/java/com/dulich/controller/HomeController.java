package com.dulich.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dulich.dto.*;
import com.dulich.entity.Comment;
import com.dulich.repository.CommentRepository;
import com.dulich.service.BookingService;
import com.dulich.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.dulich.entity.Image;
import com.dulich.repository.ImageRepository;
import com.dulich.repository.TourStartRepository;
import com.dulich.service.TourService;
import com.dulich.service.UserService;
import com.dulich.utilities.SessionUtilities;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

	@Autowired
	public TourService tourService;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private TourStartRepository tourStartRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private final CommentService commentService;

    public HomeController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("")
	ModelAndView index() {
		ModelAndView mdv = new ModelAndView("user/index");

		Page<TourDTO> tourPage = this.tourService.findAllTour(null, null, null, null, null, PageRequest.of(0, 6));

		List<TourDTO> tours = tourPage.getContent();

		mdv.addObject("tours", tours);


		return mdv;
	}

	@GetMapping("/tour/trong-nuoc")
	ModelAndView tourTrongNuoc(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageIndex,
							   @RequestParam(value = "ten_tour", required = false) String ten_tour,
							   @RequestParam(value = "gia_tour", required = false) Long gia_tour,
							   @RequestParam(value = "ngay_khoi_hanh", required = false) String ngay_khoi_hanh) {
		ModelAndView mdv = new ModelAndView("user/tour1");
		Long gia_tour_from = null;
		Long gia_tour_to = null;
		if (gia_tour != null) {
			gia_tour_from = gia_tour == 0 ? null : (gia_tour == 1 ? 0 : (gia_tour == 2 ? 10000000l : 50000000l));

			gia_tour_to = gia_tour == 0 ? null : (gia_tour == 1 ? 10000000l : (gia_tour == 2 ? 50000000l : 500000000));
		}

		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

		Date ngay_khoi_hanh_value = null;
		try {
			ngay_khoi_hanh_value = ngay_khoi_hanh != null ? format.parse(ngay_khoi_hanh) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Page<TourDTO> tourPage = this.tourService.findAllTour(ten_tour, gia_tour_from, gia_tour_to,
				ngay_khoi_hanh_value, 1, PageRequest.of(pageIndex-1, 12));

		List<TourDTO> tours = tourPage.getContent();

		mdv.addObject("tours", tours);
		return mdv;
	}

	@GetMapping("/tour/ngoai-nuoc")
	ModelAndView tourNgoaiNuoc(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageIndex,
							   @RequestParam(value = "ten_tour", required = false) String ten_tour,
							   @RequestParam(value = "gia_tour", required = false) Long gia_tour,
							   @RequestParam(value = "ngay_khoi_hanh", required = false) String ngay_khoi_hanh) {

		Long gia_tour_from = null;
		Long gia_tour_to = null;
		if (gia_tour != null) {
			gia_tour_from = gia_tour == 0 ? null : (gia_tour == 1 ? 0 : (gia_tour == 2 ? 10000000l : 50000000l));

			gia_tour_to = gia_tour == 0 ? null : (gia_tour == 1 ? 10000000l : (gia_tour == 2 ? 50000000l : 500000000l));
		}

		DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

		Date ngay_khoi_hanh_value = null;
		try {
			ngay_khoi_hanh_value = ngay_khoi_hanh != null ? format.parse(ngay_khoi_hanh) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ModelAndView mdv = new ModelAndView("user/tour2");

		Page<TourDTO> tourPage = this.tourService.findAllTour(ten_tour, gia_tour_from, gia_tour_to, ngay_khoi_hanh_value, 2,
				PageRequest.of(pageIndex-1, 12));

		List<TourDTO> tours = tourPage.getContent();

		mdv.addObject("tours", tours);
		return mdv;
	}

	@GetMapping("/tour/{id}")
	ModelAndView tourDetail(@PathVariable(name = "id", required = true) Long id) {
		ModelAndView mdv = new ModelAndView("user/tour-detail");

		TourDTO tour = this.tourService.findTourById(id);
		List<Image> imageList = this.imageRepository.findByTourId(id);
		List<TourStartDTO> listDate = this.tourStartRepository.getDateStartByTourId(id);
		List<Comment> comments = this.commentService.getCommentsByTour(id); // gọi service đã sửa để lấy bình luận


		mdv.addObject("tour", tour);
		mdv.addObject("listDate", listDate);
		mdv.addObject("imageList", imageList);
		mdv.addObject("comments", comments); // Thêm danh sách bình luận vào model


		return mdv;
	}

	@GetMapping("/login")
	ModelAndView login() {
		if(this.userService.checkLogin()) {
			return this.account();
		}
		ModelAndView mdv = new ModelAndView("user/login");

		return mdv;
	}

	@GetMapping("/register")
	ModelAndView register() {

		if(this.userService.checkLogin()) {
			return this.account();
		}

		ModelAndView mdv = new ModelAndView("user/register");

		return mdv;
	}


	@PostMapping("/login")
	ModelAndView loginAction(LoginDTO login) {


		ModelAndView mdv = new ModelAndView("redirect:/account");

		if(!this.userService.login(login)) {
			ModelAndView mdvErr = new ModelAndView("user/login");
			mdvErr.addObject("err", "Sai thông tin đăng nhập");
			return mdvErr;
		}

		mdv.addObject("user", SessionUtilities.getUser());

		return mdv;
	}

	@PostMapping("/register")
	ModelAndView registerAction(RegisterDTO user) {


		ModelAndView mdv = new ModelAndView("user/login");

		if(!this.userService.register(user)) {
			ModelAndView mdvErr = new ModelAndView("user/register");
			mdvErr.addObject("err", "Đăng ký thất bại");
			return mdvErr;
		}
		mdv.addObject("message", "Đăng ký thành công vui lòng đăng nhập");
		return mdv;
	}

	@GetMapping("/logout")
	ModelAndView logout() {
		SessionUtilities.setUser(null);
		SessionUtilities.setUsername(null);
		return this.index();
	}

	@GetMapping("/account")
	public ModelAndView account() {
		// Tạo đối tượng ModelAndView
		ModelAndView mdv = new ModelAndView();

		// Kiểm tra xem người dùng có đăng nhập hay không
		if (SessionUtilities.getUsername() == null || SessionUtilities.getUser() == null) {
			// Nếu chưa đăng nhập, chuyển hướng tới trang đăng nhập
			mdv.setViewName("redirect:/login");
			return mdv;
		}

		// Nếu đã đăng nhập, chuyển đến trang tài khoản
		mdv.setViewName("user/account");
		mdv.addObject("user", SessionUtilities.getUser()); // Truyền thông tin người dùng vào view

		return mdv;
	}



	@GetMapping("/changepassword")
	ModelAndView changePassword(ChangePasswordDTO changePasswordDTO) {

		ModelAndView mdv = new ModelAndView();
		if(!this.userService.checkLogin()) {
			mdv.setViewName("redirect:/login");
			return mdv;
		}
		mdv.setViewName("user/changepassword");
		return mdv;

	}

	@PostMapping("/changePassword")
	ModelAndView changePasswordAction(ChangePasswordDTO changePasswordDTO) {

		if(changePasswordDTO.getNewPassword()!=null && changePasswordDTO.getOldPassword()!=null) {
			if(this.userService.changePassword(changePasswordDTO)) {
				ModelAndView accountView = this.account();
				accountView.addObject("message","Thay đổi mật khẩu thành công");
				return accountView;
			}
		}

		ModelAndView mdv = new ModelAndView("user/changepassword");
		mdv.addObject("err","Mật khẩu cũ không đúng");

		return mdv;
	}

	@PostMapping("/updateAccount")
	ModelAndView updateAccountAction(UpdateUserDTO updateUserDTO) {

		log.info("update account:{}",updateUserDTO);

		if(this.userService.updateUser(updateUserDTO)) {
			return this.account().addObject("message","Cập nhật thành công!");
		}else {
			return this.account().addObject("message","Có lỗi xảy ra!");
		}

	}

	@GetMapping("/tour/booking/{id}")
	ModelAndView booking(@PathVariable("id") Long tour_id,
						 @RequestParam(value = "ngay_khoi_hanh") String ngay_khoi_hanh,
						 @RequestParam(value = "so_luong_nguoi_lon", required = false, defaultValue = "1") Integer so_luong_nguoi_lon, // Mặc định là 1 nếu không có giá trị
						 @RequestParam(value = "so_luong_tre_em_13", required = false, defaultValue = "0") Integer so_luong_tre_em_13, // Trẻ em 1-3 tuổi mặc định là 0
						 @RequestParam(value = "so_luong_tre_em_46", required = false, defaultValue = "0") Integer so_luong_tre_em_46, // Trẻ em 4-6 tuổi mặc định là 0
						 @RequestParam(value = "tong_tien") Long tong_tien) {
		log.info("ngay khoi hanh log: {}", ngay_khoi_hanh);
		ModelAndView mdv = new ModelAndView("user/booking-checkout");

		if (tour_id == null || ngay_khoi_hanh == null || so_luong_nguoi_lon == null || so_luong_tre_em_13 == null || so_luong_tre_em_46 == null || tong_tien == null) {
			return new ModelAndView("redirect:/err");
		}

		if (SessionUtilities.getUsername() == null) {
			ModelAndView loginView = new ModelAndView("redirect:/login");
			return loginView;
		}

		if(this.tourService.findTourById(tour_id)==null) {
			return new ModelAndView("redirect:/error");
		}

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date ngay_khoi_hanh_value = null;
		try {
			ngay_khoi_hanh_value = ngay_khoi_hanh != null ? format.parse(ngay_khoi_hanh.split(" ")[0]) : null;
		} catch (ParseException e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/error");
		}

		mdv.addObject("user",SessionUtilities.getUser());
		TourDTO tour = this.tourService.findTourById(tour_id);
		mdv.addObject("tour",tour);
		mdv.addObject("ngay_khoi_hanh",ngay_khoi_hanh_value);
		mdv.addObject("so_luong_nguoi_lon",so_luong_nguoi_lon);
		mdv.addObject("so_luong_tre_em_13",so_luong_tre_em_13);
		mdv.addObject("so_luong_tre_em_46",so_luong_tre_em_46);
		mdv.addObject("tong_tien",tong_tien);

		return mdv;

	}


	@PostMapping(value = "/tour/booking/{tour_id}")
	ModelAndView bookingAction(@PathVariable("tour_id") Long tour_id,
							   @RequestParam("so_luong_nguoi_lon") Integer so_luong_nguoi_lon,
							   @RequestParam("so_luong_tre_em_13") Integer so_luong_tre_em_13,
							   @RequestParam("so_luong_tre_em_46") Integer so_luong_tre_em_46,
							   @RequestParam("ngay_khoi_hanh") String ngay_khoi_hanh,
							   @RequestParam("ghi_chu") String ghi_chu,
							   @RequestParam("pt_thanh_toan") Integer pt_thanh_toan) {


		ModelAndView mdv = new ModelAndView();

		BookingDTO bookingDTO = new BookingDTO();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		Date ngay_khoi_hanh_value = null;
		try {
			ngay_khoi_hanh_value = ngay_khoi_hanh != null ? format.parse(ngay_khoi_hanh.split(" ")[0]) : null;
		} catch (ParseException e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/error");
		}

		bookingDTO.setSo_luong_nguoi_lon(so_luong_nguoi_lon);
		bookingDTO.setSo_luong_tre_em_13(so_luong_tre_em_13);
		bookingDTO.setSo_luong_tre_em_46(so_luong_tre_em_46);
		bookingDTO.setGhi_chu(ghi_chu);
		bookingDTO.setNgay_khoi_hanh(ngay_khoi_hanh_value);
		bookingDTO.setPt_thanh_toan(pt_thanh_toan);
		bookingDTO.setTour_id(tour_id);
		bookingDTO.setUser_id(SessionUtilities.getUser().getId());


		if(this.bookingService.addNewBooking(bookingDTO)) {
			mdv.setViewName("redirect:/account");
			return mdv;
		}

		mdv.setViewName("redirect:/error");
		return mdv;
	}


	@GetMapping("/error")
	public String error() {
		return "user/error";
	}

	@GetMapping("/user/tour")
	ModelAndView userTour(){

		if(!this.userService.checkLogin()){
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mdv = new ModelAndView("user/user-booking-list");

		List<BookingDTO> bookingList = this.bookingService.findBookingByUserId(SessionUtilities.getUser().getId());

		mdv.addObject("bookingList",bookingList);

		return mdv;

	}

	@GetMapping("/user/booking/{id}")
	ModelAndView userBookingDetai(@PathVariable Long id) {

		if(!this.userService.checkLogin()){
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mdv = new ModelAndView();

		BookingDTO booking = this.bookingService.getBookingById(id);

		if(booking!=null) {
			mdv.setViewName("user/user-booking");
			mdv.addObject("booking",booking);
			return mdv;
		}

		mdv.setViewName("redirect:/error");
		return mdv;
	}

	@GetMapping("/user/booking/cancel/{id}")
	ModelAndView cancelTour(@PathVariable Long id) {

		if(id==null) {
			return new ModelAndView("redirect:/error");
		}

		String referrer = request.getHeader("Referer");


		log.info("Url trước đó : {}",referrer);

		BookingDTO bookingDTO = this.bookingService.getBookingById(id);

		if(bookingDTO!=null) {

			if(bookingDTO.getTrang_thai()==0 || bookingDTO.getTrang_thai()==1){
				this.bookingService.approveBooking(id,4);
			}

		}else {
			return new ModelAndView("redirect:/error");
		}

		return new ModelAndView("redirect:/user/tour" );
	}

	@GetMapping("/about")
	public String about() {
		return "/user/about";
	}

	@GetMapping("/tin-tuc")
	public String news() {
		return "/user/tin-tuc";
	}

}
package com.dulich.service.impl;

import com.dulich.entity.Image;
import com.dulich.entity.Tour;
import com.dulich.repository.ImageRepository;
import com.dulich.repository.TourRepository;
import com.dulich.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TourRepository tourRepository;

    @Override
    public List<Image> findByTourId(Long id) {
        return this.imageRepository.findByTourId(id);
    }

    @Override
    public Image addToTour(Long tourId, String url) {
        Image image = new Image();

        // Tìm Tour theo tourId
        Tour tour = this.tourRepository.findById(tourId).orElse(null);

        // Nếu tìm thấy Tour, gán Tour vào Image
        if (tour != null) {
            image.setTour(tour);  // Gán đối tượng Tour vào Image
            image.setUrl(url);     // Gán URL ảnh vào Image
            return this.imageRepository.save(image); // Lưu ảnh vào bảng Image
        }

        // Nếu không tìm thấy Tour, trả về null
        return null;
    }

    @Override
    public void deleteById(Long id) {
        this.imageRepository.deleteById(id);
    }
}

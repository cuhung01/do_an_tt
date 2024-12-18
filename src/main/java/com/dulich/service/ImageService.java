package com.dulich.service;

import java.util.List;

import com.dulich.entity.Image;

public interface ImageService {

	List<Image> findByTourId(Long id);

	public Image addToTour(Long tourId,String url);

	void deleteById(Long id);
}

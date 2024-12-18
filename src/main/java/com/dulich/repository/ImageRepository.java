package com.dulich.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dulich.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	// Truy vấn để tìm ảnh theo Tour thông qua đối tượng Tour
	@Query("select i from Image i where i.tour.id = :id")
	List<Image> findByTourId(@Param("id") Long id);

}

package com.orange.signsatwork.biz.persistence.service.impl;

/*
 * #%L
 * Signs at work
 * %%
 * Copyright (C) 2016 Orange
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.orange.signsatwork.biz.domain.Rating;
import com.orange.signsatwork.biz.domain.Video;
import com.orange.signsatwork.biz.domain.Videos;
import com.orange.signsatwork.biz.persistence.model.CommentDB;
import com.orange.signsatwork.biz.persistence.model.RatingDB;
import com.orange.signsatwork.biz.persistence.model.UserDB;
import com.orange.signsatwork.biz.persistence.model.VideoDB;
import com.orange.signsatwork.biz.persistence.repository.*;
import com.orange.signsatwork.biz.persistence.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {
  private final VideoRepository videoRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final RatingRepository ratingRepository;
  private final SignRepository signRepository;

  @Override
  public Videos all() {
    return videosFrom(videoRepository.findAll());
  }

  @Override
  public Video withId(long id) {
    return videoFrom(videoRepository.findOne(id));
  }

  @Override
  public Video createVideoComment(long id, long userId, String commentText) {
    VideoDB videoDB = videoRepository.findOne(id);
    UserDB userDB = userRepository.findOne(userId);

    CommentDB commentDB = new CommentDB();
    commentDB.setCommentDate(new Date());
    commentDB.setText(commentText);
    commentDB.setVideo(videoDB);
    commentDB.setUser(userDB);

    commentRepository.save(commentDB);

    return videoFrom(videoDB);
  }

  @Override
  public Video createVideoRating(long id, long userId, Rating rating) {
    VideoDB videoDB = videoRepository.findOne(id);
    UserDB userDB = userRepository.findOne(userId);

    RatingDB ratingDB = new RatingDB();
    ratingDB.setUser(userDB);
    ratingDB.setVideo(videoDB);
    ratingDB.setRatingDate(new Date());
    ratingDB.setRating(rating);

    ratingRepository.save(ratingDB);

    return videoFrom(videoDB);
  }

  @Override
  public Videos forSign(long signId) {
    return videosFrom(videoRepository.findBySign(signRepository.findOne(signId)));
  }

  @Override
  @Transactional
  public Rating ratingFor(Video video, long userId) {
    VideoDB videoDB = videoRepository.findOne(video.id);
    List<RatingDB> videos = videoDB.getRatings();
    Optional<RatingDB> rating = videos.stream()
            .filter(ratingDB -> ratingDB.getUser().getId() == userId)
            .findAny();

    return rating.isPresent() ? rating.get().getRating() : Rating.Neutral;
  }

  static Videos videosFrom(Iterable<VideoDB> videosDB) {
    List<Video> videos = new ArrayList<>();
    videosDB.forEach(videoDB -> videos.add(videoFrom(videoDB)));
    return new Videos(videos);
  }

  static Video videoFrom(VideoDB videoDB) {
    return new Video(videoDB.getId(), videoDB.getUrl(), videoDB.getCreateDate(), UserServiceImpl.userFromSignView(videoDB.getUser()), null, RatingServiceImpl.ratingsFrom(videoDB.getRatings()));
  }

  static Video videoFromRatingView(VideoDB videoDB) {
    return new Video(videoDB.getId(), videoDB.getUrl(), videoDB.getCreateDate(), null, null, null);
  }

  private VideoDB videoDBFrom(Video video) {
    VideoDB videoDB = new VideoDB(video.url, video.createDate);
    return videoDB;
  }
}

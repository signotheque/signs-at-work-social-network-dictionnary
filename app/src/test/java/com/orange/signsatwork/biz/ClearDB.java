package com.orange.signsatwork.biz;

import com.orange.signsatwork.biz.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClearDB {
  @Autowired
  UserRepository userRepository;
  @Autowired
  SignRepository signRepository;
  @Autowired
  VideoRepository videoRepository;
  @Autowired
  RatingRepository ratingRepository;
  @Autowired
  CommentRepository commentRepository;
  @Autowired
  RequestRepository requestRepository;
  @Autowired
  FavoriteRepository favoriteRepository;
  @Autowired
  CommunityRepository communityRepository;

  public void deleteAll() {
    userRepository.deleteAll();
    signRepository.deleteAll();
    videoRepository.deleteAll();
    ratingRepository.deleteAll();
    commentRepository.deleteAll();
    requestRepository.deleteAll();
    favoriteRepository.deleteAll();
    commentRepository.deleteAll();
  }
}

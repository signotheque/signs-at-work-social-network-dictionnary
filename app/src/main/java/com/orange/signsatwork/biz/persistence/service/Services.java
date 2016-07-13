package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.security.AppSecurityAdmin;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Accessors(fluent = true)
public class Services {
  @Autowired
  private CommentService commentService;
  @Autowired
  private CommunityService communityService;
  @Autowired
  private FavoriteService favoriteService;
  @Autowired
  private RatingService ratingService;
  @Autowired
  private RequestService requestService;
  @Autowired
  private SignService signService;
  @Autowired
  private UserService userService;
  @Autowired
  private VideoService videoService;

  public void clearPersistence() {
    commentService.all().stream().forEach(comment -> commentService.delete(comment));
    communityService.all().stream().forEach(community -> communityService.delete(community));
    favoriteService.all().stream().forEach(favorite -> favoriteService.delete(favorite));
    ratingService.deleteAll();
    videoService.all().stream().forEach(video -> videoService.delete(video));
    requestService.all().stream().forEach(request -> requestService.delete(request));
    signService.all().stream().forEach(sign -> signService.delete(sign));
    userService.all().stream().filter(user -> user.username != AppSecurityAdmin.ADMIN_USERNAME).forEach(user -> userService.delete(user));
  }
}

package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveUser() {
    // given
    User user = testUser.get("user-canRemoveUser");
    Sign sign = services.signService().create(user.id, "sign-canRemoveUser", "//video-canRemoveUser");
    Video video = sign.loadVideos().videos.list().get(0);
    Favorite favorite = services.userService().createUserFavorite(user.id, "favorite-canRemoveUser");
    Request request = services.userService().createUserRequest(user.id, "request-canRemoveUser");
    services.videoService().createVideoRating(video.id, user.id, Rating.Positive);
    Comment comment = services.videoService().createVideoComment(video.id, user.id, "comment-canRemoveUser");

    // then, check we have a correct testing context
    Assertions.assertThat(services.userService().withId(user.id)).isNotNull();
    Assertions.assertThat(services.favoriteService().withId(favorite.id)).isNotNull();
    Assertions.assertThat(services.requestService().withId(request.id)).isNotNull();
    Assertions.assertThat(services.commentService().withId(comment.id)).isNotNull();
    Assertions.assertThat(services.videoService().withId(video.id)).isNotNull();
    Assertions.assertThat(services.ratingService().all().stream().filter(r -> r.primaryKey.user.id == user.id).count()).isEqualTo(1);

    // do
    services.userService().delete(user);

    // then
    Assertions.assertThat(services.userService().all().stream().filter(u -> u.id == user.id).count()).isEqualTo(0);
    Assertions.assertThat(services.favoriteService().all().stream().filter(f -> f.id == favorite.id).count()).isEqualTo(0);
    Assertions.assertThat(services.requestService().all().stream().filter(r -> r.id == request.id).count()).isEqualTo(0);
    Assertions.assertThat(services.commentService().all().stream().filter(c -> c.id == comment.id).count()).isEqualTo(0);
    Assertions.assertThat(services.videoService().all().stream().filter(v -> v.id == video.id).count()).isEqualTo(0);
    Assertions.assertThat(services.ratingService().all().stream().filter(r -> r.primaryKey.user.id == user.id).count()).isEqualTo(0);
  }
}

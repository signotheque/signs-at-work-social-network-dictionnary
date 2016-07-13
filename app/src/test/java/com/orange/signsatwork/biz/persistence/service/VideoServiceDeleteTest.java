package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.*;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveVideo() {
    // given
    User user = testUser.get("user-canRemoveVideo");
    Sign sign = services.signService().create(user.id, "sign-canRemoveVideo", "//video-canRemoveVideo");
    Video video = sign.loadVideos().videos.list().get(0);
    services.videoService().createVideoRating(video.id, user.id, Rating.Negative);
    Comment comment = services.videoService().createVideoComment(video.id, user.id, "comment-canRemoveVideo");

    // do/then
    Assertions.assertThat(services.videoService().all().stream().filter(v -> v.id == video.id).count()).isEqualTo(1);
    Assertions.assertThat(services.userService().withId(user.id).loadVideos().videos.ids()).contains(video.id);
    Assertions.assertThat(services.signService().withId(sign.id).loadVideos().videos.ids()).contains(video.id);
    Assertions.assertThat(services.ratingService().all().stream().filter(r -> r.primaryKey.user.id == user.id && r.primaryKey.video.id == video.id).count()).isEqualTo(1);
    Assertions.assertThat(services.commentService().all().stream().filter(c -> c.id == comment.id).count()).isEqualTo(1);

    // do/then
    services.videoService().delete(video);
    Assertions.assertThat(services.videoService().all().stream().filter(v -> v.id == video.id).count()).isEqualTo(0);
    Assertions.assertThat(services.userService().withId(user.id).loadVideos().videos.ids()).doesNotContain(video.id);
    Assertions.assertThat(services.signService().withId(sign.id).loadVideos().videos.ids()).doesNotContain(video.id);
    Assertions.assertThat(services.ratingService().all().stream().filter(r -> r.primaryKey.user.id == user.id && r.primaryKey.video.id == video.id).count()).isEqualTo(0);
    Assertions.assertThat(services.commentService().all().stream().filter(c -> c.id == comment.id).count()).isEqualTo(0);
  }
}

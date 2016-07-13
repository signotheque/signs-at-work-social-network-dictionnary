package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveComment() {
    // given
    User user = testUser.get("user-canRemoveComment");
    Sign sign = services.signService().create(user.id, "sign-canRemoveComment", "//video-canRemoveComment");
    Video video = sign.loadVideos().videos.list().get(0);

    // do/then
    Comment comment = services.videoService().createVideoComment(video.id, user.id, "comment-canRemoveComment");
    Assertions.assertThat(services.commentService().all().stream().filter(c -> c.id == comment.id).count()).isEqualTo(1);

    // do/then
    services.commentService().delete(comment);
    Assertions.assertThat(services.commentService().all().stream().filter(c -> c.id == comment.id).count()).isEqualTo(0);
  }
}

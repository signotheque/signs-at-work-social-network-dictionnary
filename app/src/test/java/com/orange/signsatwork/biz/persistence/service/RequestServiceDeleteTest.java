package com.orange.signsatwork.biz.persistence.service;

import com.orange.signsatwork.biz.TestUser;
import com.orange.signsatwork.biz.domain.Community;
import com.orange.signsatwork.biz.domain.Request;
import com.orange.signsatwork.biz.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveRequest() {
    // given
    User user = testUser.get("user-canRemoveRequest");

    // do/then
    Request request = services.userService().createUserRequest(user.id, "request-canRemoveRequest");
    Assertions.assertThat(services.requestService().withId(request.id)).isNotNull();
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().requests.ids()).contains(request.id);

    // do/then
    services.requestService().delete(request);
    Assertions.assertThat(services.requestService().all().stream().filter(r -> r.id == request.id).count()).isEqualTo(0);
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().requests.ids()).doesNotContain(request.id);
  }
}

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
public class CommunityServiceDeleteTest {

  @Autowired
  Services services;

  @Autowired
  TestUser testUser;

  @Test
  public void canRemoveCommunity() {
    // given
    User user = testUser.get("user-CanRemoveCommunity");
    Community community = services.communityService().create(Community.create("community-canRemoveCommunity"));

    // do/then
    services.userService().changeUserCommunities(user.id, Arrays.asList(new Long[]{community.id}));
    Assertions.assertThat(services.communityService().all().stream().filter(c -> c.id == community.id).count()).isEqualTo(1);
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().communitiesIds()).contains(community.id);

    // do/then
    services.communityService().delete(community);
    Assertions.assertThat(services.userService().withId(user.id).loadCommunitiesRequestsFavorites().communitiesIds()).doesNotContain(community.id);
    Assertions.assertThat(services.communityService().all().stream().filter(c -> c.id == community.id).count()).isEqualTo(0);
  }
}

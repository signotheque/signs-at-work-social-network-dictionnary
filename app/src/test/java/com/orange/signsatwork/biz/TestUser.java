package com.orange.signsatwork.biz;

import com.orange.signsatwork.biz.domain.User;
import com.orange.signsatwork.biz.persistence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestUser {

  @Autowired
  UserService userService;

  public User get(String username) {
    return userService.create(new User(0, username, "titi", "toto", "titi@toto.org", "", "", null, null, null, null, null, null, null), "pass");
  }
}

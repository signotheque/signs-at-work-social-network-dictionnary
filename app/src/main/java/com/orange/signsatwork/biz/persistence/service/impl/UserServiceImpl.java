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

import com.orange.signsatwork.biz.domain.User;
import com.orange.signsatwork.biz.domain.Users;
import com.orange.signsatwork.biz.persistence.model.CommunityDB;
import com.orange.signsatwork.biz.persistence.model.FavoriteDB;
import com.orange.signsatwork.biz.persistence.model.RequestDB;
import com.orange.signsatwork.biz.persistence.model.UserDB;
import com.orange.signsatwork.biz.persistence.repository.*;
import com.orange.signsatwork.biz.persistence.service.CommunityService;
import com.orange.signsatwork.biz.persistence.service.FavoriteService;
import com.orange.signsatwork.biz.persistence.service.RequestService;
import com.orange.signsatwork.biz.persistence.service.UserService;
import com.orange.signsatwork.biz.security.AppSecurityAdmin;
import com.orange.signsatwork.biz.security.AppSecurityRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, ApplicationListener<AuthenticationSuccessEvent> {
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final CommunityRepository communityRepository;
  private final RequestRepository requestRepository;
  private final FavoriteRepository favoriteRepository;
  private final CommunityService communityService;
  private final RequestService requestService;
  private final FavoriteService favoriteService;

  private final PasswordEncoder passwordEncoder;

  @Override
  public Users all() {
    return usersFrom(userRepository.findAll());
  }

  @Override
  public User withId(long id) {
    return userFrom(withDBId(id));
  }

  @Override
  public User withUserName(String userName) {
    List<UserDB> userDBList = userRepository.findByUsername(userName);
    if (userDBList.size() == 1) {
      return userFrom(userRepository.findByUsername(userName).get(0));
    } else {
      String err = "Error while retrieving user with username = '" + userName + "' (list size = " + userDBList.size() + ")";
      RuntimeException e = new IllegalStateException(err);
      log.error(err, e);
      throw e;
    }
  }

  @Override
  public User create(User user, String password) {
    UserDB userDB = userRepository.save(userDBFrom(user, password));
    return userFrom(userDB);
  }

  @Override
  public User changeUserCommunities(long userId, List<Long> communitiesIds) {
    UserDB userDB = withDBId(userId);
    List<CommunityDB> userCommunities = userDB.getCommunities();
    userCommunities.clear();
    communityRepository.findAll(communitiesIds).forEach(userCommunities::add);
    userDB = userRepository.save(userDB);
    return userFrom(userDB);
  }

  @Override
  public User createUserRequest(long userId, String requestName) {
    UserDB userDB = withDBId(userId);

    RequestDB requestDB = new RequestDB();
    requestDB.setRequestDate(new Date());
    requestDB.setName(requestName);
    requestRepository.save(requestDB);

    userDB.getRequests().add(requestDB);
    userRepository.save(userDB);
    return userFrom(userDB);
  }

  @Override
  public User createUserFavorite(long userId, String favoriteName) {
    UserDB userDB = withDBId(userId);

    FavoriteDB favoriteDB = new FavoriteDB();

    favoriteDB.setName(favoriteName);
    favoriteRepository.save(favoriteDB);

    userDB.getFavorites().add(favoriteDB);
    userRepository.save(userDB);
    return userFrom(userDB);
  }

  @Override
  public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
    String userName = ((UserDetails) authenticationSuccessEvent.getAuthentication().getPrincipal()).getUsername();
    if (!AppSecurityAdmin.isAdmin(userName)) {
      UserDB userDB = userRepository.findByUsername(userName).get(0);
      userDB.setLastConnectionDate(new Date());
      userRepository.save(userDB);
    }
  }

  private UserDB withDBId(long id) {
    return userRepository.findOne(id);
  }

  private Users usersFrom(Iterable<UserDB> usersDB) {
    List<User> users = new ArrayList<>();
    usersDB.forEach(userDB -> users.add(userFrom(userDB)));
    return new Users(users);
  }

  private User userFrom(UserDB userDB) {
    return new User(
            userDB.getId(),
            userDB.getUsername(), userDB.getFirstName(), userDB.getLastName(),
            userDB.getEmail(), userDB.getEntity(), userDB.getActivity(), userDB.getLastConnectionDate(),
            null /* communities lazy loading, use User to load it */,
            null, null,
            communityService, requestService, favoriteService);
  }

  static User userFromSignView(UserDB userDB) {
    return new User(
            userDB.getId(),
            userDB.getUsername(), userDB.getFirstName(), userDB.getLastName(),
            userDB.getEmail(), userDB.getEntity(), userDB.getActivity(), userDB.getLastConnectionDate(),
            null, null, null, null, null, null);
  }
  /**
   * Create a transient UserDB with a hashed password and a ROLE_USER as default
   * @param user user domain object
   * @param password raw password
   * @return the UserDB object to persist
   */
  private UserDB userDBFrom(User user, String password) {
    UserDB userDB = new UserDB(user.username, passwordEncoder.encode(password), user.firstName, user.lastName, user.email, user.entity, user.activity);
    addUserRole(userDB);
    return userDB;
  }

  private void addUserRole(UserDB userDB) {
    userDB.getUserRoles().add(
            userRoleRepository.findByRole(AppSecurityRoles.Role.ROLE_USER.toString()).get(0)
    );
  }
}

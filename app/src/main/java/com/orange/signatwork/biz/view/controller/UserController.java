package com.orange.signatwork.demo.biz.view.controller;

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

import com.orange.signatwork.demo.biz.domain.User;
import com.orange.signatwork.demo.biz.persistence.service.CommunityService;
import com.orange.signatwork.demo.biz.persistence.service.MessageByLocaleService;
import com.orange.signatwork.demo.biz.persistence.service.RequestService;
import com.orange.signatwork.demo.biz.persistence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class UserController {

  @Autowired
  private UserService userService;
  @Autowired
  private CommunityService communityService;
  @Autowired
  private RequestService requestService;
  @Autowired
  MessageByLocaleService messageByLocaleService;

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/profile")
  public String userDetails(Principal principal, Model model) {
    User user = userService.withUserName(principal.getName());
    model.addAttribute("title", messageByLocaleService.getMessage("profile"));
    model.addAttribute("user", user);
    return "profile";
  }
}

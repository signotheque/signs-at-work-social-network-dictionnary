package com.orange.spring.demo.biz.view.controller;

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

import com.orange.spring.demo.biz.domain.*;
import com.orange.spring.demo.biz.persistence.service.MessageByLocaleService;
import com.orange.spring.demo.biz.persistence.service.SignService;
import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.persistence.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
public class RatingController {

  @Autowired
  private UserService userService;

  @Autowired
  private SignService signService;

  @Autowired
  private VideoService videoService;

  @Autowired
  MessageByLocaleService messageByLocaleService;

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/rate-positive", method = RequestMethod.POST)
  public String ratePositive(@PathVariable long signId, Principal principal) {
    return doRate(signId, principal, Rating.Positive);
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/rate-neutral", method = RequestMethod.POST)
  public String rateNeutral(@PathVariable long signId, Principal principal) {
    return doRate(signId, principal, Rating.Neutral);
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/rate-negative", method = RequestMethod.POST)
  public String rateNegative(@PathVariable long signId, Principal principal) {
    return doRate(signId, principal, Rating.Negative);
  }

  private String doRate(long signId, Principal principal, Rating rating) {
    User user = userService.withUserName(principal.getName());
    Sign sign = signService.withId(signId);
    sign.changeRate(user.id, rating);

    return "redirect:/sign/" + signId;
  }
}

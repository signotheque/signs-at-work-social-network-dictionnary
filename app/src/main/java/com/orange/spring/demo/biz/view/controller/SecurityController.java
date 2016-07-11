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

import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.security.AppSecurityAdmin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SecurityController {

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  static Map<String, Object> authentModel(Principal principal, UserService userService) {
    boolean authenticated = principal != null && principal.getName() != null;
    Map<String, Object> modelMap = authenticatedModel(authenticated);
    modelMap.put("authenticatedUsername",
            authenticated ? principal.getName() : "Please sign in");
    modelMap.put("isAdmin", authenticated && isAdmin(principal));
    if (authenticated && !isAdmin(principal)) {
      modelMap.put("user", userService.withUserName(principal.getName()));
    }
    return modelMap;
  }

  static Map<String, Object> authenticatedModel(boolean isAuthenticated) {
    Map<String, Object> modelMap = new HashMap<>();
    modelMap.put("isAuthenticated", isAuthenticated);
    return modelMap;
  }

  private static boolean isAdmin(Principal principal) {
    return AppSecurityAdmin.isAdmin(principal.getName());
  }
}

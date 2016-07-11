package com.orange.spring.demo.biz.view.model;

import com.orange.spring.demo.biz.persistence.service.UserService;
import com.orange.spring.demo.biz.security.AppSecurityAdmin;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class AuthentModel {

  public static void addAuthenticatedModel(Model model, boolean isAuthenticated) {
    model.addAllAttributes(authenticatedModel(isAuthenticated));
  }

  public static void addAuthentModelWithUserDetails(Model model, Principal principal, UserService userService) {
    boolean authenticated = principal != null && principal.getName() != null;
    addAuthenticatedModel(model, authenticated);
    model.addAttribute("authenticatedUsername",
            authenticated ? principal.getName() : "Please sign in");
    model.addAttribute("isAdmin", authenticated && isAdmin(principal));
    if (authenticated && !isAdmin(principal)) {
      model.addAttribute("user", userService.withUserName(principal.getName()));
    }
  }

  private static Map<String, Object> authenticatedModel(boolean isAuthenticated) {
    Map<String, Object> modelMap = new HashMap<>();
    modelMap.put("isAuthenticated", isAuthenticated);
    return modelMap;
  }

  private static boolean isAdmin(Principal principal) {
    return AppSecurityAdmin.isAdmin(principal.getName());
  }
}

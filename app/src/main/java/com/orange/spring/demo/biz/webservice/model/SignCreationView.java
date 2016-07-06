package com.orange.spring.demo.biz.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignCreationView {
  private String signName;
  private String videoUrl;
}

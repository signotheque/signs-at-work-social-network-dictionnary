package com.orange.signatwork.demo.biz.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignCreationView {
  private String signName;
  private String videoUrl;
}

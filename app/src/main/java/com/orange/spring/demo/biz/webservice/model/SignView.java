package com.orange.spring.demo.biz.webservice.model;

import com.orange.spring.demo.biz.domain.Sign;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignView {
  private long id;
  private String signName;
  private String videoUrl;

  public SignView(Sign sign) {
    this(sign.id, sign.name, sign.url);
  }
}

package com.orange.signatwork.demo.biz.persistence.service.impl;

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

import com.orange.signatwork.demo.biz.domain.RatingDat;
import com.orange.signatwork.demo.biz.domain.RatingId;
import com.orange.signatwork.demo.biz.domain.Ratings;
import com.orange.signatwork.demo.biz.persistence.model.RatingDB;
import com.orange.signatwork.demo.biz.persistence.service.RatingService;
import com.orange.signatwork.demo.biz.persistence.model.RatingDBId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {


  static Ratings ratingsFrom(Iterable<RatingDB> ratingsDB) {
    List<RatingDat> ratingDats = new ArrayList<>();
    ratingsDB.forEach(ratingDB -> ratingDats.add(ratingFrom(ratingDB)));
    return new Ratings(ratingDats);
  }

  static RatingDat ratingFrom(RatingDB ratingDB) {
    return new RatingDat(ratingIdFrom(ratingDB.getPrimaryKey()), ratingDB.getRatingDate(), ratingDB.getRating());
  }

 static RatingId ratingIdFrom(RatingDBId ratingDBId) {
   return new RatingId(VideoServiceImpl.videoFromRatingView(ratingDBId.getVideo()),UserServiceImpl.userFromSignView(ratingDBId.getUser()));
 }

}

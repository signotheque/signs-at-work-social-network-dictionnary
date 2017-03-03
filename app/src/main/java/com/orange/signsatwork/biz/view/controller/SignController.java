package com.orange.signsatwork.biz.view.controller;

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

import com.orange.signsatwork.biz.domain.*;
import com.orange.signsatwork.biz.persistence.model.*;
import com.orange.signsatwork.biz.persistence.service.MessageByLocaleService;
import com.orange.signsatwork.biz.persistence.service.Services;
import com.orange.signsatwork.biz.persistence.service.SignService;
import com.orange.signsatwork.biz.persistence.service.VideoService;
import com.orange.signsatwork.biz.view.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class SignController {
  private static final boolean SHOW_ADD_FAVORITE = true;
  private static final boolean HIDE_ADD_FAVORITE = false;

  private static final String HOME_URL = "/";
  private static final String SIGNS_URL = "/signs";
  private static final String SIGN_URL = "/sign";


  @Autowired
  private Services services;

  @Autowired
  MessageByLocaleService messageByLocaleService;

  @RequestMapping(value = SIGNS_URL)
  public String signs(@RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);
    fillModelWithSigns(model, principal);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("isAll", true);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("all"));
    model.addAttribute("classDropdownTitle", " signe pull-left");
    model.addAttribute("classDropdownSize", "adjust_size btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }


  @RequestMapping(value = "/sec/signs/alphabetic")
  public String signsAndRequestInAlphabeticalOrder(@RequestParam("isAlphabeticAsc") boolean isAlphabeticAsc, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);
    final User user = AuthentModel.isAuthenticated(principal) ? services.user().withUserName(principal.getName()) : null;
    List<Object[]> querySigns;

    if (isAlphabeticAsc == true) {
      querySigns = services.sign().SignsAndRequestsAlphabeticalOrderDescSignsView(user.id);
      model.addAttribute("isAlphabeticDesc", true);
      model.addAttribute("isAlphabeticAsc", false);
      model.addAttribute("classDropdownDirection", "  direction_down pull-right");

    } else {
      querySigns = services.sign().SignsAndRequestsAlphabeticalOrderAscSignsView(user.id);
      model.addAttribute("isAlphabeticAsc", true);
      model.addAttribute("isAlphabeticDesc", false);
      model.addAttribute("classDropdownDirection", "  direction_up pull-right");
    }


    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<SignView2> signViews = signViewsData.stream()
      .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, signInFavorite, user))
      .collect(Collectors.toList());


    fillModelWithFavorites(model, user);
    model.addAttribute("signsView", signViews);
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("alphabetic"));
    model.addAttribute("classDropdownTitle", " signe pull-left");
    model.addAttribute("classDropdownSize", "btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }

  @RequestMapping(value = "/sec/signs/{favoriteId}")
  public String signsInFavorite(@PathVariable long favoriteId, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    User user = services.user().withUserName(principal.getName());

    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);
    Favorite favorite = services.favorite().withId(favoriteId);
    List<Object[]> querySigns = services.sign().SignsForFavoriteView(favoriteId);
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<SignView2> signViews = signViewsData.stream()
      .map(signViewData -> new SignView2(
        signViewData,
        signWithCommentList.contains(signViewData.id),
        SignView2.createdAfterLastDeconnection(signViewData.createDate, user == null ? null : user.lastDeconnectionDate),
        signWithView.contains((signViewData.id)),
        signWithPositiveRate.contains(signViewData.id),
        signInFavorite.contains(signViewData.id))
      )
      .collect(Collectors.toList());

    SignsViewSort2 signsViewSort2 = new SignsViewSort2();
    signViews = signsViewSort2.sort(signViews, false);

    model.addAttribute("signsView", signViews);
    fillModelWithFavorites(model, user);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("favoriteId", favoriteId);
    model.addAttribute("dropdownTitle", favorite.name);
    model.addAttribute("classDropdownTitle", " favorite_signe pull-left");
    model.addAttribute("classDropdownSize", "adjust_size btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);


    return "signs";
  }


  @RequestMapping(value = "/sec/signs/mostcommented")
  public String signsMostCommented(@RequestParam("isMostCommented") boolean isMostCommented, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    User user = services.user().withUserName(principal.getName());

    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);

    List<Object[]> querySigns = services.sign().SignsForSignsView();
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithCommentList;
    if (isMostCommented == true) {
      signWithCommentList = Arrays.asList(services.sign().lowCommented());
      model.addAttribute("isLowCommented", true);
      model.addAttribute("isMostCommented", false);
      model.addAttribute("classDropdownDirection", "  direction_down pull-right");

    } else {
      signWithCommentList = Arrays.asList(services.sign().mostCommented());
      model.addAttribute("isMostCommented", true);
      model.addAttribute("isLowCommented", false);
      model.addAttribute("classDropdownDirection", "  direction_up pull-right");
    }

    List<SignViewData> commented = signViewsData.stream()
      .filter(signViewData -> signWithCommentList.contains(signViewData.id))
      .sorted(new CommentOrderComparator(signWithCommentList))
      .collect(Collectors.toList());


    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));


    List<SignView2> signViews = commented.stream()
      .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, signInFavorite, user))
      .collect(Collectors.toList());


    model.addAttribute("signsView", signViews);
    fillModelWithFavorites(model, user);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("most_commented"));
    model.addAttribute("classDropdownTitle", " most_active pull-left");
    model.addAttribute("classDropdownSize", "btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }

  @RequestMapping(value = "/sec/signs/mostrating")
  public String signsMostRating(@RequestParam("isMostRating") boolean isMostRating, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    User user = services.user().withUserName(principal.getName());

    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);

    List<Object[]> querySigns = services.sign().SignsForSignsView();
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithRatingList;
    if (isMostRating == true) {
      signWithRatingList = Arrays.asList(services.sign().lowRating());
      model.addAttribute("isLowRating", true);
      model.addAttribute("isMostRating", false);
      model.addAttribute("classDropdownDirection", "  direction_down pull-right");

    } else {
      signWithRatingList = Arrays.asList(services.sign().mostRating());
      model.addAttribute("isMostRating", true);
      model.addAttribute("isLowRating", false);
      model.addAttribute("classDropdownDirection", "  direction_up pull-right");

    }

    List<SignViewData> rating = signViewsData.stream()
      .filter(signViewData -> signWithRatingList.contains(signViewData.id))
      .sorted(new CommentOrderComparator(signWithRatingList))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<SignView2> signViews = rating.stream()
      .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, signInFavorite, user))
      .collect(Collectors.toList());


    model.addAttribute("signsView", signViews);


    fillModelWithFavorites(model, user);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("most_rating"));
    model.addAttribute("classDropdownTitle", " sentiment_positif pull-left");
    model.addAttribute("classDropdownSize", "btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }

  @RequestMapping(value = "/sec/signs/mostviewed")
  public String signsMostViewed(@RequestParam("isMostViewed") boolean isMostViewed, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    User user = services.user().withUserName(principal.getName());

    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);

    List<Object[]> querySigns = services.sign().SignsForSignsView();
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithViewedList;
    if (isMostViewed == true) {
      signWithViewedList = Arrays.asList(services.sign().lowViewed());
      model.addAttribute("isLowViewed", true);
      model.addAttribute("isMostViewed", false);
      model.addAttribute("classDropdownDirection", "  direction_down pull-right");
    } else {
      signWithViewedList = Arrays.asList(services.sign().mostViewed());
      model.addAttribute("isMostViewed", true);
      model.addAttribute("isLowViewed", false);
      model.addAttribute("classDropdownDirection", "  direction_up pull-right");
    }

    List<SignViewData> viewed = signViewsData.stream()
      .filter(signViewData -> signWithViewedList.contains(signViewData.id))
      .sorted(new CommentOrderComparator(signWithViewedList))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<SignView2> signViews = viewed.stream()
      .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, signInFavorite, user))
      .collect(Collectors.toList());


    model.addAttribute("signsView", signViews);


    fillModelWithFavorites(model, user);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostRecent", false);
    model.addAttribute("isLowRecent", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("most_viewed"));
    model.addAttribute("classDropdownTitle", " most_viewed pull-left");
    model.addAttribute("classDropdownSize", "btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }

  @RequestMapping(value = "/sec/signs/mostrecent")
  public String signsMostRecent(@RequestParam("isMostRecent") boolean isMostRecent, @RequestParam("isSearch") boolean isSearch, Principal principal, Model model) {
    User user = services.user().withUserName(principal.getName());

    fillModelWithContext(model, "sign.list", principal, SHOW_ADD_FAVORITE, HOME_URL);

    List<Object[]> querySigns;
    if (isMostRecent == true) {
      querySigns = services.sign().lowRecent(user.lastDeconnectionDate);
      model.addAttribute("isLowRecent", true);
      model.addAttribute("isMostRecent", false);
      model.addAttribute("classDropdownDirection", "  direction_down pull-right");
    } else {
     querySigns = services.sign().mostRecent(user.lastDeconnectionDate);
      model.addAttribute("isMostRecent", true);
      model.addAttribute("isLowRecent", false);
      model.addAttribute("classDropdownDirection", "  direction_up pull-right");
    }
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<SignView2> signViews = signViewsData.stream()
      .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, signInFavorite, user))
      .collect(Collectors.toList());


    model.addAttribute("signsView", signViews);


    fillModelWithFavorites(model, user);
    model.addAttribute("requestCreationView", new RequestCreationView());
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("isAll", false);
    model.addAttribute("isMostCommented", false);
    model.addAttribute("isLowCommented", false);
    model.addAttribute("isMostRating", false);
    model.addAttribute("isLowRating", false);
    model.addAttribute("isMostViewed", false);
    model.addAttribute("isLowViewed", false);
    model.addAttribute("isAlphabeticAsc", false);
    model.addAttribute("isAlphabeticDesc", false);
    model.addAttribute("dropdownTitle", messageByLocaleService.getMessage("most_recent"));
    model.addAttribute("classDropdownTitle", "  most_recent pull-left");
    model.addAttribute("classDropdownSize", "btn btn-default dropdown-toggle");
    model.addAttribute("isSearch", isSearch);

    return "signs";
  }

  @RequestMapping(value = "/sign/{signId}")
  public String sign(HttpServletRequest req, @PathVariable long signId, Principal principal, Model model) {
    final User user = AuthentModel.isAuthenticated(principal) ? services.user().withUserName(principal.getName()) : null;

    String referer = req.getHeader("Referer");
    String backUrl = referer != null && referer.contains(SIGNS_URL) ? SIGNS_URL + "/?isSearch=false" : HOME_URL;
    fillModelWithContext(model, "sign.info", principal, SHOW_ADD_FAVORITE, backUrl);

    List<Object[]> querySigns = services.sign().AllVideosForSign(signId);
    List<VideoViewData> videoViewsData = querySigns.stream()
      .map(objectArray -> new VideoViewData(objectArray))
      .collect(Collectors.toList());
    if (videoViewsData.size() == 1) {
      return showVideo(signId, videoViewsData.get(0).videoId);
    } else {
      model.addAttribute("title", videoViewsData.get(0).signName);


      List<Long> videoWithCommentList = Arrays.asList(services.sign().NbCommentForAllVideoBySign(signId));

      List<Long> videoWithPostiveRateList = Arrays.asList(services.sign().NbPositiveRateForAllVideoBySign(signId));

      List<VideoView2> videoViews;
      List<Long> signInFavorite = new ArrayList<>();
      if (user != null) {
        signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));
        List<Long> finalSignInFavorite = signInFavorite;
        videoViews = videoViewsData.stream()
          .map(videoViewData -> buildVideoView(videoViewData, videoWithCommentList, videoWithPostiveRateList, finalSignInFavorite, user))
          .collect(Collectors.toList());
      } else {
        List<Long> finalSignInFavorite1 = signInFavorite;
        videoViews = videoViewsData.stream()
          .map(videoViewData -> buildVideoView(videoViewData, videoWithCommentList, videoWithPostiveRateList, finalSignInFavorite1, user))
          .collect(Collectors.toList());
      }


      VideosViewSort videosViewSort = new VideosViewSort();
      videoViews = videosViewSort.sort(videoViews);

      model.addAttribute("videosView", videoViews);
      return "videos";
    }

  }

  @RequestMapping(value = "/sign/{signId}/{videoId}")
  public String video(HttpServletRequest req, @PathVariable long signId, @PathVariable long videoId, Principal principal, Model model) {

    Boolean isVideoCreatedByMe = false;
    String referer = req.getHeader("Referer");
    String backUrl;


    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));
    model.addAttribute("showAddFavorite", SHOW_ADD_FAVORITE && AuthentModel.isAuthenticated(principal));
    model.addAttribute("commentCreationView", new CommentCreationView());
    model.addAttribute("favoriteCreationView", new FavoriteCreationView());


    Sign sign = services.sign().withIdSignsView(signId);
    if (referer != null ) {
      if (referer.contains(SIGNS_URL)) {
        backUrl = SIGNS_URL + "/?isSearch=false";
      }  else if (referer.contains(SIGN_URL)) {

        if (sign.nbVideo == 1) {
          backUrl = HOME_URL;
        } else {
          backUrl = signUrl(signId);
        }
      } else {
        backUrl = HOME_URL;
      }
    } else {
      backUrl = HOME_URL;
    }
    model.addAttribute("backUrl", backUrl);

    Video video = services.video().withId(videoId);
    if (principal != null) {
      User user = services.user().withUserName(principal.getName());
      Object[] queryRating = services.video().RatingForVideoByUser(videoId, user.id);
      RatingData ratingData = new RatingData(queryRating);
      model.addAttribute("ratingData", ratingData);
      List<Object[]> queryAllComments = services.video().AllCommentsForVideo(videoId);
      List<CommentData> commentDatas = queryAllComments.stream()
        .map(objectArray -> new CommentData(objectArray))
        .collect(Collectors.toList());
      model.addAttribute("commentDatas", commentDatas);
      fillModelWithFavorites(model, user);
      if (video.user.id == user.id) {
        isVideoCreatedByMe = true;
      }
    }

    if (!isVideoCreatedByMe) {
      if ((referer != null) && referer.contains("detail")) {
      } else {
        services.video().increaseNbView(videoId);
      }
    }

    if ((video.idForName == 0) || (sign.nbVideo == 1 )){
      model.addAttribute("title", sign.name + " / " + messageByLocaleService.getMessage("info"));
      model.addAttribute("videoName", sign.name);
    } else {
      model.addAttribute("title", sign.name + " (" + video.idForName + ")" + " / " + messageByLocaleService.getMessage("info"));
      model.addAttribute("videoName", sign.name + " (" + video.idForName + ")");
    }


    model.addAttribute("signView", sign);
    model.addAttribute("videoView", video);
    model.addAttribute("isVideoCreatedByMe", isVideoCreatedByMe);

    return "sign";
  }



  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/{videoId}/detail")
  public String videoDetail(@PathVariable long signId, @PathVariable long videoId, Principal principal, Model model)  {
    Boolean isVideoCreatedByMe = false;

    model.addAttribute("backUrl", videoUrl(signId, videoId));
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));
    model.addAttribute("showAddFavorite", SHOW_ADD_FAVORITE && AuthentModel.isAuthenticated(principal));

    model.addAttribute("favoriteCreationView", new FavoriteCreationView());
    Sign sign = services.sign().withIdSignsView(signId);
    Video video = services.video().withId(videoId);
    if (principal != null) {
      User user = services.user().withUserName(principal.getName());
      Object[] queryRating = services.video().RatingForVideoByUser(videoId, user.id);
      RatingData ratingData = new RatingData(queryRating);
      model.addAttribute("ratingData", ratingData);
      List<Object[]> queryAllComments = services.video().AllCommentsForVideo(videoId);
      List<CommentData> commentDatas = queryAllComments.stream()
        .map(objectArray -> new CommentData(objectArray))
        .collect(Collectors.toList());
      model.addAttribute("commentDatas", commentDatas);
      fillModelWithFavorites(model, user);
      if (video.user.id == user.id) {
        isVideoCreatedByMe = true;
      }
    }
    List<Object[]> queryAllVideosHistory = services.sign().AllVideosHistoryForSign(signId);
    List<VideoHistoryData> videoHistoryDatas = queryAllVideosHistory.stream()
      .map(objectArray -> new VideoHistoryData(objectArray))
      .collect(Collectors.toList());
    model.addAttribute("videoHistoryDatas", videoHistoryDatas);

    if ((video.idForName == 0) || (sign.nbVideo == 1 )){
      model.addAttribute("title", sign.name + " / " + messageByLocaleService.getMessage("détail"));
      model.addAttribute("videoName", sign.name);
    } else {
      model.addAttribute("title", sign.name + " (" + video.idForName + ")" + " / " + messageByLocaleService.getMessage("détail"));
      model.addAttribute("videoName", sign.name + " (" + video.idForName + ")");
    }

    model.addAttribute("signView", sign);
    model.addAttribute("signCreationView", new SignCreationView());
    model.addAttribute("videoView", video);
    model.addAttribute("isVideoCreatedByMe", isVideoCreatedByMe);

    return "sign-detail";
  }


  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/{videoId}/video-associates")
  public String associatesVideo(@PathVariable long signId, @PathVariable long videoId, Principal principal, Model model)  {
    fillModelWithContext(model, "sign.associated", principal, HIDE_ADD_FAVORITE, signUrl(signId));
    User user = services.user().withUserName(principal.getName());

    List<Object[]> queryVideos = services.video().AssociateVideos(videoId, videoId);
    List<VideoViewData> videoViewsData = queryVideos.stream()
      .map(objectArray -> new VideoViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> videoWithCommentList = Arrays.asList(services.sign().NbCommentForAllVideoBySign(signId));

    List<Long> videoWithPostiveRateList = Arrays.asList(services.sign().NbPositiveRateForAllVideoBySign(signId));

    List<Long> signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));

    List<VideoView2> videoViews = videoViewsData.stream()
      .map(videoViewData -> buildVideoView(videoViewData, videoWithCommentList, videoWithPostiveRateList, signInFavorite, user))
      .collect(Collectors.toList());


    VideosViewSort videosViewSort = new VideosViewSort();
    videoViews = videosViewSort.sort(videoViews);

    model.addAttribute("videosView", videoViews);

    return "video-associates";
  }


  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/{videoId}/video-associate-form")
  public String associateVideo(@PathVariable long signId, @PathVariable long videoId, Principal principal, Model model)  {
    fillModelWithContext(model, "sign.associate-form", principal, HIDE_ADD_FAVORITE, signUrl(signId));
    VideoService videoService = services.video();
    Video video = videoService.withIdLoadAssociates(videoId);

    VideoProfileView videoProfileView = new VideoProfileView(video);
    model.addAttribute("videoProfileView", videoProfileView);

    List<Object[]> querySigns = services.sign().AllVideosForAllSigns();
    List<VideoViewData> videoViewsData = querySigns.stream()
      .map(objectArray -> new VideoViewData(objectArray))
      .filter(v -> v.videoId != videoId)
      .collect(Collectors.toList());

    List<VideoViewData> videoForSameSigne = videoViewsData.stream()
      .filter(v -> v.signId == signId)
      .collect(Collectors.toList());

    videoViewsData.removeAll(videoForSameSigne);

    List<VideoViewData> videoAssociate = videoViewsData.stream()
      .filter(v -> video.associateVideosIds.contains(v.videoId))
      .collect(Collectors.toList());

    videoViewsData.removeAll(videoAssociate);


    List<VideoViewData> sortedVideos = new ArrayList<>();
    sortedVideos.addAll(videoForSameSigne);
    sortedVideos.addAll(videoAssociate);
    sortedVideos.addAll(videoViewsData);

    model.addAttribute("videosView", sortedVideos);
    model.addAttribute("signId", signId);
    return "video-associate-form";
  }


  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/{signId}/{videoId}/associate", method = RequestMethod.POST)
  public String changeVideoAssociates(HttpServletRequest req, @PathVariable long signId, @PathVariable long videoId, Principal principal)  {
    List<Long> associateVideosIds =
      transformAssociateVideosIdsToLong(req.getParameterMap().get("associateVideosIds"));

    services.video().changeVideoAssociates(videoId, associateVideosIds);

    log.info("Change video (id={}) associates, ids={}", videoId, associateVideosIds);

    return showSign(signId);
  }


  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/create", method = RequestMethod.POST)
  public String createSign(@ModelAttribute SignCreationView signCreationView, Principal principal) {
    User user = services.user().withUserName(principal.getName());
    Sign sign = services.sign().create(user.id, signCreationView.getSignName(), signCreationView.getVideoUrl(), "");

    log.info("createSign: username = {} / sign name = {} / video url = {}", user.username, signCreationView.getSignName(), signCreationView.getVideoUrl());

    return showSign(sign.id);
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/sign/search")
  public String searchSign(@ModelAttribute SignCreationView signCreationView) {
    return "redirect:/sec/signs-suggest?name="+signCreationView.getSignName();
  }

  @Secured("ROLE_USER")
  @RequestMapping(value = "/sec/signs-suggest")
  public String showSignsSuggest(Model model,@RequestParam("name") String name, Principal principal) {
    model.addAttribute("backUrl", "/sec/suggest");
    model.addAttribute("title", messageByLocaleService.getMessage("sign.new"));
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));
    User user = services.user().withUserName(principal.getName());
    Signs signs = services.sign().search(name);


    model.addAttribute("signName", name);
    model.addAttribute("isSignAlreadyExist", false);
    List<Sign> signsWithSameName = new ArrayList<>();
    for (Sign sign: signs.list()) {
      if (sign.name.equals(name) ) {
        model.addAttribute("isSignAlreadyExist", true);
        model.addAttribute("signMatche", sign);
      } else {
        signsWithSameName.add(sign);
      }
    }

    model.addAttribute("signsWithSameName", signsWithSameName);

    SignCreationView signCreationView = new SignCreationView();
    signCreationView.setSignName(name);
    model.addAttribute("signCreationView", signCreationView);

    return "signs-suggest";
  }

  private String signUrl(long signId) {
    return "/sign/" + signId;
  }

  private String videoUrl(long signId, long videoId) {
    return "/sign/" + signId + "/" + videoId;
  }

  private String showSign(long signId) {
    return "redirect:/sign/" + signId;
  }

  private String showVideo(long signId, long videoId) {
    return "redirect:/sign/" + signId + "/" + videoId;
  }

  private void fillModelWithContext(Model model, String messageEntry, Principal principal, boolean showAddFavorite, String backUrl) {
    model.addAttribute("title", messageByLocaleService.getMessage(messageEntry));
    model.addAttribute("backUrl", backUrl);
    AuthentModel.addAuthenticatedModel(model, AuthentModel.isAuthenticated(principal));
    model.addAttribute("showAddFavorite", showAddFavorite && AuthentModel.isAuthenticated(principal));
  }

  private void fillModelWithSigns(Model model, Principal principal) {
    final User user = AuthentModel.isAuthenticated(principal) ? services.user().withUserName(principal.getName()) : null;

    List<Object[]> querySigns = services.sign().SignsForSignsView();
    List<SignViewData> signViewsData = querySigns.stream()
      .map(objectArray -> new SignViewData(objectArray))
      .collect(Collectors.toList());

    List<Long> signWithCommentList = Arrays.asList(services.sign().mostCommented());

    List<Long> signWithView = Arrays.asList(services.sign().mostViewed());

    List<Long> signWithPositiveRate = Arrays.asList(services.sign().mostRating());

    List<SignView2> signViews;
    List<Long> signInFavorite = null;
    if (user != null) {
      signInFavorite = Arrays.asList(services.sign().SignsForAllFavoriteByUser(user.id));
      List<Long> finalSignInFavorite = signInFavorite;
      signViews = signViewsData.stream()
        .map(signViewData -> buildSignView(signViewData, signWithCommentList, signWithView, signWithPositiveRate, finalSignInFavorite, user))
        .collect(Collectors.toList());
    } else {
      signViews = signViewsData.stream()
        .map(signViewData -> new SignView2(
          signViewData,
          signWithCommentList.contains(signViewData.id),
          SignView2.createdAfterLastDeconnection(signViewData.createDate, user == null ? null : user.lastDeconnectionDate),
          signWithView.contains(signViewData.id),
          signWithPositiveRate.contains(signViewData.id),
          false)
        )
        .collect(Collectors.toList());
    }

    SignsViewSort2 signsViewSort2 = new SignsViewSort2();
    signViews = signsViewSort2.sort(signViews, false);

    fillModelWithFavorites(model, user);
    model.addAttribute("signsView", signViews);
    model.addAttribute("signCreationView", new SignCreationView());
  }

  private SignView2 buildSignView(SignViewData signViewData, List<Long> signWithCommentList, List<Long> signWithView, List<Long> signWithPositiveRate, List<Long> signInFavorite, User user) {
    return new SignView2(
      signViewData,
      signWithCommentList.contains(signViewData.id),
      SignView2.createdAfterLastDeconnection(signViewData.createDate, user == null ? null : user.lastDeconnectionDate),
      signWithView.contains(signViewData.id),
      signWithPositiveRate.contains(signViewData.id),
      signInFavorite.contains(signViewData.id));
  }

  private VideoView2 buildVideoView(VideoViewData videoViewData, List<Long> videoWithCommentList, List<Long> videoWithPositiveRate, List<Long> signBelowToFavorite, User user) {
    return new VideoView2(
      videoViewData,
      videoWithCommentList.contains(videoViewData.videoId),
      VideoView2.createdAfterLastDeconnection(videoViewData.createDate, user == null ? null : user.lastDeconnectionDate),
      videoViewData.nbView > 0,
      videoWithPositiveRate.contains(videoViewData.videoId),
      signBelowToFavorite.contains(videoViewData.signId));
  }


  private List<Long> transformAssociateVideosIdsToLong(String[] associateVideosIds) {
    return associateVideosIds == null ? new ArrayList<>() :
      Arrays.asList(associateVideosIds).stream()
        .map(Long::parseLong)
        .collect(Collectors.toList());
  }

  private void fillModelWithFavorites(Model model, User user) {
    if (user != null) {
      List<FavoriteModalView> myFavorites = FavoriteModalView.from(services.favorite().favoritesforUser(user.id));
      model.addAttribute("myFavorites", myFavorites);
    }
  }

}

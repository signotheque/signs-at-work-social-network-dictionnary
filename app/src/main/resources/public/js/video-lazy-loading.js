/*
 * #%L
 * Telsigne
 * %%
 * Copyright (C) 2016 Orange
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

(function videoViewsLazyLoading($) {
  var HIDDEN_CLASS = 'video-view-hidden';
  var NB_VIDEO_VIEWS_INC = 16;
  var REVEAL_DURATION_MS = 1000;

  var videosContainer = document.getElementById("videos-container");
  /** Live node list (updated while we iterate over it...) */
  var videoViewsHidden = videosContainer.getElementsByClassName(HIDDEN_CLASS);

  var videosCount = videosContainer.children.length;

  var displayedVideosCount = 0;
  function showVideoView(videoView) {
    videoView.style.opacity = "0";
    videoView.className = videoView.className.replace(HIDDEN_CLASS, '');
    var img = videoView.getElementsByTagName('img')[0];
    var thumbnailUrl = img.dataset.src;
    img.src = thumbnailUrl;
    $(videoView).fadeTo(REVEAL_DURATION_MS, 1);
  }

  function showNextVideoViews() {
    var viewsToReveal = [];
    for (var i = 0; i < NB_VIDEO_VIEWS_INC && i < videoViewsHidden.length; i++) {
      viewsToReveal.push(videoViewsHidden[i]);
    }
    for (var i = 0; i < viewsToReveal.length; i++) {
      showVideoView(viewsToReveal[i]);
      displayedVideosCount++;
    }
    console.log("total: " + videosCount + ", hidden: " + videoViewsHidden.length + ", displayedVideosCount: " + displayedVideosCount);
  }

  function onScroll(event) {
    var noMoreHiddenVideos = videoViewsHidden.length === 0;
    var closeToBottom = $(window).scrollTop() + $(window).height() > $(document).height() - $(window).height()/5;
    if(!noMoreHiddenVideos && closeToBottom) {
      showNextVideoViews();
    }
  }

  function scrollBarVisible() {
    return $(document).height() > $(window).height();
  }

  function initWithFirstVideos() {
    do {
      showNextVideoViews();
    } while(!scrollBarVisible() && displayedVideosCount != videosCount);
  }

  function main() {
    // show first signs at load
    initWithFirstVideos();

    // then wait to reach the page bottom to load next views
    document.addEventListener('scroll', onScroll);
  }

  main();

})($);
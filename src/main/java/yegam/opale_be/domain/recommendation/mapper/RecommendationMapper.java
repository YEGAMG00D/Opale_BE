package yegam.opale_be.domain.recommendation.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.recommendation.dto.response.*;

import java.util.Arrays;
import java.util.List;

@Component
public class RecommendationMapper {

  /* 공연 추천 DTO 변환 */
  public RecommendedPerformanceDto toPerformance(Performance p, Double score) {

    return RecommendedPerformanceDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genrenm(p.getGenrenm())
        .poster(p.getPoster())
        .placeName(p.getPlaceName())
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .rating(p.getRating() != null ? p.getRating() : 0.0)
        .keywords(splitKeywords(p.getAiKeywords()))
        .aiSummary(p.getAiSummary())
        .score(score != null ? score : 0.0)
        .viewCount(p.getViewCount() != null ? p.getViewCount() : 0L)
        .build();
  }

  /* 공연장 추천 DTO 변환 */
  public RecommendedPlaceDto toPlace(Place p) {
    return RecommendedPlaceDto.builder()
        .placeId(p.getPlaceId())
        .name(p.getName())
        .address(p.getAddress())
        .telno(p.getTelno())
        .rating(p.getRating() != null ? p.getRating() : 0.0)
        .stageCount(p.getStageCount())
        .viewCount(p.getViewCount() != null ? p.getViewCount() : 0L)
        .build();
  }

  /* 채팅방 추천 DTO 변환 */
  public RecommendedChatRoomDto toChatRoom(ChatRoom r) {
    return RecommendedChatRoomDto.builder()
        .roomId(r.getRoomId())
        .title(r.getTitle())
        .thumbnailUrl(r.getThumbnailUrl())
        .lastMessage(r.getLastMessage())
        .lastMessageTime(r.getLastMessageTime())
        .visitCount(r.getVisitCount() != null ? r.getVisitCount() : 0L)
        .isActive(r.getIsActive())
        .performanceId(r.getPerformance() != null ? r.getPerformance().getPerformanceId() : null)
        .performanceTitle(r.getPerformance() != null ? r.getPerformance().getTitle() : null)
        .build();
  }

  private List<String> splitKeywords(String keywords) {
    if (keywords == null || keywords.isBlank()) return List.of();
    return Arrays.stream(keywords.split(","))
        .map(String::trim)
        .filter(k -> !k.isBlank())
        .toList();
  }
}

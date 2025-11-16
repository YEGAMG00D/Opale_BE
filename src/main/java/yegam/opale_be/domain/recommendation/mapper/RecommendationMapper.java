package yegam.opale_be.domain.recommendation.mapper;

import org.springframework.stereotype.Component;
import yegam.opale_be.domain.culture.performance.entity.Performance;
import yegam.opale_be.domain.place.entity.Place;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.recommendation.dto.response.*;

@Component
public class RecommendationMapper {

  /* 공연 */
  public RecommendedPerformanceDto toPerformance(Performance p, Double score) {
    return RecommendedPerformanceDto.builder()
        .performanceId(p.getPerformanceId())
        .title(p.getTitle())
        .genre(p.getGenrenm())
        .poster(p.getPoster())
        .startDate(p.getStartDate() != null ? p.getStartDate().toLocalDate() : null)
        .endDate(p.getEndDate() != null ? p.getEndDate().toLocalDate() : null)
        .rating(p.getRating())
        .viewCount(p.getViewCount())
        .score(score)
        .build();
  }

  /* 공연장 */
  public RecommendedPlaceDto toPlace(Place p) {
    return RecommendedPlaceDto.builder()
        .placeId(p.getPlaceId())
        .name(p.getName())
        .address(p.getAddress())
        .rating(p.getRating())
        .viewCount(p.getViewCount())
        .build();
  }

  /* 채팅방 */
  public RecommendedChatRoomDto toChatRoom(ChatRoom r) {
    return RecommendedChatRoomDto.builder()
        .roomId(r.getRoomId())
        .title(r.getTitle())
        .visitCount(r.getVisitCount())
        .lastMessage(r.getLastMessage())
        .lastMessageTime(r.getLastMessageTime())
        .build();
  }
}

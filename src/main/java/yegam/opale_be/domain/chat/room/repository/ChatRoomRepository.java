package yegam.opale_be.domain.chat.room.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.entity.RoomType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  /** ⭐ 채팅방 조회수 증가 */
  @Modifying
  @Transactional
  @Query("UPDATE ChatRoom r SET r.visitCount = r.visitCount + 1 WHERE r.roomId = :roomId")
  void incrementVisitCount(Long roomId);

  List<ChatRoom> findByRoomType(RoomType roomType);

  List<ChatRoom> findByRoomTypeAndPerformance_PerformanceId(RoomType roomType, String performanceId);

  /** ⭐ 인기 채팅방: 방문자수 + 최근 메시지 시간 */
  @Query("""
      SELECT r FROM ChatRoom r
      ORDER BY r.visitCount DESC, r.lastMessageTime DESC NULLS LAST
      """)
  List<ChatRoom> findPopularChatRooms(Pageable pageable);

  /** ⭐ roomType + performanceId + keyword 검색 */
  @Query("""
    SELECT r FROM ChatRoom r
    WHERE
      (:roomType IS NULL OR r.roomType = :roomType)
      AND (:performanceId IS NULL OR r.performance.performanceId = :performanceId)
      AND (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
  List<ChatRoom> searchRooms(
      @Param("roomType") RoomType roomType,
      @Param("performanceId") String performanceId,
      @Param("keyword") String keyword
  );


  /** ⭐ 공연별 PUBLIC 채팅방 1개 찾기 */
  Optional<ChatRoom> findFirstByRoomTypeAndPerformance_PerformanceId(
      RoomType roomType,
      String performanceId
  );





}

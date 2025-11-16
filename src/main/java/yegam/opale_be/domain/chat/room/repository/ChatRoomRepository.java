package yegam.opale_be.domain.chat.room.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.entity.RoomType;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  /** ⭐ 채팅방 조회수 증가 */
  @Modifying
  @Transactional
  @Query("UPDATE ChatRoom r SET r.visitCount = r.visitCount + 1 WHERE r.roomId = :roomId")
  void incrementVisitCount(Long roomId);

  List<ChatRoom> findByRoomType(RoomType roomType);

  List<ChatRoom> findByRoomTypeAndPerformance_PerformanceId(RoomType roomType, String performanceId);
}

package yegam.opale_be.domain.chat.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.chat.room.entity.ChatRoom;
import yegam.opale_be.domain.chat.room.entity.RoomType;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  List<ChatRoom> findByRoomType(RoomType roomType);

  List<ChatRoom> findByRoomTypeAndPerformance_PerformanceId(RoomType roomType, String performanceId);



}

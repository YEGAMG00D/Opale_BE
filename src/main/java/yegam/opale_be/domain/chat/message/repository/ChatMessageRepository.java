package yegam.opale_be.domain.chat.message.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yegam.opale_be.domain.chat.message.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  /** 채팅방별 메시지 페이징 조회 */
  Page<ChatMessage> findByChatRoom_RoomId(Long roomId, Pageable pageable);

  /** 사용자별 메시지 페이징 조회 */
  Page<ChatMessage> findByUser_UserId(Long userId, Pageable pageable);
}

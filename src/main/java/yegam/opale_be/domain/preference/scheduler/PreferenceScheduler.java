package yegam.opale_be.domain.preference.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yegam.opale_be.domain.preference.service.PreferenceBatchService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreferenceScheduler {

  private final PreferenceBatchService batchService;

  /** 🔥 매일 새벽 4시에 전체 벡터 업데이트 */
  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void updateDaily() {
    log.info("⏰ 스케줄러 실행 — 전체 사용자 벡터 업데이트 시작");
    batchService.updateAllUserVectors();
  }
}

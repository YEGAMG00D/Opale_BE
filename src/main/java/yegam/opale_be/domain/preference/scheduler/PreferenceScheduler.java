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

  /** ⏰ 매일 새벽 4시(Asia/Seoul) 전체 유저 선호 벡터 업데이트 */
  // @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")

  /** ⏰ 매 5분마다 전체 유저 선호 벡터 자동 업데이트 */
  @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
  public void updateUserVectorsDaily() {
    log.info("⏰ [Scheduler] 사용자 선호 벡터 배치 시작");
    batchService.updateAllUserVectors();
    log.info("⏰ [Scheduler] 사용자 선호 벡터 배치 종료");
  }
}

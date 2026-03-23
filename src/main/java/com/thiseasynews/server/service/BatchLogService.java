package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.BatchLogResponse;
import com.thiseasynews.server.entity.BatchLog;
import com.thiseasynews.server.repository.BatchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchLogService {

    private final BatchLogRepository batchLogRepository;

    // ── 배치 시작 로그 저장 ───────────────────────────
    /**
     * 독립 트랜잭션 - 배치 본 트랜잭션이 롤백되어도 로그는 보존
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BatchLog startJob(String jobName) {
        BatchLog log = BatchLog.startJob(jobName);
        return batchLogRepository.save(log);
    }

    // ── 배치 완료 로그 업데이트 ───────────────────────
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeJob(Long batchLogId, int total, int success, int fail) {
        batchLogRepository.findById(batchLogId).ifPresent(bl -> {
            bl.complete(total, success, fail);
            log.info("[Batch] 완료 jobId={} total={} success={} fail={}",
                    batchLogId, total, success, fail);
        });
    }

    // ── 배치 실패 로그 업데이트 ───────────────────────
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failJob(Long batchLogId, String errorMessage) {
        batchLogRepository.findById(batchLogId).ifPresent(bl -> bl.fail(errorMessage));
        log.error("[Batch] 실패 jobId={} error={}", batchLogId, errorMessage);
    }

    // ── 최근 배치 이력 조회 ───────────────────────────
    @Transactional(readOnly = true)
    public List<BatchLogResponse> getRecentLogs(int limit) {
        return batchLogRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .getContent()
                .stream()
                .map(BatchLogResponse::from)
                .collect(Collectors.toList());
    }

    // ── Job명별 이력 조회 ─────────────────────────────
    @Transactional(readOnly = true)
    public List<BatchLogResponse> getLogsByJobName(String jobName) {
        return batchLogRepository
                .findByJobNameOrderByCreatedAtDesc(jobName)
                .stream()
                .map(BatchLogResponse::from)
                .collect(Collectors.toList());
    }

    // ── 배치 실행 파사드 (편의 메서드) ────────────────
    /**
     * 배치 Job 전체 흐름 (시작 → 실행 → 완료/실패 로그) 처리
     *
     * <pre>
     * batchLogService.run("RSS_CRAWLING", () -> rssCrawlService.crawl());
     * </pre>
     */
    public void run(String jobName, Runnable task) {
        BatchLog bl = startJob(jobName);
        try {
            task.run();
            completeJob(bl.getId(), 0, 0, 0);
        } catch (Exception e) {
            failJob(bl.getId(), e.getMessage());
            throw e;
        }
    }

    /**
     * 처리 결과 카운트 포함 실행
     *
     * <pre>
     * batchLogService.runWithResult("GPT_SUMMARY", () -> {
     *     int[] c = summaryService.summarizeAll();
     *     return new BatchResult(c[0], c[1], c[2]);
     * });
     * </pre>
     */
    public BatchResult runWithResult(String jobName, BatchTask task) {
        BatchLog bl = startJob(jobName);
        try {
            BatchResult result = task.execute();
            completeJob(bl.getId(), result.total(), result.success(), result.fail());
            return result;
        } catch (Exception e) {
            failJob(bl.getId(), e.getMessage());
            throw e;
        }
    }

    @FunctionalInterface
    public interface BatchTask {
        BatchResult execute();
    }

    public record BatchResult(int total, int success, int fail) {}
}

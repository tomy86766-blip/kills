package com.example.spring_server.service;

import com.example.spring_server.dto.CreateExperimentRequest;
import com.example.spring_server.dto.TrainRequest;
import com.example.spring_server.dto.TrainResponse;
import com.example.spring_server.entity.ExperimentLog;
import com.example.spring_server.repository.ExperimentLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExperimentService {

    private final WebClient mlWebClient;
    private final ExperimentLogRepository repository;

    // 생성자 주입 (3단계 빈 + 1단계 repository 를 Spring이 넣어줌)
    public ExperimentService(WebClient mlWebClient, ExperimentLogRepository repository) {
        this.mlWebClient = mlWebClient;
        this.repository = repository;
    }

    public ExperimentLog create(CreateExperimentRequest req) {
        // ① ML 서버에 학습 요청 만들기
        TrainRequest trainRequest =
                new TrainRequest(req.getEpochs(), req.getBatchSize(), req.getLearningRate());

        // ② /train 호출하고 결과 올 때까지 기다림(.block)
        TrainResponse result = mlWebClient.post()
                .uri("/train")
                .bodyValue(trainRequest)
                .retrieve()
                .bodyToMono(TrainResponse.class)
                .block();

        // ③ 중첩 응답 → 평평한 엔티티로 펼치기 + 메모/태그 합치기
        TrainResponse.Hyperparameters hp = result.getHyperparameters();
        ExperimentLog log = ExperimentLog.builder()
                .algorithm(result.getAlgorithm())
                .optimizer(hp.getOptimizer())
                .epochs(hp.getEpochs())
                .batchSize(hp.getBatchSize())
                .learningRate(hp.getLearningRate())
                .accuracy(result.getAccuracy())
                .loss(result.getLoss())
                .memo(req.getMemo())
                .tag(req.getTag())
                .deleted(false)
                .build();   // createdAt 은 안 넣음 → @CreationTimestamp 자동

        // ④ DB 저장 (저장된 객체엔 id, createdAt 채워져서 돌아옴)
        return repository.save(log);
    }

    // ─────────────────────────────────────────────
    //  찬영 : Delete (soft delete)
    // ─────────────────────────────────────────────

    @Transactional  // 메서드가 끝날 때 변경 내용을 DB에 자동 커밋
    public void softDelete(Long id) {
        // deleted = false 인 레코드만 가져옴 → 이미 삭제된 것은 예외 발생
        ExperimentLog log = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 이미 삭제된 실험입니다. id=" + id));

        // DB를 UPDATE 하지 않고, 객체 필드만 true 로 바꿈
        // → @Transactional 덕분에 메서드 종료 시 JPA가 변경을 감지(Dirty Checking)해서 자동 저장
        log.setDeleted(true);
    }
}

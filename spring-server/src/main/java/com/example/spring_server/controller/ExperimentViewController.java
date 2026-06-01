package com.example.spring_server.controller;

import com.example.spring_server.dto.CreateExperimentRequest;
import com.example.spring_server.service.ExperimentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller   // ← RestController 아님! 화면 이름을 반환
public class ExperimentViewController {

    private final ExperimentService service;

    public ExperimentViewController(ExperimentService service) {
        this.service = service;
    }

    // 입력 폼 보여주기 (기본값 미리 채워서 → 사용자가 수정)
    @GetMapping("/experiments/new")
    public String newForm(Model model) {
        CreateExperimentRequest form = new CreateExperimentRequest();
        form.setEpochs(50);
        form.setBatchSize(16);
        form.setLearningRate(0.01);
        model.addAttribute("form", form);
        return "experimentForm";   // → templates/experiment-form.mustache
    }

    // 폼 제출 → 학습 + 저장 → 목록으로 리다이렉트
    @PostMapping("/experiments")
    public String submit(@ModelAttribute CreateExperimentRequest form) {
        service.create(form);
        return "redirect:/experiments";   // 저장 후 목록으로 (윤서 Read 페이지)
    }

    // ─────────────────────────────────────────────
    //  찬영 : Delete (soft delete)
    // ─────────────────────────────────────────────

    // HTML <form> 은 GET/POST 만 지원하므로 POST 로 받음
    // URL 예시: POST /experiments/3/delete
    @PostMapping("/experiments/{id}/delete")
    public String delete(@PathVariable Long id) {
        // {id} 자리 숫자를 Long 으로 받아서 서비스에 넘김
        service.softDelete(id);

        // 삭제 후 목록 페이지로 돌아감 (redirect: 는 새 GET 요청을 보내는 것)
        return "redirect:/experiments";
    }
}

package com.poojamart.controller;

import com.poojamart.dto.AiChatRequest;
import com.poojamart.dto.AiChatResponse;
import com.poojamart.service.AiToyAdvisorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(originPatterns = "*")
public class AiToyAdvisorController {

    private final AiToyAdvisorService aiToyAdvisorService;

    public AiToyAdvisorController(AiToyAdvisorService aiToyAdvisorService) {
        this.aiToyAdvisorService = aiToyAdvisorService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        AiChatResponse response = aiToyAdvisorService.processChat(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommend")
    public ResponseEntity<AiChatResponse> recommend(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String ageGroup,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category) {
        AiChatRequest req = new AiChatRequest();
        req.setMessage(query != null ? query : "");
        req.setAgeGroup(ageGroup);
        req.setMaxPrice(maxPrice);
        req.setCategory(category);
        return ResponseEntity.ok(aiToyAdvisorService.processChat(req));
    }

    @GetMapping("/prompts")
    public ResponseEntity<List<String>> getSuggestedPrompts() {
        return ResponseEntity.ok(Arrays.asList(
            "👶 Best toys for 0-2 years babies",
            "🧸 Cuddly soft teddy bears under ₹500",
            "🏎️ Fast Remote Control Cars & Monster Trucks",
            "🔬 STEM Educational Science Toys for 6-8 Yrs",
            "🧩 Mind-Sharpening Puzzle Games",
            "🎁 Best Birthday Gifts under ₹1,000"
        ));
    }
}

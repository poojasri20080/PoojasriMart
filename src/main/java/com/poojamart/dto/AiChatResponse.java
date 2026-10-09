package com.poojamart.dto;

import com.poojamart.model.Product;
import java.util.List;

public class AiChatResponse {
    private String reply;
    private List<Product> recommendations;
    private List<String> suggestedQuestions;

    public AiChatResponse() {
    }

    public AiChatResponse(String reply, List<Product> recommendations, List<String> suggestedQuestions) {
        this.reply = reply;
        this.recommendations = recommendations;
        this.suggestedQuestions = suggestedQuestions;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<Product> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Product> recommendations) {
        this.recommendations = recommendations;
    }

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }
}

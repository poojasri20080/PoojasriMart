package com.poojamart.service;

import com.poojamart.dto.AiChatRequest;
import com.poojamart.dto.AiChatResponse;
import com.poojamart.model.Category;
import com.poojamart.model.Product;
import com.poojamart.repository.CategoryRepository;
import com.poojamart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiToyAdvisorService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AiToyAdvisorService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public AiChatResponse processChat(AiChatRequest request) {
        String msg = (request.getMessage() != null) ? request.getMessage().trim() : "";
        String lower = msg.toLowerCase();

        // 1. Check for basic greetings
        if (lower.matches("^(hi|hello|hey|vanakkam|namaste|good morning|good afternoon|good evening|who are you).*")) {
            List<Product> topFeatured = productRepository.findByFeaturedTrue().stream().limit(4).collect(Collectors.toList());
            if (topFeatured.isEmpty()) {
                topFeatured = productRepository.findAll().stream().limit(4).collect(Collectors.toList());
            }
            return new AiChatResponse(
                "👋 Hello! Welcome to **PoojaMart Toys AI Advisor**! 🧸\n\n" +
                "I can help you find the absolute best toys tailored to your child's age, interests, and your budget! " +
                "Tell me who you are shopping for (e.g., *'Best gift for 5 year old boy under ₹1,000'* or *'Safe baby rattle and teethers'*). Here are some of our trending favorites right now:",
                topFeatured,
                Arrays.asList(
                    "🧸 Soft Teddy Bears under ₹500",
                    "🏎️ Fast Remote Control Cars",
                    "🧩 STEM & Educational Toys for 6-8 Yrs",
                    "👶 Safe Toys for 0-2 Years Baby"
                )
            );
        }

        // 2. Check for order / shipping / returns inquiry
        if (lower.contains("delivery") || lower.contains("shipping") || lower.contains("cod") ||
            lower.contains("cash on delivery") || lower.contains("return") || lower.contains("how to order") ||
            lower.contains("payment") || lower.contains("order status")) {
            return new AiChatResponse(
                "📦 **Ordering & Shipping at PoojaMart Toys:**\n\n" +
                "• **Free Shipping:** On all toy orders above ₹499 across India!\n" +
                "• **Fast Delivery:** Usually delivered within 2-4 business days.\n" +
                "• **Payment Methods:** Cash on Delivery (COD), UPI (GPay, PhonePe, Paytm), and Cards.\n" +
                "• **7-Day Hassle-Free Returns:** If there's any defect or damage, we offer instant replacement or refund.\n" +
                "• **Live Tracking:** You can track every order from 'My Orders' in real-time!",
                Collections.emptyList(),
                Arrays.asList(
                    "Show best seller toys",
                    "Gifts under ₹1000",
                    "How to track my order?"
                )
            );
        }

        // 3. Extract budget constraints from message
        Double maxPrice = request.getMaxPrice();
        if (maxPrice == null) {
            Pattern pricePattern = Pattern.compile("(under|below|less than|within|budget of|<|kula)\\s*(?:rs\\.?|inr|₹)?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher m = pricePattern.matcher(lower);
            if (m.find()) {
                try {
                    maxPrice = Double.parseDouble(m.group(2));
                } catch (Exception ignored) {}
            }
        }

        // 4. Extract Category or Theme Keywords
        String categoryKeyword = null;
        if (lower.contains("rc") || lower.contains("remote") || lower.contains("monster truck") || lower.contains("drift")) {
            categoryKeyword = "Remote Control Cars";
        } else if (lower.contains("car") || lower.contains("racing") || lower.contains("die-cast") || lower.contains("hot wheels") || lower.contains("supercar")) {
            categoryKeyword = "Racing Cars";
        } else if (lower.contains("teddy") || lower.contains("bear") || lower.contains("soft toy") || lower.contains("plush")) {
            categoryKeyword = "Teddy Bears";
        } else if (lower.contains("block") || lower.contains("lego") || lower.contains("brick") || lower.contains("building") || lower.contains("construct")) {
            categoryKeyword = "Building Blocks";
        } else if (lower.contains("puzzle") || lower.contains("jigsaw") || lower.contains("cube") || lower.contains("brain") || lower.contains("board game")) {
            categoryKeyword = "Puzzle Games";
        } else if (lower.contains("science") || lower.contains("education") || lower.contains("stem") || lower.contains("microscope") || lower.contains("math") || lower.contains("robot") || lower.contains("learn")) {
            categoryKeyword = "Educational Toys";
        } else if (lower.contains("action") || lower.contains("superhero") || lower.contains("hero") || lower.contains("figure") || lower.contains("avenger") || lower.contains("spiderman") || lower.contains("batman")) {
            categoryKeyword = "Action Figures";
        } else if (lower.contains("train") || lower.contains("railway") || lower.contains("steam engine") || lower.contains("locomotive")) {
            categoryKeyword = "Toy Trains";
        } else if (lower.contains("baby") || lower.contains("infant") || lower.contains("toddler") || lower.contains("teether") || lower.contains("rattle") || lower.contains("sensory") || lower.contains("kulandhai") || lower.contains("paapa")) {
            categoryKeyword = "Baby Toys";
        } else if (lower.contains("outdoor") || lower.contains("scooter") || lower.contains("tent") || lower.contains("archery") || lower.contains("sport") || lower.contains("play tent")) {
            categoryKeyword = "Outdoor Toys";
        }

        // 5. Match candidate products
        List<Product> allProducts = productRepository.findAll();
        List<Product> matched = new ArrayList<>();

        final String targetCat = categoryKeyword;
        final Double budgetLimit = maxPrice;

        for (Product p : allProducts) {
            boolean fitsBudget = (budgetLimit == null) || (p.getPrice() <= budgetLimit);
            if (!fitsBudget) continue;

            if (targetCat != null) {
                if (p.getCategory() != null && p.getCategory().getName().equalsIgnoreCase(targetCat)) {
                    matched.add(p);
                }
            } else {
                // Keyword search in name or description
                String pText = (p.getName() + " " + p.getDescription() + " " + p.getAgeGroup()).toLowerCase();
                for (String word : lower.split("\\s+")) {
                    if (word.length() > 3 && pText.contains(word)) {
                        matched.add(p);
                        break;
                    }
                }
            }
        }

        // If no strict match found, fallback to top bestsellers or featured within budget
        if (matched.isEmpty()) {
            matched = allProducts.stream()
                .filter(p -> budgetLimit == null || p.getPrice() <= budgetLimit)
                .sorted(Comparator.comparing(Product::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .collect(Collectors.toList());
        } else {
            // Limit to top 5 best matches
            matched = matched.stream().limit(5).collect(Collectors.toList());
        }

        // 6. Build generative response explanation
        StringBuilder reply = new StringBuilder();
        if (targetCat != null) {
            reply.append("🎯 Great choice! For **").append(targetCat).append("**");
            if (budgetLimit != null) {
                reply.append(" under ₹").append(String.format("%,.0f", budgetLimit));
            }
            reply.append(", here are our highest-rated and safest toys:\n\n");
        } else if (budgetLimit != null) {
            reply.append("💰 Here are the best-selling PoojaMart toys under ₹").append(String.format("%,.0f", budgetLimit)).append(" that kids love:\n\n");
        } else {
            reply.append("✨ Based on your request, I handpicked these top-rated toys from PoojaMart! Each toy is crafted with 100% non-toxic child-safe materials and tested for hours of imaginative play:\n\n");
        }

        for (Product p : matched) {
            reply.append("• **").append(p.getName()).append("** (₹").append(String.format("%,.0f", p.getPrice())).append(")");
            if (p.getAgeGroup() != null && !p.getAgeGroup().isEmpty()) {
                reply.append(" — Ideal for ").append(p.getAgeGroup());
            }
            reply.append(" ★ ").append(p.getRating() != null ? p.getRating() : 4.8).append("\n");
        }

        reply.append("\n💡 *Tip: Click 'Add to Cart' or click any toy card below to see full specifications and place your order!*");

        List<String> nextQuestions = Arrays.asList(
            "Show toys under ₹500",
            "What are the best toys for 3-5 years?",
            "Show fast remote control cars",
            "Do you have educational STEM kits?"
        );

        return new AiChatResponse(reply.toString(), matched, nextQuestions);
    }
}

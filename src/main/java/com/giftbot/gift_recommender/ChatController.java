package com.giftbot.gift_recommender;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChatController {

    private final ChatService chatService;

    // We use simple DTOs (Data Transfer Objects) as Records for clean JSON.
    // This is what the frontend will send to us.
    public record ChatRequest(String message) {}
    // This is what we will send back to the frontend.
    public record ChatResponse(String response, boolean isFinal) {}

    // Hardcoded first question
    private static final String FIRST_QUESTION = "Q: What's the occasion for this gift?\n1. Birthday\n2. Anniversary\n3. Holiday (e.g., Christmas)\n4. Just because / Thank you";

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Serves the main index.html chat page.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/")
    public String chatPage() {
        return "index"; // This tells Thymeleaf to look for "index.html"
    }

    /**
     * API Endpoint: Starts a new chat session.
     * Creates the profile, adds the first question, and returns it.
     */
    @GetMapping("/api/start")
    @ResponseBody // Tells Spring to return JSON, not an HTML page
    public ChatResponse startChat(HttpServletRequest request) {
        // Create a new session for the user
        HttpSession session = request.getSession(true);

        // Create a new gift profile
        GiftProfile profile = new GiftProfile();
        profile.addBotQuestion(FIRST_QUESTION);

        // Store the profile in the session
        session.setAttribute("giftProfile", profile);

        // Return the first question
        return new ChatResponse(FIRST_QUESTION, false);
    }

    /**
     * API Endpoint: Handles a user's message.
     * It gets the profile from the session, processes the message, and returns the bot's reply.
     */
    @PostMapping("/api/chat")
    @ResponseBody
    public ChatResponse handleChat(@RequestBody ChatRequest chatRequest, HttpServletRequest request) {
        HttpSession session = request.getSession();
        GiftProfile profile = (GiftProfile) session.getAttribute("giftProfile");

        // Safety check if session expired or user skipped /api/start
        if (profile == null) {
            return startChat(request); // Just restart the flow
        }

        // 1. Add the user's response to the profile
        profile.addUserResponse(chatRequest.message());

        // 2. Check if we have enough info to give a recommendation
        if (profile.isMaxQuestionsReached()) {
            // Get the final recommendation
            String recommendation = chatService.getFinalRecommendation(profile);

            // Invalidate the session, the chat is over.
            session.invalidate();

            return new ChatResponse(recommendation, true);
        } else {
            // 3. Ask the next question
            String nextQuestion = chatService.getNextQuestion(profile);
            profile.addBotQuestion(nextQuestion);

            // Save the updated profile back into the session
            session.setAttribute("giftProfile", profile);

            return new ChatResponse(nextQuestion, false);
        }
    }
}
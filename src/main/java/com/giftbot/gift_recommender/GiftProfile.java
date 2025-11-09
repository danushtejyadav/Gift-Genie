package com.giftbot.gift_recommender;

import java.io.Serializable; // Import this
import java.util.ArrayList;
import java.util.List;

/**
 * A mutable object to hold the conversation state.
 * Implements Serializable to be stored in HttpSession.
 */
public class GiftProfile implements Serializable { // Add "implements Serializable"

    // This line is for serialization, good practice
    private static final long serialVersionUID = 1L;

    private final List<String> conversationHistory;
    private int questionCount;
    private static final int MAX_QUESTIONS = 7;

    public GiftProfile() {
        this.conversationHistory = new ArrayList<>();
        this.questionCount = 0;
    }

    public void addUserResponse(String response) {
        this.conversationHistory.add("user: " + response);
    }

    public void addBotQuestion(String question) {
        this.conversationHistory.add("bot: " + question);
        this.questionCount++;
    }

    public String getConversationHistory() {
        return String.join("\n", this.conversationHistory);
    }

    public int getQuestionCount() {
        return this.questionCount;
    }

    public int getMaxQuestions() {
        return MAX_QUESTIONS;
    }

    public boolean isMaxQuestionsReached() {
        // We check >= because the *last* question is the 7th.
        // The check happens *after* the 7th answer is received.
        return this.questionCount >= MAX_QUESTIONS;
    }
}
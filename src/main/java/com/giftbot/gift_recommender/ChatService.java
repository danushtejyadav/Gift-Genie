package com.giftbot.gift_recommender;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;

    /**
     * PROMPT 1: FOCUSED ONLY ON ASKING THE *NEXT* QUESTION
     * We have removed all "recommendation" logic from this prompt.
     * The {conversation_history} is the most important variable.
     */
    private static final String QUESTION_PROMPT_TEMPLATE = """
            You are a world-class "Gift-Finder" assistant.
            Your ONLY goal is to ask the *next best question* to build a gift profile.
            You are in the middle of a conversation. The history is below.

            ---
            [CRISP FRAMEWORK]
            - **Context:** The user is answering your questions. The full {conversation_history} is provided.
            - **Role:** You are an expert gift-giving psychologist.
            - **Instructions (Your Logic):**
              1.  **Analyze the {conversation_history} THOROUGHLY.** Understand what you *already know*.
              2.  **DO NOT ask about a topic you have already covered.** (e.g., If the history already contains 'relationship' or 'personality', DO NOT ask about it again).
              3.  Your question MUST target a key *unknown* domain. The key domains are:
                  [Occasion, Relationship, Interests, Hobbies, Personality, Needs, Role/Profession, Budget, Dislikes, Age Group]
              4.  **CRITICAL:** Prioritize asking questions with multiple-choice options.
              5.  **FORMAT:** You MUST respond in this *exact* format:
                  Q: [Your clever, non-repetitive question here]
                  1. [Option 1]
                  2. [Option 2]
                  3. [Option 3]
                  4. [Other (please specify)]
            - **Personality:** Warm, helpful, and insightful.
            ---

            Here is the conversation so far:
            {conversation_history}
            
            Ask your next question.
            """;

    /**
     * PROMPT 2: FOCUSED ONLY ON GIVING THE *FINAL* RECOMMENDATION
     * This prompt assumes the interview is OVER.
     */
    private static final String RECOMMENDATION_PROMPT_TEMPLATE = """
            You are a world-class "Gift-Finder" assistant.
            The interview is complete. Your ONLY goal is to give the final gift ideas.
            
            ---
            [CRISP FRAMEWORK]
            - **Context:** The user has answered all your questions. The complete {conversation_history} is provided.
            - **Role:** You are an expert gift recommendation engine.
            - **Instructions (Your Logic):**
              1.  Analyze the *entire* {conversation_history}.
              2.  Provide 3 distinct, creative, and specific gift recommendations.
              3.  For each recommendation, you MUST explain *why* it's a good fit,
                  referencing specific details from the conversation history.
              4.  **FORMAT:** You MUST respond in this *exact* format:
                  R:
                  **1. [Gift Idea 1]**
                  *Why it's a good fit:* [Your reasoning based on the history]

                  **2. [Gift Idea 2]**
                  *Why it's a good fit:* [Your reasoning based on the history]

                  **3. [Gift Idea 3]**
                  *Why it's a good fit:* [Your reasoning based on the history]
            - **Personality:** Confident, creative, and helpful.
            ---
            
            Here is the full conversation history you must analyze:
            {conversation_history}
            
            Give your 3 recommendations now.
            """;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * NEW METHOD: Specifically for getting the next question.
     */
    public String getNextQuestion(GiftProfile profile) {
        PromptTemplate promptTemplate = new PromptTemplate(QUESTION_PROMPT_TEMPLATE);
        Map<String, Object> promptData = Map.of(
                "conversation_history", profile.getConversationHistory()
        );
        Prompt prompt = promptTemplate.create(promptData);

        return this.chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * NEW METHOD: Specifically for getting the final answer.
     */
    public String getFinalRecommendation(GiftProfile profile) {
        PromptTemplate promptTemplate = new PromptTemplate(RECOMMENDATION_PROMPT_TEMPLATE);
        Map<String, Object> promptData = Map.of(
                "conversation_history", profile.getConversationHistory()
        );
        Prompt prompt = promptTemplate.create(promptData);

        return this.chatClient.prompt(prompt)
                .call()
                .content();
    }
}
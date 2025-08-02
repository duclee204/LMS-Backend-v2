package org.example.lmsbackend.service;

import org.example.lmsbackend.model.*;
import org.example.lmsbackend.dto.*;
import org.example.lmsbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Service
public class ExamSubmissionService {

    @Autowired
    private UserQuizAttemptMapper userQuizAttemptMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuizzesMapper quizzesMapper;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private ModuleProgressService moduleProgressService;

    /**
     * Submit exam and return result immediately for multiple choice
     */
    @Transactional
    public QuizResultDTO submitExam(Integer userId, ExamSubmissionDTO submissionDTO) {
        try {
            // Create quiz attempt
            UserQuizAttempt attempt = new UserQuizAttempt();
            
            // Set user
            User user = userMapper.findByUserId(userId);
            attempt.setUser(user);
            
            // Set quiz
            Quizzes quiz = quizzesMapper.findById(submissionDTO.getQuizId());
            attempt.setQuiz(quiz);
            
            attempt.setAttemptedAt(Instant.now());
            attempt.setScore(0); // Will be updated after grading
            
            // Insert attempt
            userQuizAttemptMapper.insertAttempt(attempt);
            
            // Save user answers
            List<UserAnswer> userAnswers = new ArrayList<>();
            for (UserAnswerDTO answerDTO : submissionDTO.getAnswers()) {
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setAttempt(attempt);
                
                // Set question
                Questions question = questionsMapper.findById(answerDTO.getQuestionId());
                userAnswer.setQuestion(question);
                
                System.out.println("🔍 Processing Answer for Question ID: " + answerDTO.getQuestionId());
                System.out.println("🔍 Question Type: " + (question != null ? question.getType() : "null"));
                System.out.println("🔍 AnswerDTO - answerText: " + answerDTO.getAnswerText());
                System.out.println("🔍 AnswerDTO - linkAnswer: " + answerDTO.getLinkAnswer());
                System.out.println("🔍 AnswerDTO - answerId: " + answerDTO.getAnswerId());
                System.out.println("🔍 AnswerDTO - selectedIndex: " + answerDTO.getSelectedIndex());
                
                // Handle different answer types
                if (answerDTO.getAnswerId() != null) {
                    // Multiple choice - find the selected answer by ID
                    Answer selectedAnswer = answerMapper.findById(answerDTO.getAnswerId());
                    userAnswer.setAnswer(selectedAnswer);
                    userAnswer.setIsCorrect(selectedAnswer.getIsCorrect());
                } else if (answerDTO.getSelectedIndex() != null && question.getType() == Questions.Type.MULTIPLE_CHOICE) {
                    // Handle selected index for multiple choice (from frontend)
                    try {
                        List<Answer> questionAnswers = answerMapper.findByQuestionId(question.getQuestionId());
                        questionAnswers.sort((a, b) -> (a.getOrderNumber() != null ? a.getOrderNumber() : 0) - 
                                                     (b.getOrderNumber() != null ? b.getOrderNumber() : 0));
                        
                        // Get answer by selected index
                        if (answerDTO.getSelectedIndex() >= 0 && answerDTO.getSelectedIndex() < questionAnswers.size()) {
                            Answer selectedAnswer = questionAnswers.get(answerDTO.getSelectedIndex());
                            userAnswer.setAnswer(selectedAnswer);
                            userAnswer.setIsCorrect(selectedAnswer.getIsCorrect());
                            System.out.println("✅ Selected answer by index " + answerDTO.getSelectedIndex() + 
                                             ": " + selectedAnswer.getAnswerText() + 
                                             " (Correct: " + selectedAnswer.getIsCorrect() + ")");
                        } else {
                            // Invalid index
                            userAnswer.setAnswerText("Invalid selection");
                            userAnswer.setIsCorrect(false);
                            System.out.println("❌ Invalid selectedIndex: " + answerDTO.getSelectedIndex());
                        }
                    } catch (Exception e) {
                        // Fallback: store as incorrect
                        userAnswer.setAnswerText("Error processing selection");
                        userAnswer.setIsCorrect(false);
                        System.out.println("❌ Error processing selectedIndex: " + e.getMessage());
                    }
                } else if (answerDTO.getAnswerText() != null && !answerDTO.getAnswerText().trim().isEmpty()) {
                    // Handle text answer matching for multiple choice or essay answers
                    if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
                        // Try to find answer by text match
                        try {
                            List<Answer> questionAnswers = answerMapper.findByQuestionId(question.getQuestionId());
                            Answer matchedAnswer = questionAnswers.stream()
                                .filter(ans -> ans.getAnswerText().equals(answerDTO.getAnswerText()))
                                .findFirst()
                                .orElse(null);
                            
                            if (matchedAnswer != null) {
                                userAnswer.setAnswer(matchedAnswer);
                                userAnswer.setIsCorrect(matchedAnswer.getIsCorrect());
                            } else {
                                // Store as text if no match found
                                userAnswer.setAnswerText(answerDTO.getAnswerText());
                                userAnswer.setIsCorrect(false);
                            }
                        } catch (Exception e) {
                            // Fallback: store as text
                            userAnswer.setAnswerText(answerDTO.getAnswerText());
                            userAnswer.setIsCorrect(false);
                        }
                    } else {
                        // Essay or text answer
                        System.out.println("🔍 Processing Essay Question ID: " + answerDTO.getQuestionId());
                        System.out.println("🔍 AnswerText: " + answerDTO.getAnswerText());
                        System.out.println("🔍 LinkAnswer: " + answerDTO.getLinkAnswer());
                        
                        userAnswer.setAnswerText(answerDTO.getAnswerText());
                        userAnswer.setIsCorrect(null); // Will be graded manually
                        
                        // Handle essay link submission
                        if (answerDTO.getLinkAnswer() != null && !answerDTO.getLinkAnswer().trim().isEmpty()) {
                            System.out.println("✅ Setting linkAnswer: " + answerDTO.getLinkAnswer());
                            userAnswer.setLinkAnswer(answerDTO.getLinkAnswer());
                        }
                        
                        // Handle essay file submission
                        if (answerDTO.getFileName() != null && !answerDTO.getFileName().trim().isEmpty()) {
                            System.out.println("🔍 Processing file submission for questionId: " + answerDTO.getQuestionId());
                            System.out.println("🔍 FileName: " + answerDTO.getFileName());
                            System.out.println("🔍 FilePath: " + answerDTO.getFilePath());
                            
                            userAnswer.setFileName(answerDTO.getFileName());
                            System.out.println("✅ Set fileName: " + answerDTO.getFileName());
                            
                            // Also set filePath if provided
                            if (answerDTO.getFilePath() != null && !answerDTO.getFilePath().trim().isEmpty()) {
                                userAnswer.setFilePath(answerDTO.getFilePath());
                                System.out.println("✅ Set filePath: " + answerDTO.getFilePath());
                            } else {
                                System.out.println("⚠️ FilePath is null or empty for file: " + answerDTO.getFileName());
                            }
                        }
                    }
                }
                
                // Handle essay questions with only linkAnswer or fileName (no answerText)
                if (question.getType() == Questions.Type.ESSAY && 
                    (answerDTO.getLinkAnswer() != null && !answerDTO.getLinkAnswer().trim().isEmpty()) ||
                    (answerDTO.getFileName() != null && !answerDTO.getFileName().trim().isEmpty())) {
                    
                    System.out.println("🔍 Processing Essay Question (additional check) ID: " + answerDTO.getQuestionId());
                    System.out.println("🔍 LinkAnswer: " + answerDTO.getLinkAnswer());
                    System.out.println("🔍 FileName: " + answerDTO.getFileName());
                    
                    // Ensure answerText is set (can be empty)
                    if (userAnswer.getAnswerText() == null) {
                        userAnswer.setAnswerText(answerDTO.getAnswerText());
                    }
                    userAnswer.setIsCorrect(null); // Will be graded manually
                    
                    // Handle essay link submission
                    if (answerDTO.getLinkAnswer() != null && !answerDTO.getLinkAnswer().trim().isEmpty()) {
                        System.out.println("✅ Setting linkAnswer: " + answerDTO.getLinkAnswer());
                        userAnswer.setLinkAnswer(answerDTO.getLinkAnswer());
                    }
                    
                    // Handle essay file submission
                    if (answerDTO.getFileName() != null && !answerDTO.getFileName().trim().isEmpty()) {
                        System.out.println("🔍 Processing file submission for questionId: " + answerDTO.getQuestionId());
                        System.out.println("🔍 FileName: " + answerDTO.getFileName());
                        System.out.println("🔍 FilePath: " + answerDTO.getFilePath());
                        
                        userAnswer.setFileName(answerDTO.getFileName());
                        System.out.println("✅ Set fileName: " + answerDTO.getFileName());
                        
                        // Also set filePath if provided
                        if (answerDTO.getFilePath() != null && !answerDTO.getFilePath().trim().isEmpty()) {
                            userAnswer.setFilePath(answerDTO.getFilePath());
                            System.out.println("✅ Set filePath: " + answerDTO.getFilePath());
                        } else {
                            System.out.println("⚠️ FilePath is null or empty for file: " + answerDTO.getFileName());
                        }
                    }
                }
                
                System.out.println("💾 About to insert UserAnswer:");
                System.out.println("   fileName: " + userAnswer.getFileName());
                System.out.println("   filePath: " + userAnswer.getFilePath());
                System.out.println("   answerText: " + userAnswer.getAnswerText());
                System.out.println("   linkAnswer: " + userAnswer.getLinkAnswer());
                
                userAnswerMapper.insertUserAnswer(userAnswer);
                System.out.println("✅ UserAnswer inserted successfully");
                userAnswers.add(userAnswer);
            }
            
            // Grade the quiz and get result
            QuizResultDTO result = quizResultService.gradeQuiz(attempt, userAnswers);
            
            // Update attempt score with actual points earned (not percentage)
            attempt.setScore((int) Math.round(result.getEarnedPoints()));
            userQuizAttemptMapper.updateAttemptScore(attempt.getId(), attempt.getScore());
            
            // Update module progress when quiz is completed
            if (quiz.getModule() != null) {
                Integer moduleId = quiz.getModule().getId();
                System.out.println("🎯 Updating test progress for user " + userId + " in module " + moduleId);
                moduleProgressService.updateTestProgress(userId, moduleId, true);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error submitting exam: " + e.getMessage(), e);
        }
    }

    /**
     * Check if user has already submitted a quiz
     */
    public boolean hasUserSubmittedQuiz(Integer userId, Integer quizId) {
        try {
            System.out.println("=== Checking if user " + userId + " has submitted quiz " + quizId + " ===");
            
            if (userId == null || quizId == null) {
                System.out.println("User ID or Quiz ID is null - returning false");
                return false;
            }

            // Check if user has any attempts (regardless of multiple attempts setting)
            UserQuizAttempt attempt = userQuizAttemptMapper.findByUserAndQuiz(userId, quizId);
            boolean hasSubmitted = attempt != null;
            
            System.out.println("Query result: " + (attempt != null ? "Found attempt" : "No attempt found"));
            System.out.println("Returning hasSubmitted: " + hasSubmitted);
            
            return hasSubmitted;
        } catch (Exception e) {
            System.err.println("Error checking if user submitted quiz: " + e.getMessage());
            e.printStackTrace();
            return false; // Default to false if error
        }
    }

    /**
     * Check if user can take quiz again (for multiple attempts)
     */
    public boolean canUserRetakeQuiz(Integer userId, Integer quizId) {
        try {
            System.out.println("=== Checking if user " + userId + " can retake quiz " + quizId + " ===");
            
            if (userId == null || quizId == null) {
                System.out.println("User ID or Quiz ID is null - returning false");
                return false;
            }

            // First, get the quiz to check if multiple attempts are allowed
            Quizzes quiz = quizzesMapper.findById(quizId);
            if (quiz == null) {
                System.out.println("Quiz not found - returning false");
                return false;
            }

            System.out.println("Quiz allowMultipleAttempts: " + quiz.getAllowMultipleAttempts());
            System.out.println("Quiz maxAttempts: " + quiz.getMaxAttempts());

            // If multiple attempts are not allowed, use old logic
            if (quiz.getAllowMultipleAttempts() == null || !quiz.getAllowMultipleAttempts()) {
                // For single attempt quizzes, check if user has already submitted
                UserQuizAttempt attempt = userQuizAttemptMapper.findByUserAndQuiz(userId, quizId);
                boolean canRetake = attempt == null; // Can only take if never attempted
                
                System.out.println("Single attempt quiz - can retake: " + canRetake);
                return canRetake;
            }

            // For multiple attempts, check attempt count vs maxAttempts
            int currentAttempts = getUserAttemptCount(userId, quizId);
            int maxAttempts = quiz.getMaxAttempts() != null ? quiz.getMaxAttempts() : 2; // Default to 2
            
            boolean canRetake = currentAttempts < maxAttempts;
            
            System.out.println("Multiple attempts quiz - current attempts: " + currentAttempts + 
                             ", max attempts: " + maxAttempts + ", can retake: " + canRetake);
            
            return canRetake;
        } catch (Exception e) {
            System.err.println("Error checking if user can retake quiz: " + e.getMessage());
            e.printStackTrace();
            return false; // Default to false if error
        }
    }

    /**
     * Get user's quiz result
     */
    public QuizResultDTO getUserQuizResult(Integer userId, Integer quizId) {
        try {
            System.out.println("=== Getting quiz result for user " + userId + " and quiz " + quizId + " ===");
            
            if (userId == null || quizId == null) {
                System.out.println("User ID or Quiz ID is null - returning null");
                return null;
            }

            // Get the quiz to check if multiple attempts are allowed
            Quizzes quiz = quizzesMapper.findById(quizId);
            if (quiz == null) {
                System.out.println("Quiz not found - returning null");
                return null;
            }

            UserQuizAttempt attempt = null;

            if (quiz.getAllowMultipleAttempts() != null && quiz.getAllowMultipleAttempts()) {
                // For multiple attempts, get the latest attempt
                List<UserQuizAttempt> attempts = userQuizAttemptMapper.findAllAttemptsByUserAndQuiz(userId, quizId);
                if (attempts != null && !attempts.isEmpty()) {
                    attempt = attempts.get(0); // First one is the latest due to ORDER BY attempted_at DESC
                    System.out.println("Found " + attempts.size() + " attempts, using latest attempt: " + attempt.getId());
                }
            } else {
                // For single attempt, get the only attempt
                attempt = userQuizAttemptMapper.findByUserAndQuiz(userId, quizId);
                System.out.println("Single attempt mode - found attempt: " + (attempt != null ? attempt.getId() : "none"));
            }
            
            if (attempt == null) {
                System.out.println("No attempt found - returning null");
                return null;
            }
            
            System.out.println("Attempt found: " + attempt.getId());
            
            List<UserAnswer> userAnswers = userAnswerMapper.findByAttemptId(attempt.getId());
            System.out.println("Found " + userAnswers.size() + " user answers");
            
            QuizResultDTO result = quizResultService.gradeQuiz(attempt, userAnswers);
            System.out.println("Grading completed: " + (result != null ? "Success" : "Failed"));
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Error getting user quiz result: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get attempt count for a user and quiz
     */
    public int getUserAttemptCount(Integer userId, Integer quizId) {
        try {
            if (userId == null || quizId == null) {
                return 0;
            }
            return userQuizAttemptMapper.countAttemptsByUserAndQuiz(userId, quizId);
        } catch (Exception e) {
            System.err.println("Error getting user attempt count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all attempts for a user and quiz
     */
    public List<UserQuizAttempt> getUserAttempts(Integer userId, Integer quizId) {
        try {
            if (userId == null || quizId == null) {
                return new ArrayList<>();
            }
            return userQuizAttemptMapper.findAllAttemptsByUserAndQuiz(userId, quizId);
        } catch (Exception e) {
            System.err.println("Error getting user attempts: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

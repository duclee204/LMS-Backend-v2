package org.example.lmsbackend.service;

import org.example.lmsbackend.dto.GradeDTO;
import org.example.lmsbackend.model.*;
import org.example.lmsbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class GradesService {

    @Autowired
    private UserQuizAttemptMapper userQuizAttemptMapper;

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private QuizzesMapper quizzesMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * Get grades for instructor - can filter by type (MULTIPLE_CHOICE, ESSAY, ALL)
     */
    public List<GradeDTO> getInstructorGrades(Integer courseId, String type) {
        try {
            System.out.println("=== Getting instructor grades for course: " + courseId + ", type: " + type + " ===");
            
            List<GradeDTO> grades = new ArrayList<>();
            
            // Get all quiz attempts for the course
            List<UserQuizAttempt> attempts = userQuizAttemptMapper.findByCourseId(courseId);
            System.out.println("Found " + attempts.size() + " attempts");
            
            for (UserQuizAttempt attempt : attempts) {
                Quizzes quiz = attempt.getQuiz();
                User student = attempt.getUser();
                
                // Skip if quiz type doesn't match filter
                if (!type.equals("ALL")) {
                    if (quiz.getQuizType() == null || !quiz.getQuizType().toString().equals(type)) {
                        continue;
                    }
                }
                
                // Reload attempt to get latest score (in case it was just updated)
                UserQuizAttempt latestAttempt = userQuizAttemptMapper.findById(attempt.getId());
                if (latestAttempt != null) {
                    System.out.println("Original score: " + attempt.getScore() + ", Latest score: " + latestAttempt.getScore());
                    attempt = latestAttempt;
                }
                
                GradeDTO grade = new GradeDTO();
                grade.setAttemptId(attempt.getId());
                grade.setUserId(student.getUserId());
                grade.setStudentName(student.getFullName());
                grade.setQuizId(quiz.getQuizId());
                grade.setQuizTitle(quiz.getTitle());
                grade.setQuizType(quiz.getQuizType() != null ? quiz.getQuizType().toString() : "UNKNOWN");
                grade.setScore(attempt.getScore());
                grade.setSubmittedAt(attempt.getAttemptedAt());
                
                // Calculate max score properly (sum of all question points)
                List<Questions> questions = questionsMapper.findByQuizId(quiz.getQuizId());
                int maxScore = questions.stream()
                    .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                    .sum();
                grade.setMaxScore(maxScore);
                
                // For essay questions, check if all are graded
                if (quiz.getQuizType() == Quizzes.QuizType.ESSAY) {
                    List<UserAnswer> answers = userAnswerMapper.findByAttemptId(attempt.getId());
                    boolean allGraded = answers.stream()
                        .allMatch(answer -> answer.getManualScore() != null);
                    grade.setStatus(allGraded ? "COMPLETED" : "PENDING_GRADE");
                    
                    // Set userAnswerId for the first essay answer (for loading details)
                    if (!answers.isEmpty()) {
                        grade.setUserAnswerId(answers.get(0).getId());
                    }
                } else {
                    grade.setStatus("COMPLETED");
                }
                
                grades.add(grade);
            }
            
            System.out.println("Returning " + grades.size() + " grades");
            return grades;
            
        } catch (Exception e) {
            System.err.println("Error getting instructor grades: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting instructor grades: " + e.getMessage(), e);
        }
    }

    /**
     * Get grades for a specific student
     */
    public List<GradeDTO> getStudentGrades(Integer userId) {
        try {
            System.out.println("=== Getting grades for student: " + userId + " ===");
            
            List<GradeDTO> grades = new ArrayList<>();
            
            // Get all attempts by this student
            List<UserQuizAttempt> attempts = userQuizAttemptMapper.findByUserId(userId);
            System.out.println("Found " + attempts.size() + " attempts");
            
            for (UserQuizAttempt attempt : attempts) {
                Quizzes quiz = attempt.getQuiz();
                
                GradeDTO grade = new GradeDTO();
                grade.setAttemptId(attempt.getId());
                grade.setUserId(userId);
                grade.setQuizId(quiz.getQuizId());
                grade.setQuizTitle(quiz.getTitle());
                grade.setQuizType(quiz.getQuizType() != null ? quiz.getQuizType().toString() : "UNKNOWN");
                grade.setSubmittedAt(attempt.getAttemptedAt());
                
                // Calculate scores properly (sum of all question points)
                List<Questions> questions = questionsMapper.findByQuizId(quiz.getQuizId());
                int maxScore = questions.stream()
                    .mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1)
                    .sum();
                grade.setMaxScore(maxScore);
                
                if (quiz.getQuizType() == Quizzes.QuizType.ESSAY) {
                    // For essay, calculate score based on manual grading
                    List<UserAnswer> answers = userAnswerMapper.findByAttemptId(attempt.getId());
                    
                    boolean allGraded = true;
                    
                    for (UserAnswer answer : answers) {
                        if (answer.getManualScore() == null) {
                            allGraded = false;
                            break;
                        }
                    }
                    
                    // Set score as percentage (matching how it's stored in database)
                    grade.setScore(attempt.getScore()); // Use the updated score from database
                    grade.setStatus(allGraded ? "COMPLETED" : "PENDING_GRADE");
                } else {
                    // For multiple choice, use existing score
                    grade.setScore(attempt.getScore());
                    grade.setStatus("COMPLETED");
                }
                
                grades.add(grade);
            }
            
            System.out.println("Returning " + grades.size() + " grades");
            return grades;
            
        } catch (Exception e) {
            System.err.println("Error getting student grades: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting student grades: " + e.getMessage(), e);
        }
    }

    /**
     * Grade an essay answer
     */
    @Transactional
    public boolean gradeEssayAnswer(Integer userAnswerId, Integer score, String feedback) {
        try {
            System.out.println("=== Grading essay answer: " + userAnswerId + " ===");
            System.out.println("Score: " + score + ", Feedback: " + feedback);
            
            UserAnswer answer = userAnswerMapper.findById(userAnswerId);
            if (answer == null) {
                System.out.println("❌ User answer not found");
                return false;
            }
            
            System.out.println("Found answer for attempt ID: " + answer.getAttempt().getId());
            
            // Get the attempt with full information
            UserQuizAttempt attempt = userQuizAttemptMapper.findById(answer.getAttempt().getId());
            if (attempt == null) {
                System.out.println("❌ Attempt not found");
                return false;
            }
            
            System.out.println("Found attempt with quiz ID: " + attempt.getQuiz().getQuizId());
            
            // Update the answer with manual score and feedback
            answer.setManualScore(score);
            answer.setInstructorFeedback(feedback);
            answer.setIsCorrect(score > 0); // Consider it correct if score > 0
            
            userAnswerMapper.updateUserAnswer(answer);
            System.out.println("✅ Updated answer with manualScore: " + answer.getManualScore());
            
            // Force a reload to verify the update was saved
            UserAnswer verifyAnswer = userAnswerMapper.findById(userAnswerId);
            System.out.println("✅ Verification: reloaded answer manualScore = " + 
                (verifyAnswer != null ? verifyAnswer.getManualScore() : "null"));
            
            // Update the overall attempt score
            // Reload ALL answers to get updated data
            List<UserAnswer> allAnswers = userAnswerMapper.findByAttemptId(attempt.getId());
            List<Questions> allQuestions = questionsMapper.findByQuizId(attempt.getQuiz().getQuizId());
            
            // Calculate actual score: sum of actual points earned
            int totalActualScore = 0;
            int totalMaxScore = 0;
            int totalQuestions = allQuestions.size();
            
            System.out.println("Debug: Found " + allAnswers.size() + " answers for " + totalQuestions + " questions");
            
            for (UserAnswer ans : allAnswers) {
                System.out.println("Answer ID " + ans.getId() + ": manualScore = " + ans.getManualScore());
                
                // Get the question's maximum points
                Questions question = ans.getQuestion();
                int maxPoints = (question != null && question.getPoints() != null) ? question.getPoints() : 1;
                totalMaxScore += maxPoints;
                
                if (ans.getManualScore() != null) {
                    // Cap the actual score to max points for this question
                    int actualScore = Math.min(ans.getManualScore(), maxPoints);
                    totalActualScore += actualScore;
                    
                    // Update isCorrect based on 50% threshold for individual question
                    boolean isCorrect = actualScore >= (maxPoints * 0.5);
                    ans.setIsCorrect(isCorrect);
                    userAnswerMapper.updateUserAnswer(ans);
                    
                    System.out.println("✓ Question scored " + actualScore + "/" + maxPoints + " points (isCorrect: " + isCorrect + ")");
                } else {
                    System.out.println("✗ Question not graded yet (0/" + maxPoints + " points)");
                }
            }
            
            // Save total actual score earned (not count of correct answers)
            System.out.println("Score calculation: " + totalActualScore + "/" + totalMaxScore + " total points");
            
            userQuizAttemptMapper.updateAttemptScore(attempt.getId(), totalActualScore);
            
            System.out.println("✅ Essay graded successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error grading essay answer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get detailed essay answer for grading
     */
    public UserAnswer getEssayAnswerDetails(Integer userAnswerId) {
        try {
            System.out.println("=== Getting essay answer details for ID: " + userAnswerId + " ===");
            
            UserAnswer answer = userAnswerMapper.findById(userAnswerId);
            if (answer == null) {
                System.out.println("No answer found for ID: " + userAnswerId);
                return null;
            }
            
            // Load related question details
            if (answer.getQuestion() != null) {
                Questions question = questionsMapper.findById(answer.getQuestion().getQuestionId());
                answer.setQuestion(question);
            }
            
            System.out.println("✅ Essay answer details loaded successfully");
            return answer;
            
        } catch (Exception e) {
            System.err.println("Error loading essay answer details: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get student's own essay submission details
     */
    public Map<String, Object> getStudentEssaySubmission(Integer attemptId, Integer studentId) {
        try {
            System.out.println("=== Getting student essay submission for attempt: " + attemptId + ", student: " + studentId + " ===");
            
            // Get the attempt and verify it belongs to this student
            UserQuizAttempt attempt = userQuizAttemptMapper.findById(attemptId);
            if (attempt == null) {
                System.out.println("❌ Attempt not found");
                return null;
            }
            
            if (!attempt.getUser().getUserId().equals(studentId)) {
                System.out.println("❌ Attempt does not belong to this student");
                return null;
            }
            
            // Get all answers for this attempt
            List<UserAnswer> answers = userAnswerMapper.findByAttemptId(attemptId);
            
            // Get quiz details
            Quizzes quiz = attempt.getQuiz();
            
            // Get all questions for this quiz
            List<Questions> questions = questionsMapper.findByQuizId(quiz.getQuizId());
            
            // Calculate total score and status
            int totalQuestions = questions.size();
            int gradedCount = 0;
            int totalScore = 0;
            int maxScore = 0;
            
            // Calculate max possible score from questions
            for (Questions question : questions) {
                int questionPoints = (question.getPoints() != null) ? question.getPoints() : 1;
                maxScore += questionPoints;
            }
            
            for (UserAnswer answer : answers) {
                if (answer.getManualScore() != null) {
                    gradedCount++;
                    totalScore += answer.getManualScore();
                }
            }
            
            boolean isFullyGraded = gradedCount == totalQuestions;
            
            // Build response
            Map<String, Object> result = new HashMap<>();
            result.put("attemptId", attemptId);
            result.put("quizTitle", quiz.getTitle());
            result.put("submittedAt", attempt.getAttemptedAt());
            result.put("totalQuestions", totalQuestions);
            result.put("gradedCount", gradedCount);
            result.put("totalScore", totalScore);
            result.put("maxScore", maxScore);
            result.put("isFullyGraded", isFullyGraded);
            result.put("questions", questions);
            result.put("answers", answers);
            
            System.out.println("✅ Student essay submission details loaded successfully");
            return result;
            
        } catch (Exception e) {
            System.err.println("Error loading student essay submission: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

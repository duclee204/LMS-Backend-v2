package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Questions;
import org.example.lmsbackend.model.Answer;
import org.example.lmsbackend.model.UserAnswer;
import org.example.lmsbackend.model.UserQuizAttempt;
import org.example.lmsbackend.dto.QuizResultDTO;
import org.example.lmsbackend.dto.QuestionResultDTO;
import org.example.lmsbackend.dto.QuizResultDetailDTO;
import org.example.lmsbackend.repository.QuestionsMapper;
import org.example.lmsbackend.repository.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizResultService {

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswerMapper answerMapper;

    /**
     * Chấm điểm một bài thi của sinh viên
     * @param attempt Thông tin attempt của sinh viên
     * @param userAnswers Danh sách câu trả lời của sinh viên
     * @return Điểm số và chi tiết kết quả
     */
    @Transactional(readOnly = true)
    public QuizResultDTO gradeQuiz(UserQuizAttempt attempt, List<UserAnswer> userAnswers) {
        
        // Lấy tất cả câu hỏi của quiz với đáp án đúng
        List<Questions> questions = questionsMapper.findByQuizId(attempt.getQuiz().getQuizId());
        
        double totalPoints = 0;
        double earnedPoints = 0;
        
        QuizResultDTO result = new QuizResultDTO();
        result.setAttemptId(attempt.getId());
        result.setQuizId(attempt.getQuiz().getQuizId());
        result.setUserId(attempt.getUser().getUserId());
        
        // Duyệt qua từng câu hỏi để chấm điểm
        for (Questions question : questions) {
            totalPoints += question.getPoints();
            
            // Tìm câu trả lời của sinh viên cho câu hỏi này
            UserAnswer userAnswer = userAnswers.stream()
                .filter(ua -> ua.getQuestion().getQuestionId().equals(question.getQuestionId()))
                .findFirst()
                .orElse(null);
            
            if (userAnswer != null) {
                // Calculate actual earned points for this question
                double questionEarnedPoints = 0;
                boolean isCorrect = false;
                
                if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
                    isCorrect = checkAnswer(question, userAnswer);
                    questionEarnedPoints = isCorrect ? question.getPoints() : 0;
                } else if (question.getType() == Questions.Type.ESSAY) {
                    // For essay, use manual score if available
                    if (userAnswer.getManualScore() != null) {
                        // Cap earned points to max points for this question
                        questionEarnedPoints = Math.min(userAnswer.getManualScore(), question.getPoints());
                        // Consider correct if earned at least 50% of max points
                        isCorrect = questionEarnedPoints >= (question.getPoints() * 0.5);
                    }
                }
                
                earnedPoints += questionEarnedPoints;
                
                // Tạo chi tiết kết quả cho từng câu
                QuestionResultDTO questionResult = new QuestionResultDTO();
                questionResult.setQuestionId(question.getQuestionId());
                questionResult.setQuestionText(question.getQuestionText());
                questionResult.setQuestionType(question.getType().toString());
                questionResult.setPoints(question.getPoints()); // max points
                questionResult.setEarnedPoints(questionEarnedPoints); // actual earned points
                questionResult.setCorrect(isCorrect);
                questionResult.setUserAnswer(getUserAnswerText(question, userAnswer));
                questionResult.setCorrectAnswer(getCorrectAnswerText(question));
                
                result.getQuestionResults().add(questionResult);
            }
        }
        
        result.setTotalPoints(totalPoints);
        result.setEarnedPoints(earnedPoints);
        result.setScore((totalPoints > 0) ? (earnedPoints / totalPoints) * 100 : 0);
        
        return result;
    }
    
    /**
     * Kiểm tra câu trả lời của sinh viên có đúng không
     */
    private boolean checkAnswer(Questions question, UserAnswer userAnswer) {
        
        if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
            // Với trắc nghiệm, kiểm tra answer_id
            if (userAnswer.getAnswer() != null) {
                return userAnswer.getAnswer().getIsCorrect();
            }
        } else if (question.getType() == Questions.Type.ESSAY) {
            // Với tự luận, cần giảng viên chấm thủ công
            // Tạm thời trả về false, sẽ được cập nhật sau
            return userAnswer.getIsCorrect() != null && userAnswer.getIsCorrect();
        }
        
        return false;
    }
    
    /**
     * Lấy text câu trả lời của sinh viên
     */
    private String getUserAnswerText(Questions question, UserAnswer userAnswer) {
        
        if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
            if (userAnswer.getAnswer() != null) {
                return userAnswer.getAnswer().getAnswerText();
            }
        } else if (question.getType() == Questions.Type.ESSAY) {
            return userAnswer.getAnswerText();
        }
        
        return "Không có câu trả lời";
    }
    
    /**
     * Lấy text đáp án đúng
     */
    private String getCorrectAnswerText(Questions question) {
        
        if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
            List<Answer> answers = answerMapper.findByQuestionId(question.getQuestionId());
            return answers.stream()
                .filter(Answer::getIsCorrect)
                .map(Answer::getAnswerText)
                .findFirst()
                .orElse("Không có đáp án đúng");
        } else {
            return "Câu tự luận - cần chấm thủ công";
        }
    }
    
    /**
     * Lấy kết quả chi tiết của một quiz attempt
     */
    public QuizResultDetailDTO getQuizResultDetail(Integer attemptId) {
        // TODO: Implement logic to get detailed result
        // Bao gồm: thông tin attempt, danh sách câu hỏi, câu trả lời, điểm số
        return null;
    }
    
    /**
     * Lấy kết quả của một attempt
     */
    public QuizResultDTO getAttemptResult(Integer attemptId) {
        // TODO: Implement logic to get attempt result with grading
        // Tạm thời trả về null, sẽ implement sau
        return null;
    }
}

package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Questions;
import org.example.lmsbackend.model.Answer;
import org.example.lmsbackend.model.Quizzes;
import org.example.lmsbackend.dto.QuestionsDTO;
import org.example.lmsbackend.dto.AnswerDTO;
import org.example.lmsbackend.dto.QuizUpdateDTO;
import org.example.lmsbackend.repository.QuestionsMapper;
import org.example.lmsbackend.repository.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class QuestionsService {

    @Autowired
    private QuestionsMapper questionsMapper;

    @Autowired
    private AnswerMapper answerMapper;

    // Tạo mới câu hỏi với answers và trả về DTO với ID
    @Transactional
    public QuestionsDTO createQuestionWithReturn(QuestionsDTO dto) {
        try {
            // Validate input
            if (dto.getQuestionText() == null || dto.getQuestionText().trim().isEmpty()) {
                System.err.println("ERROR: Question text is null or empty!");
                throw new IllegalArgumentException("Question text cannot be null or empty");
            }
            
            // Tạo câu hỏi
            Questions question = new Questions();
            
            // Tạo Quizzes object với ID
            org.example.lmsbackend.model.Quizzes quiz = new org.example.lmsbackend.model.Quizzes();
            quiz.setQuizId(dto.getQuizId());
            question.setQuiz(quiz);
            
            question.setQuestionText(dto.getQuestionText().trim());
            question.setType(Questions.Type.valueOf(dto.getType()));
            question.setPoints(dto.getPoints());
            question.setQuestionFileUrl(dto.getQuestionFileUrl());
            question.setQuestionFileName(dto.getQuestionFileName());

            questionsMapper.insertQuestion(question);

            // Nếu là câu hỏi trắc nghiệm, thêm các lựa chọn
            if (dto.getType().equals("MULTIPLE_CHOICE") && dto.getAnswers() != null && !dto.getAnswers().isEmpty()) {
                for (int i = 0; i < dto.getAnswers().size(); i++) {
                    AnswerDTO answerDTO = dto.getAnswers().get(i);
                    
                    Answer answer = new Answer();
                    answer.setQuestion(question);
                    answer.setAnswerText(answerDTO.getAnswerText());
                    answer.setIsCorrect(answerDTO.getIsCorrect());
                    answer.setOrderNumber(i + 1); // Thứ tự từ 1

                    answerMapper.insertAnswer(answer);
                }
            }

            // Convert to DTO and return with ID
            return convertToDTO(question);
        } catch (Exception e) {
            System.err.println("Error creating question: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Tạo mới câu hỏi với answers (legacy method)
    @Transactional
    public boolean createQuestion(QuestionsDTO dto) {
        return createQuestionWithReturn(dto) != null;
    }

    // Lấy danh sách câu hỏi theo quiz
    public List<Questions> getQuestionsByQuizId(int quizId) {
        return questionsMapper.findByQuizId(quizId);
    }

    // Cập nhật câu hỏi và answers
    @Transactional
    public boolean updateQuestion(Integer questionId, QuestionsDTO dto) {
        try {
            Questions existingQuestion = questionsMapper.findById(questionId);
            if (existingQuestion == null) {
                return false;
            }

            // Cập nhật thông tin câu hỏi
            existingQuestion.setQuestionText(dto.getQuestionText());
            existingQuestion.setType(Questions.Type.valueOf(dto.getType()));
            existingQuestion.setPoints(dto.getPoints());

            questionsMapper.updateQuestion(existingQuestion);

            // Nếu là câu hỏi trắc nghiệm, cập nhật answers
            if (dto.getType().equals("MULTIPLE_CHOICE")) {
                // Xóa tất cả answers cũ
                answerMapper.deleteByQuestionId(questionId);

                // Thêm answers mới
                if (dto.getAnswers() != null && !dto.getAnswers().isEmpty()) {
                    for (int i = 0; i < dto.getAnswers().size(); i++) {
                        AnswerDTO answerDTO = dto.getAnswers().get(i);
                        
                        Answer answer = new Answer();
                        answer.setQuestion(existingQuestion);
                        answer.setAnswerText(answerDTO.getAnswerText());
                        answer.setIsCorrect(answerDTO.getIsCorrect());
                        answer.setOrderNumber(i + 1);

                        answerMapper.insertAnswer(answer);
                    }
                }
            } else {
                // Nếu không phải trắc nghiệm, xóa tất cả answers
                answerMapper.deleteByQuestionId(questionId);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error updating question: " + e.getMessage());
            return false;
        }
    }

    // Xóa câu hỏi và tất cả answers
    @Transactional
    public boolean deleteQuestion(Integer questionId) {
        try {
            Questions existingQuestion = questionsMapper.findById(questionId);
            if (existingQuestion == null) {
                return false;
            }

            // Xóa tất cả answers trước
            answerMapper.deleteByQuestionId(questionId);
            
            // Xóa câu hỏi
            questionsMapper.deleteQuestion(questionId);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        }
    }

    // Lấy quiz theo ID
    public Quizzes getQuizById(Integer quizId) {
        try {
            return questionsMapper.findQuizById(quizId);
        } catch (Exception e) {
            System.err.println("Error getting quiz by ID: " + e.getMessage());
            return null;
        }
    }

    // Lấy tất cả câu hỏi với answers theo quiz ID
    public List<QuestionsDTO> getQuestionsWithAnswersByQuizId(Integer quizId) {
        try {
            List<Questions> questions = questionsMapper.findByQuizId(quizId);
            
            List<QuestionsDTO> questionDTOs = new ArrayList<>();
            
            for (Questions question : questions) {
                QuestionsDTO dto = convertToDTO(question);
                questionDTOs.add(dto);
            }
            
            return questionDTOs;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Lấy một câu hỏi với answers theo question ID
    public QuestionsDTO getQuestionWithAnswersById(Integer questionId) {
        try {
            Questions question = questionsMapper.findById(questionId);
            if (question == null) {
                return null;
            }
            
            return convertToDTO(question);
        } catch (Exception e) {
            System.err.println("Error getting question by ID: " + e.getMessage());
            return null;
        }
    }

    // Cập nhật thông tin cơ bản của quiz
    @Transactional
    public boolean updateQuizBasicInfo(Integer quizId, QuizUpdateDTO updateData) {
        try {
            return questionsMapper.updateQuizBasicInfo(quizId, 
                updateData.getTitle(), 
                updateData.getDescription(), 
                updateData.getTimeLimit(),
                updateData.getAllowMultipleAttempts()) > 0;
        } catch (Exception e) {
            System.err.println("Error updating quiz basic info: " + e.getMessage());
            return false;
        }
    }

    // Convert Questions entity to DTO với answers
    private QuestionsDTO convertToDTO(Questions question) {
        QuestionsDTO dto = new QuestionsDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuizId(question.getQuiz() != null ? question.getQuiz().getQuizId() : null);
        dto.setQuestionText(question.getQuestionText());
        dto.setType(question.getType().toString());
        dto.setPoints(question.getPoints());
        dto.setQuestionFileUrl(question.getQuestionFileUrl());
        dto.setQuestionFileName(question.getQuestionFileName());
        
        // Lấy answers cho câu hỏi trắc nghiệm
        if (question.getType() == Questions.Type.MULTIPLE_CHOICE) {
            List<Answer> answers = answerMapper.findByQuestionId(question.getQuestionId());
            List<AnswerDTO> answerDTOs = new ArrayList<>();
            
            for (Answer answer : answers) {
                if (answer == null) {
                    continue;
                }
                AnswerDTO answerDTO = new AnswerDTO();
                answerDTO.setAnswerId(answer.getAnswerId());
                answerDTO.setQuestionId(question.getQuestionId()); // Use question.getQuestionId() directly instead of answer.getQuestion().getQuestionId()
                answerDTO.setAnswerText(answer.getAnswerText());
                answerDTO.setIsCorrect(answer.getIsCorrect());
                answerDTO.setOrderNumber(answer.getOrderNumber());
                answerDTOs.add(answerDTO);
            }
            
            dto.setAnswers(answerDTOs);
        }
        
        return dto;
    }
}

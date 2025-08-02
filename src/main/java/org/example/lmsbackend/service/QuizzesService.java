package org.example.lmsbackend.service;

import org.example.lmsbackend.model.Quizzes;
import org.example.lmsbackend.model.Questions;
import org.example.lmsbackend.model.Course;
import org.example.lmsbackend.model.Modules;
import org.example.lmsbackend.dto.QuizzesDTO;
import org.example.lmsbackend.repository.QuizzesRepository;
import org.example.lmsbackend.repository.CourseRepository;
import org.example.lmsbackend.repository.QuestionsMapper;
import org.example.lmsbackend.repository.AnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class QuizzesService {
    @Autowired
    private QuizzesRepository quizzesRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionsService questionsService;
    
    @Autowired
    private QuestionsMapper questionsMapper;
    
    @Autowired
    private AnswerMapper answerMapper;

    public Quizzes createQuiz(QuizzesDTO dto) {
        Quizzes quiz = new Quizzes();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setQuizType(dto.getQuizType());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setShuffleAnswers(dto.getShuffleAnswers());
        quiz.setAllowMultipleAttempts(dto.getAllowMultipleAttempts());
        quiz.setMaxAttempts(dto.getMaxAttempts() != null ? dto.getMaxAttempts() : 2);
        quiz.setShowQuizResponses(dto.getShowQuizResponses());
        quiz.setShowOneQuestionAtATime(dto.getShowOneQuestionAtATime());
        
        // Ensure publish is never null - default to false if not provided
        quiz.setPublish(dto.getPublish() != null ? dto.getPublish() : false);
        
        // Set course relationship properly
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + dto.getCourseId()));
            quiz.setCourse(course);
        }
        
        // Set module relationship if provided
        if (dto.getModuleId() != null) {
            Modules module = new Modules();
            module.setId(dto.getModuleId());
            quiz.setModule(module);
        }
        
        return quizzesRepository.save(quiz);
    }
    public List<QuizzesDTO> getAllQuizzes() {
        List<Quizzes> entities = quizzesRepository.findAll();
        List<QuizzesDTO> dtos = new ArrayList<>();
        for (Quizzes q : entities) {
            QuizzesDTO dto = new QuizzesDTO();
            dto.setQuizId(q.getQuizId());
            dto.setTitle(q.getTitle());
            dto.setDescription(q.getDescription());
            dto.setQuizType(q.getQuizType());
            dto.setTimeLimit(q.getTimeLimit());
            dto.setShuffleAnswers(q.getShuffleAnswers());
            dto.setAllowMultipleAttempts(q.getAllowMultipleAttempts());
            dto.setShowQuizResponses(q.getShowQuizResponses());
            dto.setShowOneQuestionAtATime(q.getShowOneQuestionAtATime());
            dto.setPublish(q.getPublish());
            dto.setCourseId(q.getCourseId());
            dtos.add(dto);
        }
        return dtos;
    }

    public List<QuizzesDTO> getQuizzesByCourse(Integer courseId, Boolean publish, Boolean withoutModule) {
        List<Quizzes> entities;
        
        if (withoutModule != null && withoutModule) {
            // Get quizzes without module (moduleId is null)
            if (publish != null && publish) {
                entities = quizzesRepository.findByCourseIdAndPublishTrueAndModuleIsNull(courseId);
            } else {
                entities = quizzesRepository.findByCourseIdAndModuleIsNull(courseId);
            }
        } else {
            // Get all quizzes (with and without module)
            if (publish != null && publish) {
                entities = quizzesRepository.findByCourseIdAndPublishTrue(courseId);
            } else {
                entities = quizzesRepository.findByCourseId(courseId);
            }
        }
        
        List<QuizzesDTO> dtos = new ArrayList<>();
        for (Quizzes q : entities) {
            QuizzesDTO dto = new QuizzesDTO();
            dto.setQuizId(q.getQuizId());
            dto.setTitle(q.getTitle());
            dto.setDescription(q.getDescription());
            dto.setQuizType(q.getQuizType());
            dto.setTimeLimit(q.getTimeLimit());
            dto.setShuffleAnswers(q.getShuffleAnswers());
            dto.setAllowMultipleAttempts(q.getAllowMultipleAttempts());
            dto.setShowQuizResponses(q.getShowQuizResponses());
            dto.setShowOneQuestionAtATime(q.getShowOneQuestionAtATime());
            dto.setPublish(q.getPublish());
            dto.setCourseId(q.getCourseId());
            dtos.add(dto);
        }
        return dtos;
    }

    // Keep the old method for backward compatibility
    public List<QuizzesDTO> getQuizzesByCourse(Integer courseId, Boolean publish) {
        return getQuizzesByCourse(courseId, publish, null);
    }

    public void updateQuiz(QuizzesDTO dto) {
        Quizzes quiz = new Quizzes();
        quiz.setQuizId(dto.getQuizId());
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setQuizType(dto.getQuizType());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setShuffleAnswers(dto.getShuffleAnswers());
        quiz.setAllowMultipleAttempts(dto.getAllowMultipleAttempts());
        quiz.setMaxAttempts(dto.getMaxAttempts() != null ? dto.getMaxAttempts() : 2);
        quiz.setShowQuizResponses(dto.getShowQuizResponses());
        quiz.setShowOneQuestionAtATime(dto.getShowOneQuestionAtATime());
        quiz.setPublish(dto.getPublish());
        
        // Set course relationship properly
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + dto.getCourseId()));
            quiz.setCourse(course);
        }
        
        quizzesRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(int quizId) {
        try {
            // First check if quiz exists
            Quizzes quiz = quizzesRepository.findById(quizId).orElse(null);
            if (quiz == null) {
                throw new RuntimeException("Quiz not found with ID: " + quizId);
            }
            
            // Delete all questions and their related data for this quiz
            // The cascade should handle UserAnswers and Answers automatically
            // But let's be explicit to avoid constraint issues
            List<org.example.lmsbackend.model.Questions> questions = questionsMapper.findByQuizId(quizId);
            
            for (org.example.lmsbackend.model.Questions question : questions) {
                // Delete answers first (though cascade should handle this)
                answerMapper.deleteByQuestionId(question.getQuestionId());
                // Then delete the question (this should cascade delete user_answers)
                questionsMapper.deleteQuestion(question.getQuestionId());
            }
            
            // The UserQuizAttempts should be cascade deleted by database constraint
            // Finally delete the quiz
            quizzesRepository.deleteById(quizId);
        } catch (Exception e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete quiz with ID: " + quizId + ". " + e.getMessage(), e);
        }
    }

    public Integer getCourseIdByQuizId(Integer quizId) {
        Quizzes quiz = quizzesRepository.findById(quizId).orElse(null);
        return (quiz != null) ? quiz.getCourseId() : null;
    }

    public QuizzesDTO getQuizById(Integer quizId) {
        Quizzes quiz = quizzesRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return null;
        }
        
        QuizzesDTO dto = new QuizzesDTO();
        dto.setQuizId(quiz.getQuizId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setQuizType(quiz.getQuizType());
        dto.setTimeLimit(quiz.getTimeLimit());
        dto.setShuffleAnswers(quiz.getShuffleAnswers());
        dto.setAllowMultipleAttempts(quiz.getAllowMultipleAttempts());
        dto.setMaxAttempts(quiz.getMaxAttempts());
        dto.setShowQuizResponses(quiz.getShowQuizResponses());
        dto.setShowOneQuestionAtATime(quiz.getShowOneQuestionAtATime());
        dto.setPublish(quiz.getPublish());
        dto.setCourseId(quiz.getCourseId());
        
        return dto;
    }

    public Map<String, Object> getQuizWithQuestions(Integer quizId) {
        QuizzesDTO quiz = getQuizById(quizId);
        if (quiz == null) {
            return null;
        }
        
        List<Questions> questions = questionsService.getQuestionsByQuizId(quizId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("quiz", quiz);
        result.put("questions", questions);
        
        return result;
    }

    public void updateQuizStatus(Integer quizId, boolean publish) {
        System.out.println("=== Updating Quiz Status ===");
        System.out.println("Quiz ID: " + quizId);
        System.out.println("New Status: " + (publish ? "Published" : "Not Published"));
        
        Quizzes quiz = quizzesRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));
        
        boolean oldStatus = quiz.getPublish() != null ? quiz.getPublish() : false;
        System.out.println("Old Status: " + (oldStatus ? "Published" : "Not Published"));
        
        quiz.setPublish(publish);
        Quizzes savedQuiz = quizzesRepository.save(quiz);
        
        System.out.println("✅ Quiz status updated. New status: " + 
            (savedQuiz.getPublish() ? "Published" : "Not Published"));
    }

    public List<QuizzesDTO> getQuizzesByModule(Integer moduleId, Boolean publish) {
        List<Quizzes> entities;
        if (publish != null && publish) {
            entities = quizzesRepository.findByModuleIdAndPublishTrue(moduleId);
        } else {
            entities = quizzesRepository.findByModuleIdOrderByOrderNumber(moduleId);
        }
        
        List<QuizzesDTO> dtos = new ArrayList<>();
        for (Quizzes q : entities) {
            QuizzesDTO dto = new QuizzesDTO();
            dto.setQuizId(q.getQuizId());
            dto.setTitle(q.getTitle());
            dto.setDescription(q.getDescription());
            dto.setQuizType(q.getQuizType());
            dto.setTimeLimit(q.getTimeLimit());
            dto.setShuffleAnswers(q.getShuffleAnswers());
            dto.setAllowMultipleAttempts(q.getAllowMultipleAttempts());
            dto.setMaxAttempts(q.getMaxAttempts());
            dto.setShowQuizResponses(q.getShowQuizResponses());
            dto.setShowOneQuestionAtATime(q.getShowOneQuestionAtATime());
            dto.setPublish(q.getPublish());
            dto.setCourseId(q.getCourseId());
            dtos.add(dto);
        }
        return dtos;
    }

    // Method to update all quizzes in a module when module status changes
    public void updateQuizzesByModuleStatus(int moduleId, boolean published) {
        try {
            List<Quizzes> quizzes = quizzesRepository.findByModuleIdOrderByOrderNumber(moduleId);
            for (Quizzes quiz : quizzes) {
                boolean oldStatus = quiz.getPublish() != null ? quiz.getPublish() : false;
                if (oldStatus != published) {
                    quiz.setPublish(published);
                    quizzesRepository.save(quiz);
                    System.out.println("Quiz '" + quiz.getTitle() + "' status changed from " + 
                        oldStatus + " to " + published);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating quizzes for module " + moduleId + ": " + e.getMessage());
            throw e;
        }
    }
}

package com.iepca.app.service;

import com.iepca.app.exception.ResourceNotFoundException;
import com.iepca.app.model.Grade;
import com.iepca.app.model.embedded.ScoreEntry;
import com.iepca.app.repository.GradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GradeService {

    private static final Logger logger = LoggerFactory.getLogger(GradeService.class);

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    public Grade findById(String id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CalificaciÃ³n", "id", id));
    }

    public List<Grade> findByCourse(String courseId) {
        return gradeRepository.findByCourse(courseId);
    }

    public List<Grade> findByStudent(String studentId) {
        return gradeRepository.findByStudent(studentId);
    }

    public List<Grade> findByStudentAndYear(String studentId, Integer year) {
        return gradeRepository.findByStudentAndAcademicYear(studentId, year);
    }

    public List<Grade> findByCourseAndBimester(String courseId, Integer bimester) {
        return gradeRepository.findByCourseAndBimesterAndAcademicYear(
                courseId, bimester, Year.now().getValue());
    }

    public Grade findOrCreate(String studentId, String courseId, Integer bimester, String teacherId) {
        int year = Year.now().getValue();
        return gradeRepository.findByStudentAndCourseAndBimesterAndAcademicYear(
                        studentId, courseId, bimester, year)
                .orElseGet(() -> {
                    Grade grade = Grade.builder()
                            .student(studentId)
                            .course(courseId)
                            .bimester(bimester)
                            .academicYear(year)
                            .teacher(teacherId)
                            .scores(new ArrayList<>())
                            .build();
                    return gradeRepository.save(grade);
                });
    }

    public Grade updateScore(String gradeId, String evaluationId, Double score,
                             String comments, String gradedBy) {
        Grade grade = findById(gradeId);

        ScoreEntry existingScore = grade.getScores().stream()
                .filter(s -> evaluationId.equals(s.getEvaluation()))
                .findFirst()
                .orElse(null);

        if (existingScore != null) {
            existingScore.setScore(score);
            existingScore.setComments(comments);
            existingScore.setGradedAt(Instant.now());
            existingScore.setGradedBy(gradedBy);
        } else {
            grade.getScores().add(ScoreEntry.builder()
                    .evaluation(evaluationId)
                    .score(score)
                    .comments(comments)
                    .gradedAt(Instant.now())
                    .gradedBy(gradedBy)
                    .build());
        }

        grade.calculateAverage();
        return gradeRepository.save(grade);
    }

    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    public void bulkUpdate(String courseId, Integer bimester, String teacherId,
                           List<com.iepca.app.dto.request.GradeUpdateRequest.GradeEntry> entries) {
        for (var entry : entries) {
            Grade grade = findOrCreate(entry.getStudentId(), courseId, bimester, teacherId);
            updateScore(grade.getId(), entry.getEvaluationId(), entry.getScore(),
                    entry.getComments(), teacherId);
        }
        logger.info("Calificaciones actualizadas en bloque: curso={}, bimestre={}, registros={}",
                courseId, bimester, entries.size());
    }

    public Grade updateScore(String gradeId, String evaluationId, Double score, String comments) {
        return updateScore(gradeId, evaluationId, score, comments, null);
    }

    public Map<String, Object> getStats() {
        List<Grade> allGrades = gradeRepository.findAll();
        long totalGrades = allGrades.size();
        double avgScore = allGrades.stream()
                .filter(g -> g.getAverage() != null)
                .mapToDouble(Grade::getAverage)
                .average()
                .orElse(0);
        long passingCount = allGrades.stream()
                .filter(g -> g.getAverage() != null && g.getAverage() >= 11)
                .count();
        double passingRate = totalGrades > 0 ? (double) passingCount / totalGrades * 100 : 0;

        return Map.of(
                "totalGrades", totalGrades,
                "avgScore", Math.round(avgScore * 100.0) / 100.0,
                "passingRate", Math.round(passingRate * 100.0) / 100.0
        );
    }
}


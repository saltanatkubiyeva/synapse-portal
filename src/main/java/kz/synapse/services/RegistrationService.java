package kz.synapse.services;

import kz.synapse.database.Database;
import kz.synapse.exceptions.*;
import kz.synapse.models.*;

public class RegistrationService {

    public void register(Student student, Course course) {

        //проверка кредитов
        if (student.getCredits() + course.getCredits() > 21)
            throw new MaxCreditsException();

        //проверка пререквизитов
        for (Course prereq : course.getPrerequisites()) {
            if (!student.getPassedCourses().contains(prereq))
                throw new PrerequisiteNotMetException(
                        course.getCourseCode()
                );
        }

        //проверка лимита fails
        if (student.getCourseFails().getOrDefault(course, 0) >= 3)
            throw new CourseFailLimitException(course.getCourseCode());

        // 4. проверка мест
        if (!course.hasAvailableSpots())
            throw new IllegalStateException(
                    "Course " + course.getName() + " is full"
            );

        // проверка что регистрация открыта
        if (!Database.getInstance().isRegistrationOpen())
            throw new IllegalStateException(
                    "Registration is currently closed"
            );

        //проверка что студент ещё не записан
        if (student.getEnrolledCourses().contains(course))
            throw new IllegalStateException(
                    "Already registered for " + course.getName()
            );

        //все проверки пройдены — создаём заявку
        CourseRegistration request = new CourseRegistration(student, course);
        Database.getInstance().addPendingRegistration(request);
    }
}
package kz.synapse.factory;

import kz.synapse.enums.*;
import kz.synapse.models.*;
import kz.synapse.interfaces.Researcher;

public class UserFactory {

    // создать студента
    public static Student createStudent(String id, String name,
                                        String email, String password,
                                        Language language, School school) {
        return new Student(id, name, email, password, language, school);
    }

    // создать graduate student (всегда researcher)
    public static ResearcherDecorator createGraduateStudent(String id, String name,
                                                            String email, String password,
                                                            Language language, School school,
                                                            GraduateDegree degree) {
        GraduateStudent gs = new GraduateStudent(
                id, name, email, password, language, school, degree
        );
        return new ResearcherDecorator(gs);
    }

    // создать учителя
    public static Teacher createTeacher(String id, String name,
                                        String email, String password,
                                        Language language, double salary,
                                        TeacherPosition position, School school) {
        Teacher teacher = new Teacher(
                id, name, email, password, language, salary, position, school
        );
        return teacher;
    }

    // создать профессора (всегда researcher)
    public static ResearcherDecorator createProfessor(String id, String name,
                                                      String email, String password,
                                                      Language language, double salary,
                                                      School school) {
        Teacher professor = new Teacher(
                id, name, email, password, language,
                salary, TeacherPosition.PROFESSOR, school
        );
        return new ResearcherDecorator(professor);
    }

    // создать admin
    public static Admin createAdmin(String id, String name,
                                    String email, String password,
                                    Language language, double salary) {
        return new Admin(id, name, email, password, language, salary);
    }

    // создать school manager
    public static SchoolManager createSchoolManager(String id, String name,
                                                    String email, String password,
                                                    Language language, double salary,
                                                    School school) {
        return new SchoolManager(
                id, name, email, password, language, salary, school
        );
    }

    // создать OR manager
    public static ORManager createORManager(String id, String name,
                                            String email, String password,
                                            Language language, double salary) {
        return new ORManager(id, name, email, password, language, salary);
    }

    // создать dean
    public static Dean createDean(String id, String name,
                                  String email, String password,
                                  Language language, double salary,
                                  School school) {
        return new Dean(id, name, email, password, language, salary, school);
    }

    // создать tech support
    public static TechSupportSpecialist createTechSupport(String id, String name,
                                                          String email, String password,
                                                          Language language, double salary) {
        return new TechSupportSpecialist(
                id, name, email, password, language, salary
        );
    }
}
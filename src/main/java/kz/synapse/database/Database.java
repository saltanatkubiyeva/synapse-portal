package kz.synapse.database;

import kz.synapse.enums.SemesterType;
import kz.synapse.models.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable {

    // singleton
    private static Database instance;

    // users
    private List<User> users = new ArrayList<>();

    // academic
    private List<Course> courses = new ArrayList<>();
    private List<CourseRegistration> pendingRegistrations = new ArrayList<>();

    // research
    private List<ResearchPaper> papers = new ArrayList<>();
    private List<ResearchProject> projects = new ArrayList<>();

    // news & journals
    private List<News> newsList = new ArrayList<>();
    private List<Journal> journals = new ArrayList<>();

    // requests
    private List<Request> techRequests = new ArrayList<>();
    private List<EmployeeRequest> employeeRequests = new ArrayList<>();
    private List<Complaint> complaints = new ArrayList<>();

    // logs
    private List<String> systemLogs = new ArrayList<>();
    private List<String> userLogs = new ArrayList<>();

    // semester
    private SemesterType currentSemester;
    private boolean isRegistrationOpen = false;

    private static final String FILE_PATH = "database.ser";

    private Database() {}

    // getInstance
    public static synchronized Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    // save / load

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
            System.out.println("Database saved.");
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    public static void load() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_PATH))) {
            instance = (Database) ois.readObject();
            System.out.println("Database loaded.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load error, starting fresh: " + e.getMessage());
            instance = new Database();
        }
    }

    // users

    public void addUser(User user)    { users.add(user); }
    public void removeUser(User user) { users.remove(user); }
    public void updateUser(User user) {
        users.replaceAll(u -> u.getId().equals(user.getId()) ? user : u);
    }
    public List<User> getUsers()      { return users; }

    public User findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User u : users)
            if (u instanceof Student)
                students.add((Student) u);
        return students;
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (User u : users)
            if (u instanceof Teacher)
                teachers.add((Teacher) u);
        return teachers;
    }

    public Dean getDeanBySchool(kz.synapse.enums.School school) {
        for (User u : users)
            if (u instanceof Dean && ((Dean) u).getSchool() == school)
                return (Dean) u;
        return null;
    }

    // courses

    public void addAvailableCourse(Course c)    { courses.add(c); }
    public List<Course> getCourses()            { return courses; }

    public void addPendingRegistration(CourseRegistration r) {
        pendingRegistrations.add(r);
    }
    public List<CourseRegistration> getPendingRegistrations() {
        return pendingRegistrations;
    }
    public void removePendingRegistration(CourseRegistration r) {
        pendingRegistrations.remove(r);
    }

    // research

    public void addPaper(ResearchPaper p)      { papers.add(p); }
    public List<ResearchPaper> getPapers()     { return papers; }

    public void addProject(ResearchProject p)  { projects.add(p); }
    public List<ResearchProject> getProjects() { return projects; }

    // news & journals

    public void addNews(News news)         { newsList.add(news); }
    public List<News> getNewsList()        { return newsList; }

    public void addJournal(Journal j)      { journals.add(j); }
    public List<Journal> getJournals()     { return journals; }

    // requests

    public void addTechRequest(Request r)              { techRequests.add(r); }
    public List<Request> getTechRequests()             { return techRequests; }

    public void addEmployeeRequest(EmployeeRequest r)  { employeeRequests.add(r); }
    public List<EmployeeRequest> getEmployeeRequests() { return employeeRequests; }

    public void addComplaint(Complaint c)              { complaints.add(c); }
    public List<Complaint> getComplaints()             { return complaints; }

    // logs

    public void addLog(String log)      { systemLogs.add(log); }
    public void addUserLog(String log)  { userLogs.add(log); }
    public List<String> getSystemLogs() { return systemLogs; }
    public List<String> getUserLogs()   { return userLogs; }
    public List<String> getAllLogs() {
        List<String> all = new ArrayList<>();
        all.addAll(systemLogs);
        all.addAll(userLogs);
        return all;
    }

    // semester

    public SemesterType getCurrentSemester()       { return currentSemester; }
    public void setCurrentSemester(SemesterType s) { currentSemester = s; }
    public boolean isRegistrationOpen()            { return isRegistrationOpen; }
    public void setRegistrationOpen(boolean open)  { isRegistrationOpen = open; }
}
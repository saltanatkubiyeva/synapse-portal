package kz.synapse.database;

import kz.synapse.utils.LanguageManager;

import kz.synapse.enums.NewsType;
import kz.synapse.enums.SemesterType;
import kz.synapse.models.*;
import kz.synapse.services.AuthService;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Database instance;
    private static final String FILE_PATH = "database.ser";

    private List<User>                   users                = new ArrayList<>();
    private List<Course>                 courses              = new ArrayList<>();
    private List<CourseOffering>         courseOfferings      = new ArrayList<>();
    private List<CourseOffering>         publishedOfferings   = new ArrayList<>();
    private List<CourseRegistration>     pendingRegistrations = new ArrayList<>();
    private List<CourseRegistration>     approvedRegistrations= new ArrayList<>();

    private List<ResearchPaper>          papers      = new ArrayList<>();
    private List<ResearchProject>        projects    = new ArrayList<>();
    private List<ResearcherDecorator<?>> researchers = new ArrayList<>();

    private List<News>           newsList         = new ArrayList<>();
    private List<Journal>        journals         = new ArrayList<>();
    private List<Request>        techRequests     = new ArrayList<>();
    private List<EmployeeRequest>employeeRequests = new ArrayList<>();
    private List<Complaint>      complaints       = new ArrayList<>();

    private List<StudentOrganization>  organizations     = new ArrayList<>();
    private List<OrganizationProposal> orgProposals      = new ArrayList<>();

    private List<String> systemLogs = new ArrayList<>();
    private List<String> userLogs   = new ArrayList<>();

    private SemesterType currentSemester;
    private boolean isRegistrationOpen = false;
    private Schedule schedule = new Schedule();

    private Database() {}

    public static synchronized Database getInstance() {
        if (instance == null) load();
        return instance;
    }

    public synchronized void save() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
        }
    }

    public static synchronized void load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            instance = (Database) ois.readObject();
            System.out.println(LanguageManager.get("db.loaded"));
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(LanguageManager.get("db.startingFresh", e.getMessage()));
            instance = new Database();
        }
        AuthService.getInstance().invalidateSession();
    }

    public void addUser(User user)    { users.add(user); }
    public void removeUser(User user) { users.remove(user); }
    public void updateUser(User user) {
        users.replaceAll(u -> u.getId().equals(user.getId()) ? user : u);
    }
    public List<User> getUsers() { return Collections.unmodifiableList(users); }

    public User findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
    }
    public User findById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        for (User u : users) if (u instanceof Student) list.add((Student) u);
        return list;
    }
    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        for (User u : users) if (u instanceof Teacher) list.add((Teacher) u);
        return list;
    }
    public Dean getDeanBySchool(kz.synapse.enums.School school) {
        for (User u : users)
            if (u instanceof Dean && ((Dean) u).getSchool() == school) return (Dean) u;
        return null;
    }

    public void addCourse(Course c)   { courses.add(c); }
    public List<Course> getCourses()  { return Collections.unmodifiableList(courses); }

    public void addCourseOffering(CourseOffering o)   { courseOfferings.add(o); }
    public void publishOffering(CourseOffering o) {
        if (!publishedOfferings.contains(o)) publishedOfferings.add(o);
    }
    public List<CourseOffering> getCourseOfferings()  { return Collections.unmodifiableList(courseOfferings); }
    public List<CourseOffering> getPublishedOfferings(){ return Collections.unmodifiableList(publishedOfferings); }

    public void addPendingRegistration(CourseRegistration r)    { pendingRegistrations.add(r); }
    public void removePendingRegistration(CourseRegistration r) { pendingRegistrations.remove(r); }
    public List<CourseRegistration> getPendingRegistrations()   { return Collections.unmodifiableList(pendingRegistrations); }
    public void addApprovedRegistration(CourseRegistration r)   { approvedRegistrations.add(r); }
    public List<CourseRegistration> getApprovedRegistrations()  { return Collections.unmodifiableList(approvedRegistrations); }

    public void addPaper(ResearchPaper p)              { papers.add(p); }
    public List<ResearchPaper> getPapers()             { return Collections.unmodifiableList(papers); }
    public void addProject(ResearchProject p)          { projects.add(p); }
    public List<ResearchProject> getProjects()         { return Collections.unmodifiableList(projects); }
    public void addResearcher(ResearcherDecorator<?> r){ researchers.add(r); }
    public List<ResearcherDecorator<?>> getResearchers(){ return Collections.unmodifiableList(researchers); }
    public ResearcherDecorator<?> getResearcherFor(User user) {
        return researchers.stream()
                .filter(r -> r.getInnerUser().getId().equals(user.getId()))
                .findFirst().orElse(null);
    }

    public void addNews(News news) {
        if (news.getType() == NewsType.RESEARCH) news.setPinned(true);
        newsList.add(news);
    }
    public List<News> getNewsList()    { return Collections.unmodifiableList(newsList); }
    public void removeNews(News news)  { newsList.remove(news); }

    public void addJournal(Journal j)  { journals.add(j); }
    public List<Journal> getJournals() { return Collections.unmodifiableList(journals); }

    public void addTechRequest(Request r)             { techRequests.add(r); }
    public List<Request> getTechRequests()            { return Collections.unmodifiableList(techRequests); }
    public void addEmployeeRequest(EmployeeRequest r) { employeeRequests.add(r); }
    public List<EmployeeRequest> getEmployeeRequests(){ return Collections.unmodifiableList(employeeRequests); }
    public void addComplaint(Complaint c)             { complaints.add(c); }
    public List<Complaint> getComplaints()            { return Collections.unmodifiableList(complaints); }

    public void addLog(String log)     { systemLogs.add(log); }
    public void addUserLog(String log) { userLogs.add(log); }
    public List<String> getSystemLogs(){ return Collections.unmodifiableList(systemLogs); }
    public List<String> getUserLogs()  { return Collections.unmodifiableList(userLogs); }
    public List<String> getAllLogs() {
        List<String> all = new ArrayList<>(systemLogs);
        all.addAll(userLogs);
        return all;
    }

    public void addOrganization(StudentOrganization o)    { organizations.add(o); }
    public void removeOrganization(StudentOrganization o) {
        for (Student s : o.getMembers())
            s.leaveOrganizationInternal(o);
        organizations.remove(o);
    }
    public List<StudentOrganization> getOrganizations()   { return Collections.unmodifiableList(organizations); }

    public void addOrgProposal(OrganizationProposal p)    { orgProposals.add(p); }
    public List<OrganizationProposal> getOrgProposals()   { return Collections.unmodifiableList(orgProposals); }
    public void removeOrgProposal(OrganizationProposal p) { orgProposals.remove(p); }

    public Schedule getSchedule()                  { return schedule; }
    public SemesterType getCurrentSemester()       { return currentSemester; }
    public void setCurrentSemester(SemesterType s) { currentSemester = s; }
    public boolean isRegistrationOpen()            { return isRegistrationOpen; }
    public void setRegistrationOpen(boolean open)  { isRegistrationOpen = open; }
}

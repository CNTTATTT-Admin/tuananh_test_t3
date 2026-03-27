package com.checkplagiarism.plagiarism.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.PlagiarismThresholds;
import com.checkplagiarism.plagiarism.domain.Permission;
import com.checkplagiarism.plagiarism.domain.Role;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.repository.PermissionRepository;
import com.checkplagiarism.plagiarism.repository.PlagiarismThresholdRepository;
import com.checkplagiarism.plagiarism.repository.RoleRepository;
import com.checkplagiarism.plagiarism.repository.UserRepository;
import com.checkplagiarism.plagiarism.util.constants.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionrRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PlagiarismThresholdRepository plagiarismThresholdRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionrRepository, RoleRepository roleRepository,
            UserRepository userRepository, PlagiarismThresholdRepository plagiarismThresholdRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionrRepository = permissionrRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.plagiarismThresholdRepository = plagiarismThresholdRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");

        long countPermissions = this.permissionrRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        // 1. INITIALIZE PERMISSIONS (if none exist or refresh)
        // We initialize them to ensure we have a complete list for role assignment
        ArrayList<Permission> allPermsList = new ArrayList<>();

        // USER module
        allPermsList.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
        allPermsList.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
        allPermsList.add(new Permission("Update role lecturer user", "/api/v1/users/lecturer/{id}", "PUT", "USERS"));
        allPermsList.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
        allPermsList.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
        allPermsList.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

        // PERMISSION module
        allPermsList.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
        allPermsList.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
        allPermsList.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
        allPermsList.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
        allPermsList
                .add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

        // ROLE module
        allPermsList.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
        allPermsList.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
        allPermsList.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
        allPermsList.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
        allPermsList.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

        // CLASS ROOM module
        allPermsList.add(new Permission("Create a class room", "/api/v1/classes", "POST", "CLASS ROOM"));
        allPermsList.add(new Permission("Update a class room", "/api/v1/classes", "PUT", "CLASS ROOM"));
        allPermsList.add(new Permission("Delete a class room", "/api/v1/classes/{id}", "DELETE", "CLASS ROOM"));
        allPermsList.add(new Permission("Get a class room by id", "/api/v1/classes/{id}", "GET", "CLASS ROOM"));
        allPermsList.add(new Permission("Get classes with pagination", "/api/v1/classes", "GET", "CLASS ROOM"));
        allPermsList.add(new Permission("Add student to class room", "/api/v1/classes/add", "POST", "CLASS ROOM"));
        allPermsList.add(new Permission("join a class room", "/api/v1/classes/join", "POST", "CLASS ROOM"));
        allPermsList.add(
                new Permission("Approved student join class room", "/api/v1/classes/approved", "PUT", "CLASS ROOM"));
        allPermsList.add(
                new Permission("rejected student join class room", "/api/v1/classes/rejected", "PUT", "CLASS ROOM"));
        allPermsList.add(new Permission("leave a class room", "/api/v1/classes/leave", "PUT", "CLASS ROOM"));
        allPermsList.add(new Permission("Delete student in class", "/api/v1/classes/{classId}/remove/{userId}",
                "DELETE", "CLASS ROOM"));
        allPermsList.add(new Permission("Get student in class", "/api/v1/classes/user/{classId}", "GET", "CLASS ROOM"));

        // ASSIGNMENT module
        allPermsList.add(new Permission("Create a assignment", "/api/v1/assignments", "POST", "ASSIGNMENT"));
        allPermsList.add(new Permission("Update a assignment", "/api/v1/assignments", "PUT", "ASSIGNMENT"));
        allPermsList.add(new Permission("Delete a assignment", "/api/v1/assignments/{id}", "DELETE", "ASSIGNMENT"));
        allPermsList.add(new Permission("Get a assignment by id", "/api/v1/assignments/{id}", "GET", "ASSIGNMENT"));
        allPermsList.add(new Permission("Get assignments with pagination", "/api/v1/assignments", "GET", "ASSIGNMENT"));

        // SUBMISSION module
        allPermsList.add(new Permission("Create a submission", "/api/v1/submissions", "POST", "SUBMISSION"));
        allPermsList.add(new Permission("Get a submission by user id", "/api/v1/my-history", "GET", "SUBMISSION"));
        allPermsList.add(new Permission("Get all submission", "/api/v1/submissions", "GET", "SUBMISSION"));
        allPermsList.add(new Permission("Get all submission by assignment", "/api/v1/assignment/{assignmentId}", "GET",
                "SUBMISSION"));
        allPermsList.add(
                new Permission("Get a check by submission id", "/api/v1/checks/{submissionId}", "GET", "SUBMISSION"));

        // THRESHOLD module
        allPermsList.add(new Permission("Create a threshold", "/api/v1/thresholds", "POST", "THRESHOLD"));
        allPermsList.add(new Permission("Update a threshold", "/api/v1/thresholds", "PUT", "THRESHOLD"));
        allPermsList.add(new Permission("Update default threshold", "/api/v1/thresholds/default", "PUT", "THRESHOLD"));
        allPermsList.add(new Permission("Delete a threshold", "/api/v1/thresholds/{id}", "DELETE", "THRESHOLD"));
        allPermsList.add(new Permission("Get a threshold by id", "/api/v1/thresholds/{id}", "GET", "THRESHOLD"));
        allPermsList.add(new Permission("Get thresholds with pagination", "/api/v1/thresholds", "GET", "THRESHOLD"));
        allPermsList.add(
                new Permission("Get thresholds by class", "/api/v1/thresholds/class/{classId}", "GET", "THRESHOLD"));
        allPermsList.add(new Permission("Update all thresholds", "/api/v1/thresholds/all", "POST", "THRESHOLD"));

        // AUTH module
        allPermsList.add(new Permission("Get current account info", "/api/v1/auth/account", "GET", "AUTH"));
        allPermsList.add(new Permission("Logout user", "/api/v1/auth/logout", "POST", "AUTH"));

        // 1. INITIALIZE PERMISSIONS
        for (Permission p : allPermsList) {
            Permission existing = this.permissionrRepository.findByApiPathAndMethod(p.getApiPath(), p.getMethod());
            if (existing == null) {
                this.permissionrRepository.save(p);
            }
        }

        // 2. INITIALIZE ROLES
        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionrRepository.findAll();

            // ADMIN ROLE (Full Permissions)
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator with full access");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);
            this.roleRepository.save(adminRole);

            // STUDENT ROLE
            List<Permission> studentPermissions = allPermissions.stream()
                    .filter(p -> p.getModule().equals("AUTH") ||
                            p.getApiPath().contains("/join") ||
                            p.getApiPath().contains("/leave") ||
                            p.getApiPath().contains("/my-history") ||
                            (p.getApiPath().contains("/submissions") && p.getMethod().equals("POST")) ||
                            p.getApiPath().contains("/checks") ||
                            (p.getApiPath().contains("/classes") && p.getMethod().equals("GET")) ||
                            (p.getApiPath().contains("/assignments") && p.getMethod().equals("GET")))
                    .toList();

            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            studentRole.setDescription("Student with limited access to own submissions and classes");
            studentRole.setActive(true);
            studentRole.setPermissions(studentPermissions);
            this.roleRepository.save(studentRole);

            // LECTURER ROLE
            List<Permission> lecturerPermissions = allPermissions.stream()
                    .filter(p -> !p.getModule().equals("USERS") &&
                            !p.getModule().equals("PERMISSIONS") &&
                            !p.getModule().equals("ROLES"))
                    .toList();

            Role lecturerRole = new Role();
            lecturerRole.setName("LECTURER");
            lecturerRole.setDescription("Lecturer with access to manage classes, assignments and check submissions");
            lecturerRole.setActive(true);
            lecturerRole.setPermissions(lecturerPermissions);
            this.roleRepository.save(lecturerRole);
        } else {
            // ALWAYS REFRESH LECTURER AND STUDENT ROLES to catch new permissions added in
            // code
            List<Permission> allExisting = this.permissionrRepository.findAll();

            Role studentRole = this.roleRepository.findByName("STUDENT");
            if (studentRole != null) {
                studentRole.setPermissions(allExisting.stream()
                        .filter(p -> p.getModule().equals("AUTH") ||
                                p.getApiPath().contains("/join") ||
                                p.getApiPath().contains("/leave") ||
                                p.getApiPath().contains("/my-history") ||
                                (p.getApiPath().contains("/submissions") && p.getMethod().equals("POST")) ||
                                p.getApiPath().contains("/checks") ||
                                (p.getApiPath().contains("/classes") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().contains("/assignments") && p.getMethod().equals("GET")))
                        .toList());
                this.roleRepository.save(studentRole);
            }

            Role lecturerRole = this.roleRepository.findByName("LECTURER");
            if (lecturerRole != null) {
                lecturerRole.setPermissions(allExisting.stream()
                        .filter(p -> !p.getModule().equals("USERS") &&
                                !p.getModule().equals("PERMISSIONS") &&
                                !p.getModule().equals("ROLES"))
                        .toList());
                this.roleRepository.save(lecturerRole);
            }
        }

        // 3. INITIALIZE USERS
        if (countUsers == 0) {
            // ADMIN USER
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setAge(20);
            admin.setGender(GenderEnum.MALE);
            admin.setName("SUPER ADMIN");
            admin.setPassword(this.passwordEncoder.encode("123456"));
            Role adminRole = this.roleRepository.findByName("ADMIN");
            if (adminRole != null)
                admin.setRole(adminRole);
            this.userRepository.save(admin);

            // LECTURER USER
            User lecturer = new User();
            lecturer.setEmail("lecturer@gmail.com");
            lecturer.setAge(35);
            lecturer.setGender(GenderEnum.FEMALE);
            lecturer.setName("Dr. Lecturer");
            lecturer.setPassword(this.passwordEncoder.encode("123456"));
            Role lecturerRole = this.roleRepository.findByName("LECTURER");
            if (lecturerRole != null)
                lecturer.setRole(lecturerRole);
            this.userRepository.save(lecturer);

            // STUDENT USER
            User student = new User();
            student.setEmail("student@gmail.com");
            student.setAge(20);
            student.setGender(GenderEnum.MALE);
            student.setName("Basic Student");
            student.setPassword(this.passwordEncoder.encode("123456"));
            Role studentRole = this.roleRepository.findByName("STUDENT");
            if (studentRole != null)
                student.setRole(studentRole);
            this.userRepository.save(student);
        }

        // 4. THRESHOLDS
        long countThresholds = this.plagiarismThresholdRepository.count();
        if (countThresholds == 0) {
            List<PlagiarismThresholds> thresholds = new ArrayList<>();
            thresholds.add(new PlagiarismThresholds(null, "SAFE", 0, 20, "#22c55e", "Safe range", true, null));
            thresholds.add(new PlagiarismThresholds(null, "LOW", 21, 40, "#eab308", "Suspicious range", true, null));
            thresholds.add(new PlagiarismThresholds(null, "MEDIUM", 41, 60, "#f97316", "High risk range", true, null));
            thresholds.add(new PlagiarismThresholds(null, "HIGH", 61, 100, "#ef4444", "Severe range", true, null));
            this.plagiarismThresholdRepository.saveAll(thresholds);
        }

        System.out.println(">>> END INIT DATABASE");
    }
}
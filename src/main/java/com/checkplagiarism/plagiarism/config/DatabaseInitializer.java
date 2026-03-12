package com.checkplagiarism.plagiarism.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.checkplagiarism.plagiarism.domain.Permission;
import com.checkplagiarism.plagiarism.domain.Role;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.repository.PermissionRepository;
import com.checkplagiarism.plagiarism.repository.RoleRepository;
import com.checkplagiarism.plagiarism.repository.UserRepository;
import com.checkplagiarism.plagiarism.util.constants.GenderEnum;



@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionrRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionrRepository, RoleRepository roleRepository,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionrRepository = permissionrRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");

        long countPermissions = this.permissionrRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Update role lecturer user", "/api/v1/users/lecturer/{id}", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a class room", "/api/v1/classes", "POST", "CLASS ROOM"));
            arr.add(new Permission("Update a class room", "/api/v1/classes", "PUT", "CLASS ROOM"));
            arr.add(new Permission("Delete a class room", "/api/v1/classes/{id}", "DELETE", "CLASS ROOM"));
            arr.add(new Permission("Get a class room by id", "/api/v1/classes/{id}", "GET", "CLASS ROOM"));
            arr.add(new Permission("Get classes with pagination", "/api/v1/classes", "GET", "CLASS ROOM"));
            arr.add(new Permission("Add student to class room", "/api/v1/classes/add", "POST", "CLASS ROOM"));
            arr.add(new Permission("join a class room", "/api/v1/classes/join", "POST", "CLASS ROOM"));
            arr.add(new Permission("Approved student join class room", "/api/v1/classes/approved", "PUT", "CLASS ROOM"));
            arr.add(new Permission("rejected student join class room", "/api/v1/classes/rejected", "PUT", "CLASS ROOM"));
            arr.add(new Permission("leave a class room", "/api/v1/classes/leave", "PUT", "CLASS ROOM"));
            arr.add(new Permission("Delete student in class", "api/v1/classes/{classId}/remove/{userId}", "DELETE", "CLASS ROOM"));
            arr.add(new Permission("Get student in class", "api/v1/classes/user/{classId}", "GET", "CLASS ROOM"));

            arr.add(new Permission("Create a assignment", "/api/v1/assignments", "POST", "ASSIGNMENT"));
            arr.add(new Permission("Update a assignment", "/api/v1/assignments", "PUT", "ASSIGNMENT"));
            arr.add(new Permission("Delete a assignment", "/api/v1/assignments/{id}", "DELETE", "ASSIGNMENT"));
            arr.add(new Permission("Get a assignment by id", "/api/v1/assignments/{id}", "GET", "ASSIGNMENT"));
            arr.add(new Permission("Get assignments with pagination", "/api/v1/assignments", "GET", "ASSIGNMENT"));

            arr.add(new Permission("Create a submission", "/api/v1/submissions", "POST", "SUBMISSION"));
            arr.add(new Permission("Get a submission by user id", "/api/v1//my-history", "GET", "SUBMISSION"));
            arr.add(new Permission("Get all submission", "/api/v1/submissions", "GET", "SUBMISSION"));
            arr.add(new Permission("Get all submission by assignment", "/api/v1/assignment/{assignmentId}", "GET", "SUBMISSION"));
            
            arr.add(new Permission("Create a threshold", "/api/v1/thresholds", "POST", "THRESHOLD"));
            arr.add(new Permission("Update a threshold", "/api/v1/thresholds", "PUT", "THRESHOLD"));
            arr.add(new Permission("Update default threshold", "/api/v1/thresholds/default", "PUT", "THRESHOLD"));
            arr.add(new Permission("Delete a threshold", "/api/v1/thresholds/{id}", "DELETE", "THRESHOLD"));
            arr.add(new Permission("Get a threshold by id", "/api/v1/thresholds/{id}", "GET", "THRESHOLD"));
            arr.add(new Permission("Get thresholds with pagination", "/api/v1/thresholds", "GET", "THRESHOLD"));

            arr.add(new Permission("Get a check by submission id", "/api/v1/checks/{submissionId}", "GET", "SUBMISSION"));
            
            arr.add(new Permission("Get class by student", "/api/v1/classes/user", "GET", "CLASS"));
            arr.add(new Permission("Get class by lecturer", "/api/v1/classes/lecturer", "GET", "CLASS"));
            arr.add(new Permission("Get thresshold by user", "/api/v1/thresholds/class/{classId}", "GET", "THRESHOLD"));
            arr.add(new Permission("create submission ", "/api/v1/submissions", "POST", "SUBMISSION"));
            this.permissionrRepository.saveAll(arr);

            
           
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionrRepository.findAll();
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("admin is full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);

            List<Long> studentIds = List.of(23L, 26L, 28L, 34L, 35L,45L,46L,48L);
            List<Permission> studentPermissions = permissionrRepository.findByIdIn(studentIds);

            Role studentRole = new Role();
            studentRole.setName("STUDENT");
            studentRole.setDescription("this's student role");
            studentRole.setActive(true);
            studentRole.setPermissions(studentPermissions);

            roleRepository.save(studentRole);


            List<Long> lecturerIds = List.of(17L, 18L, 19L, 20L, 21L, 22L, 24L, 25L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L, 41L, 42L, 43L, 44L,45L,46L,47L,48L);
            List<Permission> lecturerPermissions = permissionrRepository.findByIdIn(lecturerIds);

            Role lecturerRole = new Role();
            lecturerRole.setName("LECTURER");
            lecturerRole.setDescription("this's lecturer role");
            lecturerRole.setActive(true);
            lecturerRole.setPermissions(lecturerPermissions);

            roleRepository.save(lecturerRole);
        }

        if (countUsers == 0) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setAge(20);
            admin.setGender(GenderEnum.MALE);
            admin.setName("SUPPER ADMIN");
            admin.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("ADMIN");
            if (adminRole != null) {
                admin.setRole(adminRole);
            }
            this.userRepository.save(admin);
        }
    }
}
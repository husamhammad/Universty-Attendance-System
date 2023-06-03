
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AttendanceSystemUI extends JFrame {

    // Set The DataBase Connection Properties
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Husam";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin";
    private String currentUserRole;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AttendanceSystemUI frame = new AttendanceSystemUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Login InterFace 
    public AttendanceSystemUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Attendance System");
        setSize(500, 200);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));
        getContentPane().add(loginPanel, BorderLayout.CENTER);

        JLabel lblUsername = new JLabel("  Username:");
        loginPanel.add(lblUsername);

        usernameField = new JTextField();
        loginPanel.add(usernameField);
        usernameField.setColumns(20);

        JLabel lblPassword = new JLabel("  Password:");
        loginPanel.add(lblPassword);

        passwordField = new JPasswordField();
        loginPanel.add(passwordField);
        passwordField.setColumns(20);

        JButton btnLogin = new JButton("Login");
        loginPanel.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    currentUserRole = getCurrentUserRole(username);
                    showDashboard();
                } else {
                    JOptionPane.showMessageDialog(AttendanceSystemUI.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private String getCurrentUserRole(String username) {
        String role = null;

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "SELECT role FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Retrieve the role from the result set
            if (resultSet.next()) {
                role = resultSet.getString("role");
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }

        return role;
    }

    // Function To Check Authentication For Login
    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Function To Show Dashboard
    private void showDashboard() {
        getContentPane().removeAll();
        setSize(957, 150);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel coursePanel = new JPanel();
        tabbedPane.addTab("Course Management", null, coursePanel, null);
        coursePanel.setLayout(new BorderLayout());

        JPanel courseListPanel = new JPanel();
        coursePanel.add(courseListPanel, BorderLayout.NORTH);

        JButton btnViewCourseList = new JButton("View Course List");
        courseListPanel.add(btnViewCourseList);

        btnViewCourseList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCourseList();
            }
        });

        JButton btnCreateCourse = new JButton("Create Course ");
        courseListPanel.add(btnCreateCourse);

        btnCreateCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createCourse();
            }
        });
        JButton btnEditCourse = new JButton("Edit Course");
        courseListPanel.add(btnEditCourse);

        btnEditCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editCourse();
            }
        });

        JButton btnAssignAssistantTeacherToCourse = new JButton("Assign Assistant Teacher To Course");
        courseListPanel.add(btnAssignAssistantTeacherToCourse);

        btnAssignAssistantTeacherToCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignAssistantTeacherToCourse();
            }
        });

        JPanel courseDetailsPanel = new JPanel();
        coursePanel.add(courseDetailsPanel, BorderLayout.CENTER);

        JButton btnViewCourseDetails = new JButton("View Course Details");
        courseDetailsPanel.add(btnViewCourseDetails);

        btnViewCourseDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCourseDetails();
            }
        });

        JButton btnRunQuery = new JButton("Run Test Qurey");
        courseListPanel.add(btnRunQuery);

        btnRunQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testQuery();
            }
        });

        JPanel userPanel = new JPanel();
        tabbedPane.addTab("User Management", null, userPanel, null);
        userPanel.setLayout(new BorderLayout());

        JPanel userListPanel = new JPanel();
        userPanel.add(userListPanel, BorderLayout.NORTH);

        JButton btnCreateUser = new JButton("Create User");
        userListPanel.add(btnCreateUser);

        btnCreateUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });

        JButton btnEditUser = new JButton("Edit User");
        userListPanel.add(btnEditUser);

        btnEditUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });

        JButton btnViewUserList = new JButton("View User List");
        userListPanel.add(btnViewUserList);

        btnViewUserList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewUserList();
            }
        });

        JButton btnDeleteUser = new JButton("Delete User ");
        userListPanel.add(btnDeleteUser);

        btnDeleteUser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        JPanel lecturePanel = new JPanel();
        tabbedPane.addTab("Lecture Management", null, lecturePanel, null);
        lecturePanel.setLayout(new BorderLayout());

        JPanel lectureListPanel = new JPanel();
        lecturePanel.add(lectureListPanel, BorderLayout.NORTH);

        JButton btnCreateLecture = new JButton("Create Lecture");
        lectureListPanel.add(btnCreateLecture);

        btnCreateLecture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createLecture();
            }
        });
        JButton btnEditLecture = new JButton("Edit Lecture");
        lectureListPanel.add(btnEditLecture);

        btnEditLecture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editLecture();
            }
        });

        JButton btnSearchLecture = new JButton("Search Lecture");
        lectureListPanel.add(btnSearchLecture);

        btnSearchLecture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchLecture();
            }
        });

        JButton btnViewLectureList = new JButton("View Lecture List");
        lectureListPanel.add(btnViewLectureList);

        btnViewLectureList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewLectureList();
            }
        });

        JButton btnViewLectureForCourse = new JButton("View Lecture for specific Course");
        lectureListPanel.add(btnViewLectureForCourse);

        btnViewLectureForCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onViewLecturesForCourse();
            }
        });

        JPanel lectureDetailsPanel = new JPanel();
        lecturePanel.add(lectureDetailsPanel, BorderLayout.CENTER);

        JButton btnViewLectureDetails = new JButton("View Lecture Details");
        lectureDetailsPanel.add(btnViewLectureDetails);

        btnViewLectureDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewLectureDetails();
            }
        });

        JPanel studentPanel = new JPanel();
        tabbedPane.addTab("Student Management", null, studentPanel, null);
        studentPanel.setLayout(new BorderLayout());

        JPanel studentListPanel = new JPanel();
        studentPanel.add(studentListPanel, BorderLayout.NORTH);

        JButton btnRegisterStudent = new JButton("Register Student");
        studentListPanel.add(btnRegisterStudent);

        btnRegisterStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerStudent();
            }
        });
        JButton btnEditStudent = new JButton("Edit Student");
        studentListPanel.add(btnEditStudent);

        btnEditStudent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editStudent();
            }
        });

        JButton btnViewStudentList = new JButton("View Student List");
        studentListPanel.add(btnViewStudentList);

        btnViewStudentList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewStudentList();
            }
        });

        JPanel studentDetailsPanel = new JPanel();
        studentPanel.add(studentDetailsPanel, BorderLayout.CENTER);

        JButton btnViewStudentDetails = new JButton("View Student Details");
        studentDetailsPanel.add(btnViewStudentDetails);

        btnViewStudentDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewStudentDetails();
            }
        });

        JPanel attendancePanel = new JPanel();
        tabbedPane.addTab("Attendance Tracking", null, attendancePanel, null);
        attendancePanel.setLayout(new BorderLayout());

        JPanel attendanceRecordPanel = new JPanel();
        attendancePanel.add(attendanceRecordPanel, BorderLayout.NORTH);

        JButton btnRecordAttendance = new JButton("Record Attendance");
        attendanceRecordPanel.add(btnRecordAttendance);

        btnRecordAttendance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recordAttendance();
            }
        });

        JPanel importAttendancePanel = new JPanel();
        attendancePanel.add(importAttendancePanel, BorderLayout.CENTER);

        JButton btnImportAttendance = new JButton("Import Attendance");
        importAttendancePanel.add(btnImportAttendance);

        btnImportAttendance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importAttendance();
            }
        });

        JPanel attendanceReportsPanel = new JPanel();
        tabbedPane.addTab("Attendance Reports", null, attendanceReportsPanel, null);
        attendanceReportsPanel.setLayout(new BorderLayout());

        JPanel ViewAttendanceReportsPanel = new JPanel();
        attendanceReportsPanel.add(ViewAttendanceReportsPanel, BorderLayout.NORTH);

        JButton btnViewAttendanceReports = new JButton("View Attendance Reports");
        ViewAttendanceReportsPanel.add(btnViewAttendanceReports);

        btnViewAttendanceReports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAttendanceReports();
            }
        });

        JButton btnViewAttendanceForStudentInCourse = new JButton("View Attendance For Student in Specific Course");
        ViewAttendanceReportsPanel.add(btnViewAttendanceForStudentInCourse);

        btnViewAttendanceForStudentInCourse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewAttendanceStudent();
            }
        });

        JPanel modifyAttendancePanel = new JPanel();
        attendanceReportsPanel.add(modifyAttendancePanel, BorderLayout.CENTER);

        JButton btnModifyAttendanceReport = new JButton("Modify Attendance Report");
        modifyAttendancePanel.add(btnModifyAttendanceReport);

        btnModifyAttendanceReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyAttendanceReport();
            }
        });

        JPanel withdrawalListPanel = new JPanel();
        tabbedPane.addTab("Withdrawal List", null, withdrawalListPanel, null);
        withdrawalListPanel.setLayout(new BorderLayout());

        JPanel generateWithdrawalPanel = new JPanel();
        withdrawalListPanel.add(generateWithdrawalPanel, BorderLayout.NORTH);

        JButton btnGenerateWithdrawalList = new JButton("Generate Withdrawal List");
        generateWithdrawalPanel.add(btnGenerateWithdrawalList);

        btnGenerateWithdrawalList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateWithdrawalList();
            }
        });

        JPanel exportWithdrawalPanel = new JPanel();
        withdrawalListPanel.add(exportWithdrawalPanel, BorderLayout.CENTER);

        JButton btnExportWithdrawalList = new JButton("Export Withdrawal List");
        exportWithdrawalPanel.add(btnExportWithdrawalList);

        btnExportWithdrawalList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportWithdrawalList();
            }
        });
    }

    // Function To View Course List
    private void viewCourseList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM courses";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder courseList = new StringBuilder();
            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String subject = resultSet.getString("subject");
                String book = resultSet.getString("book");
                String teacher = resultSet.getString("teacher");
                String assistant_teacher = resultSet.getString("assistant_teacher");
                String virtualMeetingPlace = resultSet.getString("virtual_meeting_place");

                courseList.append("Course ID: ").append(courseId).append("\n");
                courseList.append("Subject: ").append(subject).append("\n");
                courseList.append("Book: ").append(book).append("\n");
                courseList.append("Teacher: ").append(teacher).append("\n");
                courseList.append("Assistant Teacher: ").append(assistant_teacher).append("\n");
                courseList.append("Virtual Meeting Place: ").append(virtualMeetingPlace).append("\n");
                courseList.append("--------------------\n");
            }

            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(courseList.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Course List", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To View Course Details
    private void viewCourseDetails() {
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter Course ID:");
        if (courseIdInput == null) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM courses WHERE course_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(courseIdInput));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String subject = resultSet.getString("subject");
                String book = resultSet.getString("book");
                String teacher = resultSet.getString("teacher");
                String assistant_teacher = resultSet.getString("assistant_teacher");
                String virtualMeetingPlace = resultSet.getString("virtual_meeting_place");

                StringBuilder courseDetails = new StringBuilder();
                courseDetails.append("Course ID: ").append(courseId).append("\n");
                courseDetails.append("Subject: ").append(subject).append("\n");
                courseDetails.append("Book: ").append(book).append("\n");
                courseDetails.append("Teacher: ").append(teacher).append("\n");
                courseDetails.append("Assistant Teacher: ").append(assistant_teacher).append("\n");
                courseDetails.append("Virtual Meeting Place: ").append(virtualMeetingPlace).append("\n");

                JTextArea textArea = new JTextArea(10, 40);
                textArea.setText(courseDetails.toString());
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Course Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Course not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To Create A New User
    private void createUser() {
        // Check the role and restrict access for Teaching Assistant
        if (currentUserRole.equals("Teaching Assistant")) {
            // Display an access denied message
            JOptionPane.showMessageDialog(this, "Access Denied. You do not have sufficient privileges.", "Access Denied", JOptionPane.ERROR_MESSAGE);
        } else {
            // Prompt the user to enter the user details
            String username = JOptionPane.showInputDialog(this, "Enter the Username:");
            if (username == null) {
                // User clicked Cancel, return from the function
                return;
            }
            String password = JOptionPane.showInputDialog(this, "Enter the Password:");
            if (password == null) {
                // User clicked Cancel, return from the function
                return;
            }

            // Prompt the user to choose the role
            Object[] roleOptions = {"Teacher", "Teaching Assistant", "Admin"};
            int roleChoice = JOptionPane.showOptionDialog(this, "Select the role:", "Role", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, roleOptions, roleOptions[0]);
            if (roleChoice == JOptionPane.CLOSED_OPTION) {
                // User closed the dialog, return from the function
                return;
            }

            // Map the selected role choice to the corresponding role string
            String role;
            switch (roleChoice) {
                case 0:
                    role = "Teacher";
                    break;
                case 1:
                    role = "Teaching Assistant";
                    break;
                case 2:
                    role = "Admin";
                    break;
                default:
                    role = "";
            }

            // Insert the new user into the database
            try {
                // Establish a database connection
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Prepare the SQL statement
                String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, role);

                // Execute the SQL statement
                int rowsAffected = statement.executeUpdate();

                // Close the statement and connection
                statement.close();
                connection.close();

                if (rowsAffected > 0) {
                    // Show success message to the user
                    JOptionPane.showMessageDialog(this, "User created successfully!");
                } else {
                    // Show error message if no rows were affected
                    JOptionPane.showMessageDialog(this, "Failed to create user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to create user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Function To Edit An Existing User
    private void editUser() {
        // Prompt the user to enter the user ID to edit
        String userIdInput = JOptionPane.showInputDialog(this, "Enter the User ID to edit:");
        if (userIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int userId;
        try {
            userId = Integer.parseInt(userIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid User ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt the user to enter the updated user details
        String username = JOptionPane.showInputDialog(this, "Enter the Username:");
        if (username == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String password = JOptionPane.showInputDialog(this, "Enter the Password:");
        if (password == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String role = JOptionPane.showInputDialog(this, "Enter the Role:");
        if (role == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Update the user in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.setInt(4, userId);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to update user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To View User List
    private void viewUserList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder userList = new StringBuilder();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");

                userList.append("User ID: ").append(userId).append("\n");
                userList.append("Username: ").append(username).append("\n");
                userList.append("Role: ").append(role).append("\n");
                userList.append("--------------------\n");
            }

            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(userList.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "User List", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function to Delete User 
    private void deleteUser() {
        String userIdInput = JOptionPane.showInputDialog(this, "Enter User ID:");
        if (userIdInput == null) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(userIdInput));
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To Create A New Course
    private void createCourse() {
        // Prompt the user to enter the course details
        String subject = JOptionPane.showInputDialog(this, "Enter the Subject:");
        if (subject == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String book = JOptionPane.showInputDialog(this, "Enter the Book:");
        if (book == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String teacher = JOptionPane.showInputDialog(this, "Enter the Teacher:");
        if (teacher == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String virtualMeetingPlace = JOptionPane.showInputDialog(this, "Enter the Virtual Meeting Place:");
        if (virtualMeetingPlace == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Insert the new course into the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "INSERT INTO courses (subject, book, teacher, virtual_meeting_place) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, subject);
            statement.setString(2, book);
            statement.setString(3, teacher);
            statement.setString(4, virtualMeetingPlace);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Course created successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to create course!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to create course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Edit A Course
    private void editCourse() {
        // Prompt the user to enter the course ID
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter the Course ID:");
        if (courseIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int courseId;
        try {
            courseId = Integer.parseInt(courseIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt the user to enter the new course details
        String subject = JOptionPane.showInputDialog(this, "Enter the Subject:");
        if (subject == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String book = JOptionPane.showInputDialog(this, "Enter the Book:");
        if (book == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String teacher = JOptionPane.showInputDialog(this, "Enter the Teacher:");
        if (teacher == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String virtualMeetingPlace = JOptionPane.showInputDialog(this, "Enter the Virtual Meeting Place:");
        if (virtualMeetingPlace == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Update the course in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "UPDATE courses SET subject = ?, book = ?, teacher = ?, virtual_meeting_place = ? WHERE course_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, subject);
            statement.setString(2, book);
            statement.setString(3, teacher);
            statement.setString(4, virtualMeetingPlace);
            statement.setInt(5, courseId);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Course updated successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignAssistantTeacherToCourse() {
        // Prompt the system administrator to enter the assistant teacher's username and the course ID
        String assistantTeacherUsername = JOptionPane.showInputDialog(this, "Enter the Assistant Teacher's Username:");
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter the Course ID:");

        if (assistantTeacherUsername == null || courseIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Check if the assistant teacher exists
            String checkAssistantTeacherSql = "SELECT * FROM users WHERE username = ? AND role = 'Teaching Assistant'";
            PreparedStatement checkAssistantTeacherStatement = connection.prepareStatement(checkAssistantTeacherSql);
            checkAssistantTeacherStatement.setString(1, assistantTeacherUsername);
            ResultSet assistantTeacherResult = checkAssistantTeacherStatement.executeQuery();

            if (!assistantTeacherResult.next()) {
                JOptionPane.showMessageDialog(this, "Assistant Teacher not found or does not have the correct role!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Assign the assistant teacher to the course
            String assignAssistantTeacherSql = "UPDATE courses SET assistant_teacher = ? WHERE course_id = ?";
            PreparedStatement assignAssistantTeacherStatement = connection.prepareStatement(assignAssistantTeacherSql);
            assignAssistantTeacherStatement.setString(1, assistantTeacherUsername);
            assignAssistantTeacherStatement.setInt(2, courseId);

            // Execute the SQL statement
            int rowsAffected = assignAssistantTeacherStatement.executeUpdate();

            // Close the statements and connection
            assistantTeacherResult.close();
            checkAssistantTeacherStatement.close();
            assignAssistantTeacherStatement.close();
            connection.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Assistant Teacher assigned to the course successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign Assistant Teacher to the course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to assign Assistant Teacher to the course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Create A New Lecture
    private void createLecture() {
        // Prompt the user to enter the lecture details
        String courseIdString = JOptionPane.showInputDialog(this, "Enter the Course ID:");
        if (courseIdString == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int courseId = Integer.parseInt(courseIdString);

        String title = JOptionPane.showInputDialog(this, "Enter the Lecture Title:");
        if (title == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String address = JOptionPane.showInputDialog(this, "Enter the Address:");
        if (address == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String location = JOptionPane.showInputDialog(this, "Enter the Location:");
        if (location == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String virtualLectureHall = JOptionPane.showInputDialog(this, "Enter the Virtual Lecture Hall:");
        if (virtualLectureHall == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Insert the new lecture into the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "INSERT INTO lectures (course_id, title, address, location, virtual_lecture_hall) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, courseId);
            statement.setString(2, title);
            statement.setString(3, address);
            statement.setString(4, location);
            statement.setString(5, virtualLectureHall);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Lecture created successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to create lecture!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to create lecture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Edit An Existing Lecture
    private void editLecture() {
        // Prompt the user to enter the lecture ID
        String lectureIdString = JOptionPane.showInputDialog(this, "Enter the Lecture ID:");
        if (lectureIdString == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int lectureId = Integer.parseInt(lectureIdString);

        // Prompt the user to enter the updated lecture details
        String title = JOptionPane.showInputDialog(this, "Enter the Lecture Title:");
        if (title == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String address = JOptionPane.showInputDialog(this, "Enter the Address:");
        if (address == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String location = JOptionPane.showInputDialog(this, "Enter the Location:");
        if (location == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String virtualLectureHall = JOptionPane.showInputDialog(this, "Enter the Virtual Lecture Hall:");
        if (virtualLectureHall == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Update the lecture in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "UPDATE lectures SET title = ?, address = ?, location = ?, virtual_lecture_hall = ? WHERE lecture_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, address);
            statement.setString(3, location);
            statement.setString(4, virtualLectureHall);
            statement.setInt(5, lectureId);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Lecture updated successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to update lecture!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update lecture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Search For A Specific Lecture By It's Title
    private void searchLecture() {
        // Prompt the user to enter the lecture title
        String lectureTitle = JOptionPane.showInputDialog(this, "Enter the Lecture Title:");
        if (lectureTitle == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Search for the lecture in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "SELECT * FROM lectures WHERE title LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + lectureTitle + "%");

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Create a StringBuilder to store the search results
            StringBuilder searchResult = new StringBuilder();

            // Iterate over the result set and append the lecture details to the search result
            while (resultSet.next()) {
                int lectureId = resultSet.getInt("lecture_id");
                int courseId = resultSet.getInt("course_id");
                String title = resultSet.getString("title");
                String address = resultSet.getString("address");
                String location = resultSet.getString("location");
                String virtualLectureHall = resultSet.getString("virtual_lecture_hall");

                searchResult.append("Lecture ID: ").append(lectureId).append("\n");
                searchResult.append("Course ID: ").append(courseId).append("\n");
                searchResult.append("Title: ").append(title).append("\n");
                searchResult.append("Address: ").append(address).append("\n");
                searchResult.append("Location: ").append(location).append("\n");
                searchResult.append("Virtual Lecture Hall: ").append(virtualLectureHall).append("\n\n");
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();

            if (searchResult.length() > 0) {
                // Display the search result to the user
                JTextArea searchResultTextArea = new JTextArea(searchResult.toString());
                searchResultTextArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(searchResultTextArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Search Result", JOptionPane.PLAIN_MESSAGE);
            } else {
                // Show message if no lectures were found
                JOptionPane.showMessageDialog(this, "No lectures found with the given title.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to search for lecture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To View Lecture List
    private void viewLectureList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM lectures";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder lectureList = new StringBuilder();
            while (resultSet.next()) {
                int lectureId = resultSet.getInt("lecture_id");
                int courseId = resultSet.getInt("course_id");
                String title = resultSet.getString("title");
                String address = resultSet.getString("address");
                String location = resultSet.getString("location");
                String virtualLectureHall = resultSet.getString("virtual_lecture_hall");

                lectureList.append("Lecture ID: ").append(lectureId).append("\n");
                lectureList.append("Course ID: ").append(courseId).append("\n");
                lectureList.append("Title: ").append(title).append("\n");
                lectureList.append("Address: ").append(address).append("\n");
                lectureList.append("Location: ").append(location).append("\n");
                lectureList.append("Virtual Lecture Hall: ").append(virtualLectureHall).append("\n");
                lectureList.append("--------------------\n");
            }

            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(lectureList.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Lecture List", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onViewLecturesForCourse() {
        // Prompt the user to select a course or enter the course ID
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter the Course ID or select from the list:");

        if (courseIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "SELECT lecture_id, title, address, location FROM lectures WHERE course_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, courseId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Process the result set and display the lectures
            StringBuilder lecturesInfo = new StringBuilder();
            while (resultSet.next()) {
                int lectureId = resultSet.getInt("lecture_id");
                String title = resultSet.getString("title");
                String address = resultSet.getString("address");
                String location = resultSet.getString("location");

                // Append the lecture information to the StringBuilder
                lecturesInfo.append("Lecture ID: ").append(lectureId).append("\n");
                lecturesInfo.append("Title: ").append(title).append("\n");
                lecturesInfo.append("Address: ").append(address).append("\n");
                lecturesInfo.append("Location: ").append(location).append("\n\n");
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();

            // Display the retrieved lectures to the user
            JOptionPane.showMessageDialog(this, lecturesInfo.toString(), "Lectures for Course ID: " + courseId, JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve lectures: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To View Lecture Details
    private void viewLectureDetails() {
        String lectureIdInput = JOptionPane.showInputDialog(this, "Enter Lecture ID:");
        if (lectureIdInput == null) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM lectures WHERE lecture_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(lectureIdInput));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int lectureId = resultSet.getInt("lecture_id");
                int courseId = resultSet.getInt("course_id");
                String title = resultSet.getString("title");
                String address = resultSet.getString("address");
                String location = resultSet.getString("location");
                String virtualLectureHall = resultSet.getString("virtual_lecture_hall");

                StringBuilder lectureDetails = new StringBuilder();
                lectureDetails.append("Lecture ID: ").append(lectureId).append("\n");
                lectureDetails.append("Course ID: ").append(courseId).append("\n");
                lectureDetails.append("Title: ").append(title).append("\n");
                lectureDetails.append("Address: ").append(address).append("\n");
                lectureDetails.append("Location: ").append(location).append("\n");
                lectureDetails.append("Virtual Lecture Hall: ").append(virtualLectureHall).append("\n");

                JTextArea textArea = new JTextArea(10, 40);
                textArea.setText(lectureDetails.toString());
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Lecture Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lecture not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To Register A New Student
    private void registerStudent() {
        // Prompt the user to enter the student details
        String fullName = JOptionPane.showInputDialog(this, "Enter the Full Name:");
        if (fullName == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String universityNumber = JOptionPane.showInputDialog(this, "Enter the University Number:");
        if (universityNumber == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String mobileNumber = JOptionPane.showInputDialog(this, "Enter the Mobile Number:");
        if (mobileNumber == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String areaOfResidence = JOptionPane.showInputDialog(this, "Enter the Area of Residence:");
        if (areaOfResidence == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Insert the new student into the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "INSERT INTO students (full_name, university_number, mobile_number, area_of_residence) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, fullName);
            statement.setString(2, universityNumber);
            statement.setString(3, mobileNumber);
            statement.setString(4, areaOfResidence);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to register student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to register student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Edit An Existing Student
    private void editStudent() {
        // Prompt the user to enter the student ID
        String studentIdString = JOptionPane.showInputDialog(this, "Enter the Student ID:");
        if (studentIdString == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int studentId = Integer.parseInt(studentIdString);

        // Prompt the user to enter the updated student details
        String fullName = JOptionPane.showInputDialog(this, "Enter the Full Name:");
        if (fullName == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String universityNumber = JOptionPane.showInputDialog(this, "Enter the University Number:");
        if (universityNumber == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String mobileNumber = JOptionPane.showInputDialog(this, "Enter the Mobile Number:");
        if (mobileNumber == null) {
            // User clicked Cancel, return from the function
            return;
        }
        String areaOfResidence = JOptionPane.showInputDialog(this, "Enter the Area of Residence:");
        if (areaOfResidence == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Update the student in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "UPDATE students SET full_name = ?, university_number = ?, mobile_number = ?, area_of_residence = ? WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, fullName);
            statement.setString(2, universityNumber);
            statement.setString(3, mobileNumber);
            statement.setString(4, areaOfResidence);
            statement.setInt(5, studentId);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to update student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To View Student List
    private void viewStudentList() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM students";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder studentList = new StringBuilder();
            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String fullname = resultSet.getString("full_name");
                int university_number = resultSet.getInt("university_number");
                String area_of_residence = resultSet.getString("area_of_residence");

                studentList.append("Student ID: ").append(studentId).append("\n");
                studentList.append("Full Name: ").append(fullname).append("\n");
                studentList.append("University Number: ").append(university_number).append("\n");
                studentList.append("Address: ").append(area_of_residence).append("\n");
                studentList.append("--------------------\n");
            }

            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(studentList.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Student List", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To View Student List
    private void viewStudentDetails() {
        String studentIdInput = JOptionPane.showInputDialog(this, "Enter Student ID:");
        if (studentIdInput == null) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(studentIdInput));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String fullname = resultSet.getString("full_name");
                int University_Number = resultSet.getInt("university_number");
                String address = resultSet.getString("area_of_residence");

                StringBuilder studentDetails = new StringBuilder();
                studentDetails.append("Student ID: ").append(studentId).append("\n");
                studentDetails.append("Full Name: ").append(fullname).append("\n");
                studentDetails.append("University Number: ").append(University_Number).append("\n");
                studentDetails.append("Address: ").append(address).append("\n");

                JTextArea textArea = new JTextArea(10, 40);
                textArea.setText(studentDetails.toString());
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Student Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Student not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function To Record Attendance
    private void recordAttendance() {
        // Prompt the user to enter the university number
        String universityNumber = JOptionPane.showInputDialog(this, "Enter the University Number:");
        if (universityNumber == null) {
            // User clicked Cancel, return from the function
            return;
        }

        // Prompt the user to enter the lecture ID
        String lectureIdInput = JOptionPane.showInputDialog(this, "Enter the Lecture ID:");
        if (lectureIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int lectureId;
        try {
            lectureId = Integer.parseInt(lectureIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Lecture ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt the user to choose attendance status
        Object[] options = {"Present", "Absent"};
        int choice = JOptionPane.showOptionDialog(this, "Select Attendance Status:", "Attendance", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.CLOSED_OPTION) {
            // User closed the dialog, return from the function
            return;
        }
        boolean isPresent = (choice == 0);

        // Save the attendance record in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "INSERT INTO attendance (student_id, lecture_id, is_present) VALUES ((SELECT student_id FROM students WHERE university_number = ?), ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, universityNumber);
            statement.setInt(2, lectureId);
            statement.setBoolean(3, isPresent);

            // Execute the SQL statement
            statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            // Show success message to the user
            JOptionPane.showMessageDialog(this, "Attendance recorded successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to record attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Modify An Attendance Reports
    private void modifyAttendanceReport() {
        // Prompt the user to enter the attendance report ID
        String attendanceIdString = JOptionPane.showInputDialog(this, "Enter the Attendance Report ID:");
        if (attendanceIdString == null) {
            // User clicked Cancel, return from the function
            return;
        }
        int attendanceId = Integer.parseInt(attendanceIdString);

        // Prompt the user to select the new attendance status
        String[] options = {"Present", "Absent"};
        int choice = JOptionPane.showOptionDialog(this, "Select the new attendance status:", "Modify Attendance Report", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            // User closed the dialog, return from the function
            return;
        }

        boolean isPresent = (choice == 0);

        // Update the attendance report in the database
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "UPDATE attendance SET is_present = ? WHERE attendance_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBoolean(1, isPresent);
            statement.setInt(2, attendanceId);

            // Execute the SQL statement
            int rowsAffected = statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();

            if (rowsAffected > 0) {
                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Attendance report modified successfully!");
            } else {
                // Show error message if no rows were affected
                JOptionPane.showMessageDialog(this, "Failed to modify attendance report!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to modify attendance report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Make Import For Attendance Rebort From CSV File
    private void importAttendance() {
        // Create a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Attendance File");
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                // Create a BufferedReader to read the file
                BufferedReader reader = new BufferedReader(new FileReader(file));

                // Prepare the SQL statement
                String sql = "INSERT INTO attendance (student_id, lecture_id, is_present) VALUES (?, ?, ?)";
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = connection.prepareStatement(sql);

                // Read the CSV file line by line
                String line;
                boolean headerSkipped = false;

                while ((line = reader.readLine()) != null) {
                    // Skip the header line
                    if (!headerSkipped) {
                        headerSkipped = true;
                        continue;
                    }

                    // Split the line into fields using comma as the delimiter
                    String[] fields = line.split(",");

                    // Read the student ID, lecture ID, and attendance status from the fields
                    int studentId = Integer.parseInt(fields[0]);
                    int lectureId = Integer.parseInt(fields[1]);
                    boolean isPresent = Boolean.parseBoolean(fields[2]);

                    // Set the parameter values in the SQL statement
                    statement.setInt(1, studentId);
                    statement.setInt(2, lectureId);
                    statement.setBoolean(3, isPresent);

                    // Execute the SQL statement
                    statement.executeUpdate();
                }

                // Close the statement and reader
                statement.close();
                reader.close();

                // Show a success message
                JOptionPane.showMessageDialog(this, "Attendance imported successfully!");
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to import attendance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Function To View Attendance Reports
    public void viewAttendanceReports() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Prepare the SQL statement
            String sql = "SELECT lectures.lecture_id, lectures.title, COUNT(attendance.attendance_id) AS total_attendance, "
                    + "SUM(CASE WHEN attendance.is_present THEN 1 ELSE 0 END) AS total_present, "
                    + "COUNT(attendance.attendance_id) - SUM(CASE WHEN attendance.is_present THEN 1 ELSE 0 END) AS total_absent "
                    + "FROM lectures "
                    + "LEFT JOIN attendance ON lectures.lecture_id = attendance.lecture_id "
                    + "GROUP BY lectures.lecture_id, lectures.title";

            // Execute the SQL statement
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Create a table model to hold the data
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Lecture ID");
            tableModel.addColumn("Title");
            tableModel.addColumn("Total Attendance");
            tableModel.addColumn("Total Present");
            tableModel.addColumn("Total Absent");

            // Populate the table model with data from the result set
            while (resultSet.next()) {
                int lectureId = resultSet.getInt("lecture_id");
                String title = resultSet.getString("title");
                int totalAttendance = resultSet.getInt("total_attendance");
                int totalPresent = resultSet.getInt("total_present");
                int totalAbsent = resultSet.getInt("total_absent");

                Object[] rowData = {lectureId, title, totalAttendance, totalPresent, totalAbsent};
                tableModel.addRow(rowData);
            }

            // Create a JTable to display the attendance report
            JTable table = new JTable(tableModel);

            // Create a scroll pane and add the table to it
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a dialog to display the attendance report
            JDialog dialog = new JDialog();
            dialog.setTitle("Attendance Report");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(scrollPane);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to view attendance reports: " + e.getMessage());
        }
    }

    private void viewAttendanceStudent() {
        // Prompt the user to enter the student's university number and select a course
        String universityNumber = JOptionPane.showInputDialog(this, "Enter the Student's University Number:");
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter the Course ID or select from the list:");

        if (universityNumber == null || courseIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Course ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "SELECT a.is_present, l.title, l.address, l.location FROM attendance a "
                    + "JOIN lectures l ON a.lecture_id = l.lecture_id "
                    + "JOIN students s ON a.student_id = s.student_id "
                    + "WHERE s.university_number = ? AND l.course_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, universityNumber);
            statement.setInt(2, courseId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Process the result set and display the attendance report
            StringBuilder attendanceReport = new StringBuilder();
            while (resultSet.next()) {
                boolean isPresent = resultSet.getBoolean("is_present");
                String title = resultSet.getString("title");
                String address = resultSet.getString("address");
                String location = resultSet.getString("location");

                // Append the attendance information to the StringBuilder
                attendanceReport.append("Title: ").append(title).append("\n");
                attendanceReport.append("Address: ").append(address).append("\n");
                attendanceReport.append("Location: ").append(location).append("\n");
                attendanceReport.append("Attendance: ").append(isPresent ? "Present" : "Absent").append("\n\n");
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();

            // Display the attendance report to the user
            JOptionPane.showMessageDialog(this, attendanceReport.toString(), "Attendance Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve attendance report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Generate The Withdrawal List Of Students With Low Attendance
    private void generateWithdrawalList() {
        // Prompt the user to enter the course ID
        String courseIdInput = JOptionPane.showInputDialog(this, "Enter the Course ID:");
        if (courseIdInput == null) {
            // User clicked Cancel, return from the function
            return;
        }

        int courseId;
        try {
            // Parse the course ID from the user input
            courseId = Integer.parseInt(courseIdInput);
        } catch (NumberFormatException e) {
            // Invalid course ID, show error message and return from the function
            JOptionPane.showMessageDialog(this, "Invalid Course ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Query the database to get the withdrawal list
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the SQL statement
            String sql = "SELECT students.student_id, students.full_name, students.university_number "
                    + "FROM students "
                    + "JOIN student_courses ON students.student_id = student_courses.student_id "
                    + "LEFT JOIN ( "
                    + "  SELECT attendance.student_id, COUNT(*) AS total_lectures, SUM(CASE WHEN is_present THEN 1 ELSE 0 END) AS attended_lectures "
                    + "  FROM attendance "
                    + "  JOIN lectures ON attendance.lecture_id = lectures.lecture_id "
                    + "  WHERE lectures.course_id = ? "
                    + "  GROUP BY attendance.student_id "
                    + ") AS student_attendance ON students.student_id = student_attendance.student_id "
                    + "WHERE student_attendance.student_id IS NULL OR (student_attendance.attended_lectures * 100 / student_attendance.total_lectures) < 25";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, courseId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Create a StringBuilder to store the withdrawal list
            StringBuilder withdrawalList = new StringBuilder();

            // Iterate over the result set and append the student details to the withdrawal list
            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                String fullName = resultSet.getString("full_name");
                String universityNumber = resultSet.getString("university_number");

                withdrawalList.append("Student ID: ").append(studentId).append("\n");
                withdrawalList.append("Full Name: ").append(fullName).append("\n");
                withdrawalList.append("University Number: ").append(universityNumber).append("\n\n");
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();

            if (withdrawalList.length() > 0) {
                // Display the withdrawal list to the user
                JTextArea withdrawalListTextArea = new JTextArea(withdrawalList.toString());
                withdrawalListTextArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(withdrawalListTextArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Withdrawal List", JOptionPane.PLAIN_MESSAGE);
            } else {
                // Show message if no students need to be withdrawn
                JOptionPane.showMessageDialog(this, "No students need to be withdrawn from the course.", "Withdrawal List", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to generate withdrawal list: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Function To Export The Withdrawal List To A CSV File
    private void exportWithdrawalList() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a location to save the withdrawal list");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File directory = fileChooser.getSelectedFile();
                String filePath = directory.getAbsolutePath() + File.separator + "withdrawal_list.csv";
                PrintWriter writer = new PrintWriter(new FileWriter(filePath));

                // Establish a database connection
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // Prepare the SQL statement
                String sql = "SELECT students.student_id, students.full_name, students.university_number, students.mobile_number, students.area_of_residence "
                        + "FROM students "
                        + "LEFT JOIN student_courses ON students.student_id = student_courses.student_id "
                        + "LEFT JOIN lectures ON student_courses.course_id = lectures.course_id "
                        + "LEFT JOIN attendance ON students.student_id = attendance.student_id AND lectures.lecture_id = attendance.lecture_id "
                        + "GROUP BY students.student_id "
                        + "HAVING COUNT(lectures.lecture_id) > 0 AND SUM(CASE WHEN attendance.is_present THEN 1 ELSE 0 END) / COUNT(lectures.lecture_id) < 0.25";

                PreparedStatement statement = connection.prepareStatement(sql);

                // Execute the SQL statement
                ResultSet resultSet = statement.executeQuery();

                // Write the header to the CSV file
                writer.println("Student ID,Full Name,University Number,Mobile Number,Area of Residence");

                // Iterate over the result set and write each student's information to the CSV file
                while (resultSet.next()) {
                    int studentId = resultSet.getInt("student_id");
                    String fullName = resultSet.getString("full_name");
                    String universityNumber = resultSet.getString("university_number");
                    String mobileNumber = resultSet.getString("mobile_number");
                    String areaOfResidence = resultSet.getString("area_of_residence");

                    // Write the student's information to the CSV file
                    writer.println(studentId + "," + fullName + "," + universityNumber + "," + mobileNumber + "," + areaOfResidence);
                }

                // Close the result set, statement, and connection
                resultSet.close();
                statement.close();
                connection.close();

                // Close the writer
                writer.close();

                // Show success message to the user
                JOptionPane.showMessageDialog(this, "Withdrawal list exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to export withdrawal list: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Function That Write and Praper The Test Query
    private void testQuery() {
        try {
            // Create a Connection object
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Query 1: Display students with less than 25% lecture attendance
            String query1 = "SELECT full_name FROM students WHERE student_id IN (SELECT student_id FROM student_courses WHERE course_id IN (SELECT course_id FROM lectures) GROUP BY student_id HAVING COUNT(*) * 100 / (SELECT COUNT(*) FROM lectures) < 25)";
            executeAndDisplayResults(connection, query1, "1) Students with less than 25% lecture attendance:");

            // Query 2: Display the top 10 most attended lectures
            String query2 = "SELECT title FROM lectures ORDER BY lecture_id LIMIT 10";
            executeAndDisplayResults(connection, query2, "2) Top 10 most attended lectures:");

            // Query 3: Display lectures missed by students attending over 80% of all lectures
            String query3 = "SELECT title FROM lectures WHERE lecture_id IN (SELECT lecture_id FROM attendance GROUP BY lecture_id HAVING COUNT(*) > (SELECT COUNT(*) * 0.8 FROM students))";
            executeAndDisplayResults(connection, query3, "3) Lectures missed by students attending over 80% of all lectures:");

            // Query 4: Display students ordered by commitment level
            String query4 = "SELECT full_name FROM students ORDER BY full_name";
            executeAndDisplayResults(connection, query4, "4) Students ordered by commitment level:");

            // Query 5: Display lectures with more absences than attendance
            String query5 = "SELECT title FROM lectures WHERE lecture_id IN (SELECT lecture_id FROM attendance GROUP BY lecture_id HAVING SUM(CASE WHEN is_present THEN 0 ELSE 1 END) > SUM(CASE WHEN is_present THEN 1 ELSE 0 END))";
            executeAndDisplayResults(connection, query5, "5) Lectures with more absences than attendance:");

            // Query 6: Display students who missed 3 consecutive lectures
            String query6 = "SELECT full_name FROM students WHERE student_id IN (SELECT DISTINCT a1.student_id FROM attendance a1 INNER JOIN attendance a2 ON a1.student_id = a2.student_id AND a1.lecture_id = a2.lecture_id - 1 INNER JOIN attendance a3 ON a2.student_id = a3.student_id AND a2.lecture_id = a3.lecture_id - 1 WHERE NOT a1.is_present AND NOT a2.is_present AND NOT a3.is_present)";
            executeAndDisplayResults(connection, query6, "6) Students who missed 3 consecutive lectures:");

            // Close the Connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function That Run and Dispay The Test Query In Console
    private void executeAndDisplayResults(Connection connection, String query, String resultHeader) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            StringBuilder sb = new StringBuilder();
            sb.append(resultHeader).append("\n");
            while (resultSet.next()) {
                sb.append(resultSet.getString(1)).append("\n");
            }
            System.out.println("-----------------------------");
            System.out.println(sb.toString());
        }
    }

}

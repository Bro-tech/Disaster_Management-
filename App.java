import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


// Enums for better data modeling
enum DisasterType { FLOOD, EARTHQUAKE, FIRE, CYCLONE, OTHER }
enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
enum TeamSpecialty { MEDICAL, RESCUE, LOGISTICS, FIREFIGHTING, OTHER }

// Disaster class
class Disaster {
    private static int idCounter = 1;
    private int id;
    private DisasterType type;
    private String location;
    private Severity severity;
    private String date;
    private String description;

    public Disaster(DisasterType type, String location, Severity severity, String date, String description) {
        this.id = idCounter++;
        this.type = type;
        this.location = location;
        this.severity = severity;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public DisasterType getType() { return type; }
    public String getLocation() { return location; }
    public Severity getSeverity() { return severity; }
    public String getDate() { return date; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "Disaster ID: " + id + ", Type: " + type + ", Location: " + location +
               ", Severity: " + severity + ", Date: " + date + ", Description: " + description;
    }

    public String serialize() {
        return id + "," + type + "," + location + "," + severity + "," + date + "," + description.replace(",", ";");
    }

    public static Disaster deserialize(String line) {
        String[] parts = line.split(",", 6);
        if (parts.length < 6) return null;
        Disaster d = new Disaster(
            DisasterType.valueOf(parts[1]),
            parts[2],
            Severity.valueOf(parts[3]),
            parts[4],
            parts[5].replace(";", ",")
        );
        d.id = Integer.parseInt(parts[0]);
        if (d.id >= idCounter) idCounter = d.id + 1;
        return d;
    }
}

// Victim class
class Victim {
    private static int idCounter = 1;
    private int id;
    private String name;
    private int age;
    private String contact;
    private String injuryLevel;
    private int disasterId;

    public Victim(String name, int age, String contact, String injuryLevel, int disasterId) {
        this.id = idCounter++;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.injuryLevel = injuryLevel;
        this.disasterId = disasterId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getContact() { return contact; }
    public String getInjuryLevel() { return injuryLevel; }
    public int getDisasterId() { return disasterId; }

    @Override
    public String toString() {
        return "Victim ID: " + id + ", Name: " + name + ", Age: " + age + ", Contact: " + contact +
               ", Injury Level: " + injuryLevel + ", Disaster ID: " + disasterId;
    }

    public String serialize() {
        return id + "," + name + "," + age + "," + contact.replace(",", ";") + "," + injuryLevel + "," + disasterId;
    }

    public static Victim deserialize(String line) {
        String[] parts = line.split(",", 6);
        if (parts.length < 6) return null;
        Victim v = new Victim(
            parts[1],
            Integer.parseInt(parts[2]),
            parts[3].replace(";", ","),
            parts[4],
            Integer.parseInt(parts[5])
        );
        v.id = Integer.parseInt(parts[0]);
        if (v.id >= idCounter) idCounter = v.id + 1;
        return v;
    }
}

// RescueTeam class
class RescueTeam {
    private static int idCounter = 1;
    private int id;
    private String teamName;
    private TeamSpecialty specialty;
    private int assignedDisasterId;
    private String status;

    public RescueTeam(String teamName, TeamSpecialty specialty, int assignedDisasterId, String status) {
        this.id = idCounter++;
        this.teamName = teamName;
        this.specialty = specialty;
        this.assignedDisasterId = assignedDisasterId;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTeamName() { return teamName; }
    public TeamSpecialty getSpecialty() { return specialty; }
    public int getAssignedDisasterId() { return assignedDisasterId; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "Team ID: " + id + ", Team: " + teamName + ", Specialty: " + specialty +
               ", Assigned to Disaster ID: " + assignedDisasterId + ", Status: " + status;
    }

    public String serialize() {
        return id + "," + teamName + "," + specialty + "," + assignedDisasterId + "," + status.replace(",", ";");
    }

    public static RescueTeam deserialize(String line) {
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;
        RescueTeam t = new RescueTeam(
            parts[1],
            TeamSpecialty.valueOf(parts[2]),
            Integer.parseInt(parts[3]),
            parts[4].replace(";", ",")
        );
        t.id = Integer.parseInt(parts[0]);
        if (t.id >= idCounter) idCounter = t.id + 1;
        return t;
    }
}

// DataHandler class
class DataHandler {
    public static <T> void saveToFile(String filename, List<T> data, java.util.function.Function<T, String> serializer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (T obj : data) {
                writer.write(serializer.apply(obj));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static <T> List<T> loadFromFile(String filename, java.util.function.Function<String, T> deserializer) {
        List<T> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                T obj = deserializer.apply(line);
                if (obj != null) data.add(obj);
            }
        } catch (IOException e) {
            // File may not exist on first run
        }
        return data;
    }

    public static void saveLogs(String filename, List<String> logs) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving logs: " + e.getMessage());
        }
    }

    public static List<String> loadLogs(String filename) {
        List<String> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            // Ignore if logs don't exist yet
        }
        return logs;
    }
}

// User class for authentication
class User {
    private String username;
    private String password;
    private String role; // "admin" or "user"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public String serialize() {
        return username + "," + password + "," + role;
    }

    public static User deserialize(String line) {
        String[] parts = line.split(",", 3);
        if (parts.length < 3) return null;
        return new User(parts[0], parts[1], parts[2]);
    }
}

// Main class
public class App {
    static List<Disaster> disasters = new ArrayList<>();
    static List<Victim> victims = new ArrayList<>();
    static List<RescueTeam> teams = new ArrayList<>();
    static List<User> users = new ArrayList<>();
    static List<String> logs = new ArrayList<>();
    static User currentUser = null;
    static Scanner sc = new Scanner(System.in);

    // Undo/Redo stacks
    static Stack<Runnable> undoStack = new Stack<>();
    static Stack<Runnable> redoStack = new Stack<>();

    // Per-disaster timeline logs
    static Map<Integer, List<String>> disasterTimelines = new HashMap<>();

    public static void main(String[] args) {
        // Load data
        disasters = DataHandler.loadFromFile("disasters.txt", Disaster::deserialize);
        victims = DataHandler.loadFromFile("victims.txt", Victim::deserialize);
        teams = DataHandler.loadFromFile("teams.txt", RescueTeam::deserialize);
        users = DataHandler.loadFromFile("users.txt", User::deserialize);
        logs = DataHandler.loadLogs("logs.txt");

        if (users.isEmpty()) {
            System.out.println("No users found. Please register a new user.");
            registerUser();
        }
        if (!login()) {
            System.out.println("Too many failed attempts. Exiting.");
            return;
        }
        int choice;
        do {
            System.out.println("\n--- Disaster Management System ---");
            System.out.println("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
            System.out.println("1. Add Disaster");
            System.out.println("2. View All Disasters");
            System.out.println("3. Add Victim");
            System.out.println("4. Assign Rescue Team");
            System.out.println("5. View Report");
            System.out.println("6. Save Data");
            System.out.println("7. Load Data");
            System.out.println("8. Register New User");
            System.out.println("9. Edit/Delete Data");
            System.out.println("10. View Logs");
            System.out.println("11. Save Logs");
            System.out.println("12. Statistics");
            System.out.println("13. Search/Filter");
            System.out.println("14. Sort");
            System.out.println("15. Undo");
            System.out.println("16. Redo");
            System.out.println("17. Alerts");
            System.out.println("18. Change Password");
            System.out.println("19. Export Disasters CSV");
            System.out.println("20. View Disaster Timeline");
            System.out.println("21. Exit");
            System.out.print("Enter your choice: ");
            int menuChoice = getIntInput("");
            choice = menuChoice;

            switch (choice) {
                case 1 -> requireAdmin(App::addDisaster);
                case 2 -> viewDisasters();
                case 3 -> requireAdmin(App::addVictim);
                case 4 -> requireAdmin(App::assignRescueTeam);
                case 5 -> viewReport();
                case 6 -> requireAdmin(App::saveData);
                case 7 -> loadData();
                case 8 -> registerUser();
                case 9 -> requireAdmin(App::editOrDeleteMenu);
                case 10 -> viewLogs();
                case 11 -> saveLogs();
                case 12 -> showStatistics();
                case 13 -> searchMenu();
                case 14 -> sortMenu();
                case 15 -> undo();
                case 16 -> redo();
                case 17 -> showAlerts();
                case 18 -> changePassword();
                case 19 -> exportDisastersCSV();
                case 20 -> viewDisasterTimeline();
                case 21 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }

        } while (choice != 21);
    }

    // Registration method
    static void registerUser() {
        System.out.print("Enter new username: ");
        String uname = sc.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(uname)) {
                System.out.println("Username already exists. Registration failed.");
                return;
            }
        }
        System.out.print("Enter new password: ");
        String pwd = sc.nextLine();
        String role;
        while (true) {
            System.out.print("Enter role (admin/user): ");
            role = sc.nextLine().trim().toLowerCase();
            if (role.equals("admin") || role.equals("user")) break;
            System.out.println("Invalid role. Please enter 'admin' or 'user'.");
        }
        users.add(new User(uname, pwd, role));
        logs.add(logEntry("User registered: " + uname + " as " + role));
        DataHandler.saveToFile("users.txt", users, User::serialize);
        System.out.println("User registered successfully!");
    }

    // Login method
    static boolean login() {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String uname = sc.nextLine();
            System.out.print("Password: ");
            String pwd = sc.nextLine();
            for (User u : users) {
                if (u.getUsername().equals(uname) && u.getPassword().equals(pwd)) {
                    currentUser = u;
                    logs.add(logEntry("Login: " + uname));
                    System.out.println("Login successful!");
                    return true;
                }
            }
            System.out.println("Invalid credentials. Try again.");
            attempts++;
        }
        return false;
    }

    // Require admin role for certain actions
    static void requireAdmin(Runnable action) {
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            action.run();
        } else {
            System.out.println("Access denied. Admins only.");
        }
    }

    // Input validation for integer
    static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    // Add Disaster with Undo
    static void addDisaster() {
        System.out.println("Available types: " + Arrays.toString(DisasterType.values()));
        System.out.print("Enter Type: ");
        DisasterType type = DisasterType.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Enter Location: ");
        String location = sc.nextLine();
        System.out.println("Available severity: " + Arrays.toString(Severity.values()));
        System.out.print("Enter Severity: ");
        Severity severity = Severity.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Enter Date (dd-MM-yyyy): ");
        String date = sc.nextLine();
        System.out.print("Enter Description: ");
        String description = sc.nextLine();

        Disaster d = new Disaster(type, location, severity, date, description);
        disasters.add(d);
        logs.add(logEntry("Disaster added: ID " + d.getId() + " by " + currentUser.getUsername()));
        logDisasterAction(d.getId(), "Disaster created");
        undoStack.push(() -> {
            disasters.remove(d);
            logs.add(logEntry("Undo: Disaster removed ID " + d.getId()));
        });
        redoStack.clear();
        System.out.println("Disaster added. ID: " + d.getId());
    }

    static void viewDisasters() {
        if (disasters.isEmpty()) {
            System.out.println("No disasters recorded.");
        } else {
            disasters.forEach(System.out::println);
        }
    }

    static void addVictim() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        int age = getIntInput("Enter Age: ");
        System.out.print("Enter Contact Info: ");
        String contact = sc.nextLine();
        System.out.print("Enter Injury Level: ");
        String injury = sc.nextLine();
        int did = getIntInput("Enter Disaster ID: ");

        Disaster disaster = findDisasterById(did);
        if (disaster == null) {
            System.out.println("Disaster ID not found. Victim not added.");
            return;
        }

        Victim v = new Victim(name, age, contact, injury, did);
        victims.add(v);
        logs.add(logEntry("Victim added: " + name + " to Disaster ID " + did + " by " + currentUser.getUsername()));
        logDisasterAction(did, "Victim added: " + name);
        undoStack.push(() -> {
            victims.remove(v);
            logs.add(logEntry("Undo: Victim removed ID " + v.getId()));
        });
        redoStack.clear();
        System.out.println("Victim added. ID: " + v.getId());
    }

    static void assignRescueTeam() {
        System.out.print("Enter Team Name: ");
        String name = sc.nextLine();
        System.out.println("Available specialties: " + Arrays.toString(TeamSpecialty.values()));
        System.out.print("Enter Specialty: ");
        TeamSpecialty spec = TeamSpecialty.valueOf(sc.nextLine().trim().toUpperCase());
        int did = getIntInput("Enter Assigned Disaster ID: ");
        System.out.print("Enter Team Status: ");
        String status = sc.nextLine();

        Disaster disaster = findDisasterById(did);
        if (disaster == null) {
            System.out.println("Disaster ID not found. Team not assigned.");
            return;
        }

        RescueTeam t = new RescueTeam(name, spec, did, status);
        teams.add(t);
        logs.add(logEntry("Team assigned: " + name + " to Disaster ID " + did + " by " + currentUser.getUsername()));
        logDisasterAction(did, "Team assigned: " + name);
        undoStack.push(() -> {
            teams.remove(t);
            logs.add(logEntry("Undo: Team removed ID " + t.getId()));
        });
        redoStack.clear();
        System.out.println("Team assigned. ID: " + t.getId());
    }

    static void viewReport() {
        System.out.println("\n--- Disaster Report ---");
        for (Disaster d : disasters) {
            System.out.println(d);
            System.out.println("Victims:");
            for (Victim v : victims) {
                if (v.getDisasterId() == d.getId())
                    System.out.println("  " + v);
            }
            System.out.println("Teams:");
            for (RescueTeam t : teams) {
                if (t.getAssignedDisasterId() == d.getId())
                    System.out.println("  " + t);
            }
            System.out.println("--------------------");
        }
    }

    static void saveData() {
        DataHandler.saveToFile("disasters.txt", disasters, Disaster::serialize);
        DataHandler.saveToFile("victims.txt", victims, Victim::serialize);
        DataHandler.saveToFile("teams.txt", teams, RescueTeam::serialize);
        DataHandler.saveToFile("users.txt", users, User::serialize);
        logs.add(logEntry("Data saved by " + currentUser.getUsername()));
        System.out.println("Data saved.");
    }

    static void loadData() {
        disasters = DataHandler.loadFromFile("disasters.txt", Disaster::deserialize);
        victims = DataHandler.loadFromFile("victims.txt", Victim::deserialize);
        teams = DataHandler.loadFromFile("teams.txt", RescueTeam::deserialize);
        users = DataHandler.loadFromFile("users.txt", User::deserialize);
        logs = DataHandler.loadLogs("logs.txt");
        logs.add(logEntry("Data loaded by " + currentUser.getUsername()));
        System.out.println("Data loaded.");
    }

    static void viewLogs() {
        System.out.println("\n--- Disaster History/Logs ---");
        if (logs.isEmpty()) {
            System.out.println("No logs recorded.");
        } else {
            logs.forEach(System.out::println);
        }
    }

    static void saveLogs() {
        DataHandler.saveLogs("logs.txt", logs);
        System.out.println("Logs saved to logs.txt");
    }

    // Edit/Delete Menu
    static void editOrDeleteMenu() {
        System.out.println("\n--- Edit/Delete Menu ---");
        System.out.println("1. Edit Disaster");
        System.out.println("2. Delete Disaster");
        System.out.println("3. Edit Victim");
        System.out.println("4. Delete Victim");
        System.out.println("5. Edit Rescue Team");
        System.out.println("6. Delete Rescue Team");
        int ch = getIntInput("Enter your choice: ");
        switch (ch) {
            case 1 -> editDisaster();
            case 2 -> deleteDisaster();
            case 3 -> editVictim();
            case 4 -> deleteVictim();
            case 5 -> editRescueTeam();
            case 6 -> deleteRescueTeam();
            default -> System.out.println("Invalid choice!");
        }
    }

    // Edit/Delete Disaster
    static void editDisaster() {
        int id = getIntInput("Enter Disaster ID to edit: ");
        Disaster d = findDisasterById(id);
        if (d == null) {
            System.out.println("Disaster not found.");
            return;
        }
        System.out.print("Enter new location (leave blank to keep): ");
        String loc = sc.nextLine();
        System.out.print("Enter new description (leave blank to keep): ");
        String desc = sc.nextLine();
        Disaster newD = new Disaster(
            d.getType(),
            loc.isEmpty() ? d.getLocation() : loc,
            d.getSeverity(),
            d.getDate(),
            desc.isEmpty() ? d.getDescription() : desc
        );
        disasters.removeIf(x -> x.getId() == id);
        disasters.add(newD);
        logs.add(logEntry("Disaster edited: ID " + id + " by " + currentUser.getUsername()));
        logDisasterAction(id, "Disaster edited");
        System.out.println("Disaster updated.");
    }

    static void deleteDisaster() {
        int id = getIntInput("Enter Disaster ID to delete: ");
        boolean removed = disasters.removeIf(d -> d.getId() == id);
        if (removed) {
            logs.add(logEntry("Disaster deleted: ID " + id + " by " + currentUser.getUsername()));
            logDisasterAction(id, "Disaster deleted");
            System.out.println("Disaster deleted.");
        } else {
            System.out.println("Disaster not found.");
        }
    }

    // Edit/Delete Victim
    static void editVictim() {
        int id = getIntInput("Enter Victim ID to edit: ");
        Victim v = findVictimById(id);
        if (v == null) {
            System.out.println("Victim not found.");
            return;
        }
        System.out.print("Enter new contact info (leave blank to keep): ");
        String contact = sc.nextLine();
        Victim newV = new Victim(
            v.getName(),
            v.getAge(),
            contact.isEmpty() ? v.getContact() : contact,
            v.getInjuryLevel(),
            v.getDisasterId()
        );
        victims.removeIf(x -> x.getId() == id);
        victims.add(newV);
        logs.add(logEntry("Victim edited: ID " + id + " by " + currentUser.getUsername()));
        logDisasterAction(v.getDisasterId(), "Victim edited: " + v.getName());
        System.out.println("Victim updated.");
    }

    static void deleteVictim() {
        int id = getIntInput("Enter Victim ID to delete: ");
        boolean removed = victims.removeIf(v -> v.getId() == id);
        if (removed) {
            logs.add(logEntry("Victim deleted: ID " + id + " by " + currentUser.getUsername()));
            System.out.println("Victim deleted.");
        } else {
            System.out.println("Victim not found.");
        }
    }

    // Edit/Delete Rescue Team
    static void editRescueTeam() {
        int id = getIntInput("Enter Team ID to edit: ");
        RescueTeam t = findTeamById(id);
        if (t == null) {
            System.out.println("Team not found.");
            return;
        }
        System.out.print("Enter new status (leave blank to keep): ");
        String status = sc.nextLine();
        RescueTeam newT = new RescueTeam(
            t.getTeamName(),
            t.getSpecialty(),
            t.getAssignedDisasterId(),
            status.isEmpty() ? t.getStatus() : status
        );
        teams.removeIf(x -> x.getId() == id);
        teams.add(newT);
        logs.add(logEntry("Team edited: ID " + id + " by " + currentUser.getUsername()));
        logDisasterAction(t.getAssignedDisasterId(), "Team edited: " + t.getTeamName());
        System.out.println("Team updated.");
    }

    static void deleteRescueTeam() {
        int id = getIntInput("Enter Team ID to delete: ");
        boolean removed = teams.removeIf(t -> t.getId() == id);
        if (removed) {
            logs.add(logEntry("Team deleted: ID " + id + " by " + currentUser.getUsername()));
            System.out.println("Team deleted.");
        } else {
            System.out.println("Team not found.");
        }
    }

    // Statistics and Analytics
    static void showStatistics() {
        System.out.println("\n--- Statistics ---");
        System.out.println("Total Disasters: " + disasters.size());
        System.out.println("Total Victims: " + victims.size());
        System.out.println("Total Rescue Teams: " + teams.size());

        Map<DisasterType, Long> disasterTypeCount = new HashMap<>();
        for (Disaster d : disasters) {
            disasterTypeCount.put(d.getType(), disasterTypeCount.getOrDefault(d.getType(), 0L) + 1);
        }
        System.out.println("Disasters by Type: " + disasterTypeCount);

        Map<Integer, Long> victimsPerDisaster = new HashMap<>();
        for (Victim v : victims) {
            victimsPerDisaster.put(v.getDisasterId(), victimsPerDisaster.getOrDefault(v.getDisasterId(), 0L) + 1);
        }
        System.out.println("Victims per Disaster: " + victimsPerDisaster);

        Map<Integer, Long> teamsPerDisaster = new HashMap<>();
        for (RescueTeam t : teams) {
            teamsPerDisaster.put(t.getAssignedDisasterId(), teamsPerDisaster.getOrDefault(t.getAssignedDisasterId(), 0L) + 1);
        }
        System.out.println("Teams per Disaster: " + teamsPerDisaster);
    }

    // 1. Advanced Search and Filtering
    static void searchMenu() {
        System.out.println("\n--- Search Menu ---");
        System.out.println("1. Search Disasters by Type");
        System.out.println("2. Search Disasters by Location");
        System.out.println("3. Filter Victims by Injury Level");
        System.out.println("4. Filter Teams by Specialty");
        int ch = getIntInput("Enter your choice: ");
        switch (ch) {
            case 1 -> {
                System.out.print("Enter type: ");
                String type = sc.nextLine().toUpperCase();
                disasters.stream().filter(d -> d.getType().toString().equals(type)).forEach(System.out::println);
            }
            case 2 -> {
                System.out.print("Enter location: ");
                String loc = sc.nextLine();
                disasters.stream().filter(d -> d.getLocation().equalsIgnoreCase(loc)).forEach(System.out::println);
            }
            case 3 -> {
                System.out.print("Enter injury level: ");
                String level = sc.nextLine();
                victims.stream().filter(v -> v.getInjuryLevel().equalsIgnoreCase(level)).forEach(System.out::println);
            }
            case 4 -> {
                System.out.print("Enter specialty: ");
                String spec = sc.nextLine().toUpperCase();
                teams.stream().filter(t -> t.getSpecialty().toString().equals(spec)).forEach(System.out::println);
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    // 2. Sorting
    static void sortMenu() {
        System.out.println("\n--- Sort Menu ---");
        System.out.println("1. Sort Disasters by Date");
        System.out.println("2. Sort Victims by Age");
        int ch = getIntInput("Enter your choice: ");
        switch (ch) {
            case 1 -> disasters.stream()
                .sorted(Comparator.comparing(Disaster::getDate))
                .forEach(System.out::println);
            case 2 -> victims.stream()
                .sorted(Comparator.comparingInt(Victim::getAge))
                .forEach(System.out::println);
            default -> System.out.println("Invalid choice!");
        }
    }

    // 3. Undo/Redo
    static void undo() {
        if (!undoStack.isEmpty()) {
            Runnable action = undoStack.pop();
            action.run();
            redoStack.push(action);
            System.out.println("Undo performed.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }
    static void redo() {
        if (!redoStack.isEmpty()) {
            Runnable action = redoStack.pop();
            action.run();
            undoStack.push(action);
            System.out.println("Redo performed.");
        } else {
            System.out.println("Nothing to redo.");
        }
    }

    // 4. Notifications/Alerts
    static void showAlerts() {
        disasters.stream()
            .filter(d -> teams.stream().noneMatch(t -> t.getAssignedDisasterId() == d.getId()))
            .forEach(d -> System.out.println("Alert: No team assigned for Disaster ID " + d.getId()));
        victims.stream()
            .filter(v -> disasters.stream().noneMatch(d -> d.getId() == v.getDisasterId()))
            .forEach(v -> System.out.println("Alert: Victim " + v.getName() + " not assigned to any disaster!"));
    }

    // 6. Password Change
    static void changePassword() {
        System.out.print("Enter current password: ");
        String oldPwd = sc.nextLine();
        if (!currentUser.getPassword().equals(oldPwd)) {
            System.out.println("Incorrect password.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPwd = sc.nextLine();
        for (User u : users) {
            if (u.getUsername().equals(currentUser.getUsername())) {
                users.remove(u);
                users.add(new User(u.getUsername(), newPwd, u.getRole()));
                break;
            }
        }
        logs.add(logEntry("Password changed for user: " + currentUser.getUsername()));
        System.out.println("Password changed successfully.");
    }

    // 7. Disaster Timeline (per-disaster logs)
    static void logDisasterAction(int disasterId, String action) {
        disasterTimelines.putIfAbsent(disasterId, new ArrayList<>());
        disasterTimelines.get(disasterId).add(logEntry(action));
    }
    static void viewDisasterTimeline() {
        int id = getIntInput("Enter Disaster ID: ");
        List<String> timeline = disasterTimelines.get(id);
        if (timeline == null || timeline.isEmpty()) {
            System.out.println("No timeline for this disaster.");
        } else {
            timeline.forEach(System.out::println);
        }
    }

    // 8. Data Export (CSV)
    static void exportDisastersCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("disasters_export.csv"))) {
            pw.println("ID,Type,Location,Severity,Date,Description");
            for (Disaster d : disasters) {
                pw.printf("%d,%s,%s,%s,%s,%s%n", d.getId(), d.getType(), d.getLocation(), d.getSeverity(), d.getDate(), d.getDescription().replace(",", ";"));
            }
            System.out.println("Disasters exported to disasters_export.csv");
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    // Utility methods
    static Disaster findDisasterById(int id) {
        for (Disaster d : disasters) if (d.getId() == id) return d;
        return null;
    }
    static Victim findVictimById(int id) {
        for (Victim v : victims) if (v.getId() == id) return v;
        return null;
    }
    static RescueTeam findTeamById(int id) {
        for (RescueTeam t : teams) if (t.getId() == id) return t;
        return null;
    }
    static String logEntry(String action) {
        return "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + action + " (User: " + (currentUser != null ? currentUser.getUsername() : "system") + ")";
    }
}


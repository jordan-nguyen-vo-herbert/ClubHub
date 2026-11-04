package com.clubhub.entities;
// import com.clubhub.entities.Role;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;



@Entity
public class Club {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long clubID;
    private String clubName;
    // EnumType.STRING stores the name ("BUSINESS"), not the position, so reordering the enum is safe
    @Enumerated(EnumType.STRING)
    private AreaOfInterest areaOfInterest;
    @Column(length = 1000) // default is 255 characters, too short for a description
    private String description;
    // When and where the club meets, e.g. "Tuesdays 6 PM, Sloan 175", optional (FR-002.6)
    private String meetingInfo;
    // The club's faculty advisor, optional (FR-002.6)
    private String facultyAdvisor;
    // The email students use to reach the club, required (FR-002.6)
    private String contactEmail;
    // When the registration was submitted, set once when the Club is created (FR-002.6)
    private Instant submittedAt;
    // The student who applied to start the club
    @ManyToOne
    @JoinColumn(name = "founder_id")
    private Student founder;
    // Every club starts as an application, PENDING until approved
    @Enumerated(EnumType.STRING)
    private ClubStatus status = ClubStatus.PENDING;
    private Date dateApproved; // null until approved
    // mappedBy: the Member side owns the link (Member.club), cascade saves new members when the club is saved
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();
    // private Map<ClubRole, List<Member>> roleToMember;
    // Potential Fields
        // officers
        // private List<Events>;

    //
    protected Club() {}

    // No id parameter, the database generates it, members are added with addMember
    // New clubs are applications: status PENDING, no dateApproved yet
    public Club(String clubName, AreaOfInterest areaOfInterest, String description, Student founder) {
        this(clubName, areaOfInterest, description, null, null, null, founder);
    }

    /**
     * Creates a club registration with every field from the registration form (FR-002.6).
     * The submission time is recorded here, so every new registration has one.
     *
     * @param clubName       the club's name
     * @param areaOfInterest what the club is about
     * @param description    what the club does
     * @param meetingInfo    when and where the club meets, may be null
     * @param facultyAdvisor the club's faculty advisor, may be null
     * @param contactEmail   the email students use to reach the club
     * @param founder        the signed-in student who registered the club
     */
    public Club(String clubName, AreaOfInterest areaOfInterest, String description, String meetingInfo,
                String facultyAdvisor, String contactEmail, Student founder) {
        this.clubName = clubName;
        this.areaOfInterest = areaOfInterest;
        this.description = description;
        this.meetingInfo = meetingInfo;
        this.facultyAdvisor = facultyAdvisor;
        this.contactEmail = contactEmail;
        this.founder = founder;
        this.submittedAt = Instant.now();
    }

    // Creates the membership, saved to the database when the club is saved
    public void addMember(Student student, ClubRole role) {
        members.add(new Member(student, this, role));
    }

    // Approves the application and records when
    public void approve() {
        this.status = ClubStatus.APPROVED;
        this.dateApproved = new Date();
    }

    public Long getClubID() {
        return this.clubID;
    }

    public String getClubName() {
        return this.clubName;
    }

    public AreaOfInterest getAreaOfInterest() {
        return this.areaOfInterest;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMeetingInfo() {
        return this.meetingInfo;
    }

    public String getFacultyAdvisor() {
        return this.facultyAdvisor;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    // Returns null only for a club saved before FR-002.6 added the field
    public Instant getSubmittedAt() {
        return this.submittedAt;
    }

    public Student getFounder() {
        return this.founder;
    }

    public ClubStatus getStatus() {
        return this.status;
    }

    // Returns null if the club hasn't been approved yet
    public String getDateApproved() {
        return this.dateApproved == null ? null : this.dateApproved.toString();
    }

    public List<Member> getMembers() {
        return new ArrayList<>(this.members);
    }
}

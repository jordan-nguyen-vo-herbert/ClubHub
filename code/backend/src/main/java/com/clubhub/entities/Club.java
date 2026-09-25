package com.clubhub.entities;
// import com.clubhub.entities.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;



@Entity
public class Club {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long clubID;
    private String clubName;
    private Date dateApproved;
    private List<Member> members;
    // private Map<ClubRole, List<Member>> roleToMember;
    // Potential Fields
        // private int studentFounderID;
        // officers
        // private List<Events>;
    
    // 
    protected Club() {}

    public Club(Long clubID, String clubName, Date dateApproved, List<Member> members) {
        this.clubID = clubID;
        this.dateApproved = dateApproved;
        this.members = new ArrayList<>(members);
    }

    public void addMember(Long studentID, ClubRole role) {
        members.add(new Member(studentID, this.clubID, role)); // problem is creating a new member object everytime, maybe change
    }

    public Long getClubID() {
        return this.clubID;
    }

    public String getClubName() {
        return this.clubName;
    }


    public String getDateApproved() {
        return this.dateApproved.toString();
    }

    public List<Member> getMembers() {
        return new ArrayList<>(this.members);
    }
}

package com.clubhub.entities;
// import com.clubhub.entities.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;



@Entity
public class Club {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long clubID;
    private String clubName;
    private Date dateApproved;
    // mappedBy: the Member side owns the link (Member.club), cascade saves new members when the club is saved
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();
    // private Map<ClubRole, List<Member>> roleToMember;
    // Potential Fields
        // private int studentFounderID;
        // officers
        // private List<Events>;
    
    // 
    protected Club() {}

    // No id parameter, the database generates it, members are added with addMember
    public Club(String clubName, Date dateApproved) {
        this.clubName = clubName;
        this.dateApproved = dateApproved;
    }

    // Creates the membership, saved to the database when the club is saved
    public void addMember(Student student, ClubRole role) {
        members.add(new Member(student, this, role));
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

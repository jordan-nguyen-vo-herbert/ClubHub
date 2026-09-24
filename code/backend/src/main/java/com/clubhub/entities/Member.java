// Created a seperate Member class, so we only have to store ID number and club name, but might fine tune that later'

public class Member {
    private int studentID;
    private String clubName;
    // Might need roll

    public Member(int studentID, String clubName) {
        this.studentID = studentID;
        this.clubName = clubName;
    }
}

package com.example.events;

public class CreateUserEvent {
    private Integer id;

    private String major;

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    @Override
    public String toString(){
        return "Student{id: " + this.getId() + ", major: " + this.getMajor();
    }
}

package com.example.myapplication.model;

// Model class for donors table (MCA Lab: SQLite demo)
public class Donor {
    private long id;
    private String name;
    private int age;
    private String bloodGroup;
    private String phone;
    private String city;
    private boolean available;

    public Donor() {}

    public Donor(long id, String name, int age, String bloodGroup,
                 String phone, String city, boolean available) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.available = available;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

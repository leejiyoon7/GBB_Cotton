package com.example.cotton;

public class BookDateSaveForm {
    private String registerDate;
    private int rentCount;
    private String rentedMember;

    public BookDateSaveForm(String registerDate, int rentCount, String rentedMember)
    {
        this.registerDate = registerDate;
        this.rentCount = rentCount;
        this.rentedMember = rentedMember;
    }

    public String getRegisterDate() {
        return registerDate;
    }
    public void setRegisterDate(String registerDate)
    {
        this.registerDate = registerDate;
    }

    public int getRentCount()
    {
        return rentCount;
    }
    public void setRentCount()
    {
        this.rentCount = rentCount;
    }

    public void setRentCount(int rentCount) {
        this.rentCount = rentCount;
    }

    public String getRentedMember() {
        return rentedMember;
    }
}

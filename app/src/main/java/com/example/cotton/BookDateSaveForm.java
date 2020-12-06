package com.example.cotton;

public class BookDateSaveForm {
    private String registerDate;
    private int rentCount;

    public BookDateSaveForm(String registerDate, int rentCount)
    {
        this.registerDate = registerDate;
        this.rentCount = rentCount;
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
}

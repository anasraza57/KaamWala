package com.example.kaamwala;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MyServicesModel {
    private String myServiceId;
    private String serviceName;
    private String optionalAddress;
    private String optionalPhone;
    private Date timing;
    private String status;
    private String note;

    public MyServicesModel() {
    }

    public MyServicesModel(String myServiceId, String serviceName, String optionalAddress, String optionalPhone, Date timing, String status, String note) {
        this.myServiceId = myServiceId;
        this.serviceName = serviceName;
        this.optionalAddress = optionalAddress;
        this.optionalPhone = optionalPhone;
        this.timing = timing;
        this.status = status;
        this.note = note;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOptionalAddress() {
        return optionalAddress;
    }

    public void setOptionalAddress(String optionalAddress) {
        this.optionalAddress = optionalAddress;
    }

    public String getOptionalPhone() {
        return optionalPhone;
    }

    public void setOptionalPhone(String optionalPhone) {
        this.optionalPhone = optionalPhone;
    }

    public Date getTiming() {
        return timing;
    }

    public void setTiming(Date timing) {
        this.timing = timing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Exclude
    public String getMyServiceId() {
        return myServiceId;
    }

    public void setMyServiceId(String myServiceId) {
        this.myServiceId = myServiceId;
    }
}

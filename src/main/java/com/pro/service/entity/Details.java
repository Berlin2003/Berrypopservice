package com.pro.service.entity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "detail_table")
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private int id;

    @Column(name = "full_name")
    private String name;

    @Column(name = "email_address")
    private String email;

    @Column(name = "message_content")
    private String message;

    @Column(name = "creation_date")
    private LocalDateTime date;

    // Payment Enum with predefined values
    @Enumerated(EnumType.STRING)  // Store enum as a string in the database
    @Column(name = "payment_status")
    private Payment payment = Payment.NONE;  // Default value

    // Status Enum with predefined values
    @Enumerated(EnumType.STRING)
    @Column(name = "process_status")
    private Status status = Status.PROCESSING;  // Default value

    // Method to format date to "dd/MM/yyyy HH:mm" in IST
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        ZonedDateTime indiaTime = date.atZone(ZoneId.of("Asia/Kolkata"));
        return indiaTime.format(formatter);
    }

    // Automatically set the current date and time in IST before persisting the entity
    @PrePersist
    public void onPrePersist() {
        this.date = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    // Automatically set the current date and time in IST before updating the entity
    @PreUpdate
    public void onPreUpdate() {
        this.date = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    // Payment Enum
    public enum Payment {
        NONE, PENDING, COMPLETED
    }

    // Status Enum
    public enum Status {
        PROCESSING, COMPLETED
    }
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public String getMessage() {
		return message;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public Payment getPayment() {
		return payment;
	}
	public Status getStatus() {
		return status;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	

}

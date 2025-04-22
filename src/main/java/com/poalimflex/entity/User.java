package com.poalimflex.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("password_hash")
    private String passwordHash;

    @Field("phone_number")
    private String phoneNumber;

    @Field("registration_date")
    private LocalDate registrationDate;

    @Field("email_verified")
    private Boolean emailVerified;

    @Field("two_factor_enabled")
    private Boolean twoFactorEnabled;
}
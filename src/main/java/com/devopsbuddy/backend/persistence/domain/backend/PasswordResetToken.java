package com.devopsbuddy.backend.persistence.domain.backend;

import com.devopsbuddy.backend.persistence.converters.LocalDateTimeAttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by madgergely on 2017.03.04..
 */
@Entity
public class PasswordResetToken implements Serializable {

    /** The Serial Version UID for Serializable classes. */
    private static final long serialVersionUID = 1L;

    /** The application logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetToken.class);

    private static final int DEFAULT_TOKEN_EXPIRATION_LENGTH_MINUTES = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime expireDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, User user, LocalDateTime localDateTime, int expirationMinutes) {
        if ((null == token) || (null == user) || (null == localDateTime)) {
            throw new IllegalArgumentException("token, user and creation date time can't be null");
        }
        if (expirationMinutes == 0) {
            LOGGER.warn("The token expiration length in minutes is zero. Assigning the default value {}", DEFAULT_TOKEN_EXPIRATION_LENGTH_MINUTES);
            expirationMinutes = DEFAULT_TOKEN_EXPIRATION_LENGTH_MINUTES;
        }
        this.token = token;
        this.user = user;
        this.expireDate = localDateTime.plusMinutes(expirationMinutes);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PasswordResetToken that = (PasswordResetToken) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
